package za.ac.cput.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; // For specific login failure
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException; // General authentication exception
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.dto.response.TokenRefreshResponseDto;
import za.ac.cput.domain.entity.security.RefreshToken;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.exception.TokenRefreshException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository; // Corrected name
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IRefreshTokenService;
import za.ac.cput.service.IUserService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * UserServiceImpl.java
 * Implementation of the IUserService interface, handling user management,
 * authentication, token generation, and cookie management.
 * This service operates with domain entities and primitives, leaving DTO mapping
 * and ResponseEntity construction to the controller layer.
 *
 * Author: [Original Author Name - Please specify if known]
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service
@Transactional
public class UserServiceImpl implements IUserService { // Renamed from UserService for clarity

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository; // Corrected from iUserRepository
    private final IRoleRepository roleRepository; // Corrected from iRoleRepository
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilities jwtUtilities;
    private final IRefreshTokenService refreshTokenService;

    // @Value("${jwt.expiration}") // This is used for AuthResponseDto in controller
    // private Long accessTokenExpirationMs; // Not directly needed in service if controller builds DTO

    @Value("${jwt.refresh-token.expiration-ms}")
    private Long refreshTokenDurationMs;

    @Value("${app.security.refresh-cookie.name}")
    private String refreshTokenCookieName;

    @Value("${app.security.refresh-cookie.path}")
    private String refreshTokenCookiePath;

    /**
     * Constructs the UserServiceImpl with necessary dependencies.
     */
    public UserServiceImpl(AuthenticationManager authenticationManager, IUserRepository userRepository,
                           IRoleRepository roleRepository, PasswordEncoder passwordEncoder,
                           JwtUtilities jwtUtilities, IRefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtilities = jwtUtilities;
        this.refreshTokenService = refreshTokenService;
        log.info("UserServiceImpl initialized.");
    }

    // --- Helper methods for cookie generation ---

    private ResponseCookie generateRefreshTokenCookie(String tokenValue) {
        log.debug("Generating refresh token cookie with name: {}, path: {}, durationMs: {}",
                refreshTokenCookieName, refreshTokenCookiePath, refreshTokenDurationMs);
        return ResponseCookie.from(refreshTokenCookieName, tokenValue)
                .httpOnly(true)
                .secure(true) // TODO: Make this configurable based on environment (true in prod)
                .path(refreshTokenCookiePath)
                .maxAge(TimeUnit.MILLISECONDS.toSeconds(refreshTokenDurationMs))
                .sameSite("Lax") // Or "Strict"
                .build();
    }

    private ResponseCookie getCleanRefreshTokenCookie() {
        log.debug("Generating clean (expiring) refresh token cookie with name: {}, path: {}",
                refreshTokenCookieName, refreshTokenCookiePath);
        return ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(true)
                .secure(true) // TODO: Make configurable
                .path(refreshTokenCookiePath)
                .maxAge(0) // Expire immediately
                .sameSite("Lax")
                .build();
    }

    // --- IUserService Implementation ---

    @Override
    public User registerUser(String firstName, String lastName, String email, String plainPassword, RoleName defaultRoleName) {
        log.info("Attempting to register new user with email: {}", email);
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: Email {} already exists.", email);
            throw new IllegalArgumentException("Email " + email + " is already taken!");
        }

        User user = new User();
        user.setUuid(UUID.randomUUID()); // Ensure UUID is set
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setDeleted(false); // Explicitly set non-deleted

        Role userRole = roleRepository.findByRoleName(defaultRoleName);
        if (userRole == null) {
            log.error("Default role {} not found in database during registration for email: {}. This is a configuration issue.", defaultRoleName, email);
            // This is a server configuration error, should not happen in a well-configured system
            throw new IllegalStateException("Default role " + defaultRoleName + " not found. Please configure roles.");
        }
        user.setRoles(Collections.singletonList(userRole));
        User savedUser = userRepository.save(user);
        log.info("Successfully registered user ID: {}, UUID: {}, Email: {}", savedUser.getId(), savedUser.getUuid(), savedUser.getEmail());
        return savedUser;
    }

    @Override
    public AuthenticatedUserDetails authenticateUser(String email, String plainPassword, HttpServletResponse httpServletResponse) {
        log.info("Attempting to authenticate user with email: {}", email);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, plainPassword)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> {
                        // This should ideally not happen if authenticationManager succeeded
                        log.error("Authenticated user {} not found in repository after successful authentication manager step.", authentication.getName());
                        return new UsernameNotFoundException("User not found with email: " + authentication.getName() + " despite authentication success.");
                    });

            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getRoleName) // Assuming Role has getRoleName() returning String
                    .collect(Collectors.toList());

            String accessToken = jwtUtilities.generateToken(user, roleNames);
            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());

            ResponseCookie refreshTokenCookie = generateRefreshTokenCookie(refreshTokenEntity.getToken());
            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            log.info("Successfully authenticated user: {}. Access and refresh tokens generated. Refresh token cookie set.", user.getEmail());

            return new AuthenticatedUserDetails(user, accessToken, roleNames);

        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user {}: Invalid credentials.", email);
            throw e; // Re-throw for controller/advice to handle
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user {}: {}", email, e.getMessage());
            throw e; // Re-throw for controller/advice to handle
        }
    }

    @Override
    public RefreshedAccessTokenDetails refreshAccessToken(String refreshTokenFromCookie, HttpServletResponse httpServletResponse) {
        log.info("Attempting to refresh access token using refresh token from cookie.");
        // Refresh token string itself is sensitive, avoid logging its full value.
        // log.debug("Received refresh token from cookie (partial for logging): {}...", refreshTokenFromCookie.substring(0, Math.min(refreshTokenFromCookie.length(), 10)));

        return refreshTokenService.findByToken(refreshTokenFromCookie)
                .map(refreshTokenEntity -> {
                    log.debug("Found refresh token entity for user ID: {}", refreshTokenEntity.getUser().getId());
                    return refreshTokenService.verifyDeviceAndExpiration(refreshTokenEntity);
                })
                .map(validRefreshTokenEntity -> {
                    User user = validRefreshTokenEntity.getUser();
                    log.debug("User ID: {} verified for token refresh.", user.getId());

                    // It's common practice to rotate refresh tokens upon use for better security
                    refreshTokenService.deleteByToken(refreshTokenFromCookie); // Delete the used refresh token
                    log.debug("Old refresh token (ending with ...{}) deleted from DB.", refreshTokenFromCookie.substring(Math.max(0, refreshTokenFromCookie.length() - 6)));

                    RefreshToken newRefreshTokenEntity = refreshTokenService.createRefreshToken(user.getId()); // Create a new one
                    ResponseCookie newRefreshTokenCookie = generateRefreshTokenCookie(newRefreshTokenEntity.getToken());
                    httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString());
                    log.info("New refresh token cookie set for user ID: {}", user.getId());

                    List<String> roleNames = user.getRoles().stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList());
                    String newAccessToken = jwtUtilities.generateToken(user, roleNames);
                    log.info("New access token generated for user ID: {}", user.getId());

                    return new RefreshedAccessTokenDetails(newAccessToken);
                })
                .orElseThrow(() -> {
                    log.warn("Refresh token from cookie not found in database or is invalid/expired.");
                    return new TokenRefreshException(refreshTokenFromCookie, "Refresh token is not in database or invalid!");
                });
    }


    @Override
    public boolean performLogoutAndClearCookie(int userId, HttpServletResponse httpServletResponse) {
        log.info("Performing logout for user ID: {}", userId);
        try {
            // Invalidate server-side refresh token(s) for the user
            refreshTokenService.deleteByUserId(userId);
            log.debug("Server-side refresh tokens invalidated for user ID: {}", userId);

            // Clear the refresh token cookie on the client-side
            clearCookies(httpServletResponse);

            // Clear the Spring Security context
            SecurityContextHolder.clearContext();
            log.debug("Security context cleared for user ID: {}", userId);

            log.info("Logout successful for user ID: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Error during logout for user ID: {}. Attempting to clear cookies as a fallback.", userId, e);
            // Attempt to clear cookies even if other steps fail
            try {
                clearCookies(httpServletResponse);
            } catch (Exception cookieEx) {
                log.error("Error while attempting to clear cookies during logout failure for user ID: {}", userId, cookieEx);
            }
            return false; // Indicate that logout might not have completed fully
        }
    }

    @Override
    public void clearCookies(HttpServletResponse httpServletResponse) {
        log.debug("Clearing authentication cookies.");
        ResponseCookie cleanRtCookie = getCleanRefreshTokenCookie();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cleanRtCookie.toString());
        // If you also have an access token cookie (less common for JWTs, but possible):
        // ResponseCookie cleanAtCookie = getCleanAccessTokenCookie(); // Assuming this helper exists
        // httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cleanAtCookie.toString());
        log.debug("Refresh token cookie clearing header added to response.");
    }


    @Override
    public AuthResponseDto authenticateAndGenerateTokens(LoginDto loginDto, HttpServletResponse response) {
        return null;
    }

    @Override
    public String authenticate(LoginDto loginDto) {
        return "";
    }

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {
        return null;
    }

    // --- Standard CRUD and other user methods ---
    @Override
    public Role saveRole(Role role) {
        log.debug("Saving role: {}", role.getRoleName());
        return roleRepository.save(role);
    }

    @Override
    public User saverUser(User user) {
        return null;
    }

    @Override
    public ResponseEntity<String> logoutUserAndClearCookie(Integer userId, HttpServletResponse response) {
        return null;
    }

    @Override
    public ResponseEntity<String> logoutUserAndClearCookie(UUID userId, HttpServletResponse response) {
        return null;
    }

    @Override
    public User saveUser(User user) { // Renamed from saverUser
        if (user.getId() == null && user.getUuid() == null) {
            user.setUuid(UUID.randomUUID());
            log.debug("Generated new UUID: {} for user being saved.", user.getUuid());
        }
        // Password should be encoded before calling this method if it's a new user or password change.
        // This method is a generic save. For create, use createUser. For update, ensure password logic.
        log.debug("Saving user ID: {}, UUID: {}, Email: {}", user.getId(), user.getUuid(), user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        log.debug("Fetching all non-deleted users.");
        return userRepository.findByDeletedFalse();
    }

    @Override
    public User create(User user) {
        // This is a more generic create, assumes password already encoded if necessary.
        // Prefer using 'createUser(User user, List<Role> roles)' for robust creation with roles.
        log.warn("Generic create(User) called. Ensure password is pre-encoded and roles are set. Email: {}", user.getEmail());
        if (user.getUuid() == null) {
            user.setUuid(UUID.randomUUID());
        }
        user.setDeleted(false);
        return userRepository.save(user);
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    @Transactional // Ensure transactionality for consistent user and role creation
    public User createUser(User userDetails, List<Role> roles) {
        log.info("Creating new user with email: {} and roles: {}", userDetails.getEmail(), roles.stream().map(Role::getRoleName).collect(Collectors.toList()));
        if (userRepository.existsByEmail(userDetails.getEmail())) {
            log.warn("User creation failed: Email {} already exists.", userDetails.getEmail());
            throw new IllegalArgumentException("Email " + userDetails.getEmail() + " is already taken!");
        }
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        userDetails.setRoles(roles); // Set the pre-resolved and validated roles
        if (userDetails.getUuid() == null) {
            userDetails.setUuid(UUID.randomUUID());
        }
        userDetails.setDeleted(false);
        User savedUser = userRepository.save(userDetails);
        log.info("Successfully created user ID: {}, UUID: {}, Email: {}", savedUser.getId(), savedUser.getUuid(), savedUser.getEmail());
        return savedUser;
    }

    @Override
    public TokenRefreshResponseDto refreshToken(String refreshTokenFromCookie, HttpServletResponse httpServletResponse) {
        return null;
    }


    @Override
    public User read(Integer id) {
        log.debug("Reading user by internal ID: {}", id);
        return userRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public User read(UUID uuid) {
        log.debug("Reading user by UUID: {}", uuid);
        return userRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

    @Override
    public User read(String email) {
        log.debug("Reading user by email: {}", email);
        return userRepository.findByEmailAndDeletedFalse(email).orElse(null);
    }

    @Override
    public User update(Integer id, User user) {
        return null;
    }

    @Override
    public ResponseEntity<AuthResponseDto> registerAndReturnAuthResponse(RegisterDto registerDto) {
        return null;
    }

    @Override
    public User update(int userId, User userUpdates) {
        log.info("Attempting to update user with internal ID: {}", userId);
        User existingUser = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for update with ID: {}", userId);
                    return new ResourceNotFoundException("User not found for update with id: " + userId);
                });

        log.debug("Found existing user for update: ID: {}, Email: {}", existingUser.getId(), existingUser.getEmail());

        // Apply updates selectively
        if (userUpdates.getFirstName() != null) {
            existingUser.setFirstName(userUpdates.getFirstName());
        }
        if (userUpdates.getLastName() != null) {
            existingUser.setLastName(userUpdates.getLastName());
        }
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equals(existingUser.getEmail())) {
            // Handle email change carefully: check for uniqueness if it's a unique field
            if (userRepository.existsByEmail(userUpdates.getEmail())) {
                log.warn("Update failed for user ID {}: New email {} already exists.", userId, userUpdates.getEmail());
                throw new IllegalArgumentException("Email " + userUpdates.getEmail() + " is already taken by another user.");
            }
            existingUser.setEmail(userUpdates.getEmail());
        }
        if (userUpdates.getPassword() != null && !userUpdates.getPassword().isEmpty()) {
            // Only encode if it's a new plain text password
            if (!passwordEncoder.matches("", userUpdates.getPassword()) && !userUpdates.getPassword().startsWith("$2a$")) { // Basic check if it's already encoded
                log.debug("Encoding new password for user ID: {}", userId);
                existingUser.setPassword(passwordEncoder.encode(userUpdates.getPassword()));
            } else if (userUpdates.getPassword().startsWith("$2a$")) {
                log.debug("Password for user ID {} appears to be already encoded, using as is (this should ideally not happen if DTO takes plain password).", userId);
                existingUser.setPassword(userUpdates.getPassword()); // Use with caution
            }
        }
        if (userUpdates.getRoles() != null && !userUpdates.getRoles().isEmpty()) {
            // Ensure roles are managed entities if they are being updated.
            // The controller should resolve role names to Role entities before calling this.
            existingUser.setRoles(userUpdates.getRoles());
            log.debug("Updating roles for user ID: {} to: {}", userId, userUpdates.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));
        }
        // Do not update UUID. Do not update 'deleted' flag here (use delete method for that).

        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated user ID: {}, UUID: {}, Email: {}", updatedUser.getId(), updatedUser.getUuid(), updatedUser.getEmail());
        return updatedUser;
    }

    @Override
    public boolean delete(int userId) {
        log.info("Attempting to soft-delete user with internal ID: {}", userId);
        return userRepository.findByIdAndDeletedFalse(userId).map(user -> {
            user.setDeleted(true);
            userRepository.save(user);
            refreshTokenService.deleteByUserId(userId); // Also delete refresh tokens on user soft delete
            log.info("Successfully soft-deleted user ID: {} and associated refresh tokens.", userId);
            return true;
        }).orElseGet(() -> {
            log.warn("User not found for soft-deletion with ID: {}", userId);
            return false;
        });
    }

    // --- Helper classes for returning structured data from service (internal to service package or domain) ---
    // These are NOT DTOs for web layer, but simple holders for multiple return values from a service method.
    // The controller will map these to actual ResponseDTOs.

    /**
     * Internal helper class to hold authenticated user details and access token.
     * This is returned by the authentication service method to the controller.
     */
    public static class AuthenticatedUserDetails {
        private final User user;
        private final String accessToken;
        private final List<String> roleNames;

        public AuthenticatedUserDetails(User user, String accessToken, List<String> roleNames) {
            this.user = user;
            this.accessToken = accessToken;
            this.roleNames = roleNames;
        }

        public User getUser() { return user; }
        public String getAccessToken() { return accessToken; }
        public List<String> getRoleNames() { return roleNames; }
    }

    /**
     * Internal helper class to hold the new access token after a successful refresh.
     */
    public static class RefreshedAccessTokenDetails {
        private final String newAccessToken;

        public RefreshedAccessTokenDetails(String newAccessToken) {
            this.newAccessToken = newAccessToken;
        }

        public String getNewAccessToken() { return newAccessToken; }
    }
}