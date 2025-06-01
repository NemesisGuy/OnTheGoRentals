package za.ac.cput.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.security.RefreshToken;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.ResourceNotFoundException; // If roleRepository.findByRoleName might throw this
import za.ac.cput.exception.TokenRefreshException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IAuthService;
import za.ac.cput.service.IRefreshTokenService;
import za.ac.cput.service.IUserService;

import java.util.ArrayList; // For initializing roles list if needed
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AuthServiceImpl.java
 * Implementation of the {@link IAuthService} interface.
 * Handles user registration, login, token refresh, and logout operations,
 * including JWT generation/validation and cookie management for refresh tokens.
 * This service orchestrates actions using {@link IUserService}, {@link IRefreshTokenService},
 * and Spring Security components.
 *
 * Author: Peter Buckingham
 * Date: 2025-05-28
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@Service
@Transactional // Apply transactionality to public methods, especially registration
public class AuthServiceImpl implements IAuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final IUserService userService;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtilities jwtUtilities;
    private final IRefreshTokenService refreshTokenService;

    @Value("${jwt.refresh-token.expiration-ms}")
    private Long refreshTokenDurationMs;

    @Value("${app.security.refresh-cookie.name}")
    private String refreshTokenCookieName;

    @Value("${app.security.refresh-cookie.path}")
    private String refreshTokenCookiePath;

    @Value("${app.security.cookie.secure:true}") // Default to true, configurable
    private boolean secureCookie;

    /**
     * Constructs the AuthServiceImpl with necessary dependencies.
     *
     * @param userService         Service for user data operations (e.g., checking email existence, creating user).
     * @param roleRepository      Repository for role data access (e.g., finding default roles).
     * @param passwordEncoder     Encoder for hashing user passwords.
     * @param authenticationManager Spring's authentication manager for validating credentials.
     * @param jwtUtilities        Utility for JWT generation and cookie creation.
     * @param refreshTokenService Service for managing the lifecycle of refresh tokens.
     */
    @Autowired
    public AuthServiceImpl(IUserService userService,
                           IRoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtilities jwtUtilities,
                           IRefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtilities = jwtUtilities;
        this.refreshTokenService = refreshTokenService;
        log.info("AuthServiceImpl initialized. Secure cookie flag: {}, Refresh token cookie name: '{}', Path: '{}', Duration: {}ms",
                secureCookie, refreshTokenCookieName, refreshTokenCookiePath, refreshTokenDurationMs);
    }

    // --- Private Helper methods for cookie generation ---
    private ResponseCookie generateHttpOnlyRefreshTokenCookie(String tokenValue) {
        log.debug("Generating HTTP-only refresh token cookie. Name: '{}', Path: '{}', Duration: {}ms, Secure: {}",
                refreshTokenCookieName, refreshTokenCookiePath, refreshTokenDurationMs, secureCookie);
        return ResponseCookie.from(refreshTokenCookieName, tokenValue)
                .httpOnly(true)
                .secure(secureCookie)
                .path(refreshTokenCookiePath)
                .maxAge(TimeUnit.MILLISECONDS.toSeconds(refreshTokenDurationMs))
                .sameSite("Lax") // "Lax" is a good default for refresh tokens. "Strict" is more secure but might affect some cross-site scenarios.
                .build();
    }

    private ResponseCookie getCleanHttpOnlyRefreshTokenCookie() {
        log.debug("Generating clean (expiring) HTTP-only refresh token cookie. Name: '{}', Path: '{}', Secure: {}",
                refreshTokenCookieName, refreshTokenCookiePath, secureCookie);
        return ResponseCookie.from(refreshTokenCookieName, "") // Empty value
                .httpOnly(true)
                .secure(secureCookie)
                .path(refreshTokenCookiePath)
                .maxAge(0) // Expire immediately
                .sameSite("Lax")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    // In AuthServiceImpl.java
    @Override
    public User registerUser(String firstName, String lastName, String email, String plainPassword, RoleName defaultRoleName) {
        log.info("AuthService: Attempting to register new user with email: {}", email);
        if (userService.read(email) != null) { // Check via IUserService.read()
            log.warn("AuthService: Registration failed. Email '{}' already exists.", email);
            throw new EmailAlreadyExistsException("Email " + email + " is already taken!");
        }

        Role userRoleEntity = roleRepository.findByRoleName(defaultRoleName);
        if (userRoleEntity == null) {
            log.error("AuthService: Default role '{}' not found. This is a critical configuration error.", defaultRoleName);
            throw new IllegalStateException("Default role " + defaultRoleName + " not found.");
        }
        List<Role> rolesToAssign = Collections.singletonList(userRoleEntity);

        // Build the User object that will be passed to userService.createUser
        User userToCreateDetails = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(plainPassword) // Pass plain password to IUserService.createUser
                // IUserService.createUser is responsible for encoding
                // UUID, deleted, authProvider should be handled by User's @PrePersist or IUserService.createUser
                .build();

        // IUserService.createUser handles encoding, setting roles, and saving.
        User savedUser = userService.createUser(userToCreateDetails, rolesToAssign);

        log.info("AuthService: Successfully registered user. ID: {}, UUID: '{}', Email: '{}'",
                savedUser.getId(), savedUser.getUuid(), savedUser.getEmail());
        return savedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthDetails loginUser(String email, String plainPassword, HttpServletResponse httpServletResponse) {
        log.info("AuthService: Attempting to authenticate user with email: '{}'", email);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, plainPassword)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("AuthService: Spring Security Authentication successful for '{}'.", email);

            // Fetch the full User entity using IUserService
            User user = userService.read(authentication.getName()); // authentication.getName() is usually the username (email)
            if (user == null) {
                // This state (authenticated by AuthenticationManager but not found by userService.read)
                // indicates a potential data inconsistency or issue with UserDetailsService logic if separate.
                log.error("AuthService: CRITICAL - Authenticated user '{}' was not found by IUserService.read(). " +
                        "This might indicate a data consistency issue or UserDetailsService discrepancy.", authentication.getName());
                throw new UsernameNotFoundException("User details for authenticated principal '" + authentication.getName() + "' not found in system after successful Spring Security authentication.");
            }

            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getRoleName) // Uses Role.getRoleName() which returns String
                    .collect(Collectors.toList());

            String accessToken = jwtUtilities.generateToken(user, roleNames);
            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId()); // Manages DB persistence

            ResponseCookie refreshTokenCookie = generateHttpOnlyRefreshTokenCookie(refreshTokenEntity.getToken());
            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            log.info("AuthService: Successfully authenticated user: '{}'. Access token generated and refresh token cookie set.", user.getEmail());
            return new AuthDetails(user, accessToken, roleNames);

        } catch (BadCredentialsException e) {
            log.warn("AuthService: Authentication failed for user '{}': Invalid credentials.", email);
            throw e; // Re-throw for the controller/global handler to manage the HTTP response
        } catch (AuthenticationException e) { // Catch other authentication-related issues
            log.warn("AuthService: Authentication attempt failed for user '{}': {}", email, e.getMessage());
            throw e; // Re-throw
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefreshedTokenDetails refreshAccessToken(String refreshTokenFromCookie, HttpServletResponse httpServletResponse) {
        log.info("AuthService: Attempting to refresh access token using refresh token from cookie.");
        // Avoid logging the full token string for security.
        // log.debug("AuthService: Received refresh token from cookie (partial for logging): {}...",
        //           refreshTokenFromCookie != null && refreshTokenFromCookie.length() > 10 ?
        //           refreshTokenFromCookie.substring(0, 10) : refreshTokenFromCookie);

        return refreshTokenService.findByToken(refreshTokenFromCookie)
                .map(refreshTokenEntity -> { // Renamed for clarity
                    log.debug("AuthService: Found refresh token entity (ID: {}) for user ID: {}",
                            refreshTokenEntity.getId(), refreshTokenEntity.getUser().getId());
                    return refreshTokenService.verifyDeviceAndExpiration(refreshTokenEntity); // Throws if invalid/expired
                })
                .map(validRefreshTokenEntity -> { // Renamed for clarity
                    User user = validRefreshTokenEntity.getUser();
                    log.debug("AuthService: User ID: {} (Email: '{}') verified for token refresh. Proceeding to rotate refresh token.",
                            user.getId(), user.getEmail());

                    refreshTokenService.deleteByToken(refreshTokenFromCookie); // Invalidate (delete) the used refresh token
                    log.debug("AuthService: Old refresh token (ending ...{}) invalidated/deleted from DB.",
                            refreshTokenFromCookie.substring(Math.max(0, refreshTokenFromCookie.length() - 6)));

                    RefreshToken newRefreshTokenEntity = refreshTokenService.createRefreshToken(user.getId()); // Create a new one
                    ResponseCookie newRefreshTokenCookie = generateHttpOnlyRefreshTokenCookie(newRefreshTokenEntity.getToken());
                    httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString());
                    log.info("AuthService: New refresh token cookie set for user ID: {}.", user.getId());

                    List<String> roleNames = user.getRoles().stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList());
                    String newAccessToken = jwtUtilities.generateToken(user, roleNames);
                    log.info("AuthService: New access token generated for user ID: {}.", user.getId());

                    return new RefreshedTokenDetails(newAccessToken);
                })
                .orElseThrow(() -> {
                    log.warn("AuthService: Refresh token from cookie not found in database, is invalid, or has expired.");
                    return new TokenRefreshException(refreshTokenFromCookie, "Refresh token is not in database or is invalid/expired!");
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean logoutUser(int userId, HttpServletResponse httpServletResponse) {
        log.info("AuthService: Performing logout for user ID: {}", userId);
        try {
            // Step 1: Invalidate server-side refresh token(s) for the user
            refreshTokenService.deleteByUserId(userId);
            log.debug("AuthService: Server-side refresh tokens potentially invalidated for user ID: {}.", userId);

            // Step 2: Instruct client to clear its authentication cookies
            clearAuthCookies(httpServletResponse);

            // Step 3: Clear the Spring Security context for the current request
            SecurityContextHolder.clearContext();
            log.debug("AuthService: Spring Security context cleared for current request.");

            log.info("AuthService: Logout process completed for user ID: {}.", userId);
            return true;
        } catch (Exception e) {
            log.error("AuthService: Error during logout process for user ID: {}. Attempting cookie cleanup as a fallback.", userId, e);
            // Attempt to clear cookies even if other steps (like DB delete of RT) fail
            try {
                clearAuthCookies(httpServletResponse);
            } catch (Exception cookieEx) {
                log.error("AuthService: Further error while attempting to clear cookies during logout failure for user ID: {}:", userId, cookieEx);
            }
            return false; // Indicate logout might not have fully completed as expected.
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAuthCookies(HttpServletResponse httpServletResponse) {
        log.debug("AuthService: Preparing to add headers to clear authentication cookies.");
        ResponseCookie cleanRtCookie = getCleanHttpOnlyRefreshTokenCookie();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cleanRtCookie.toString());
        // If an access token cookie is also used (less common for JWT access tokens in body), clear it too:
        // ResponseCookie cleanAtCookie = getCleanHttpOnlyAccessTokenCookie(); // Define this helper if needed
        // httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cleanAtCookie.toString());
        log.debug("AuthService: Headers added to HTTP response to instruct client to clear refresh token cookie.");
    }

    // --- Helper classes for returning structured data (internal to this service) ---
    // These are not web DTOs but internal data carriers.
    // The controller layer is responsible for mapping data from these into web DTOs.

    /**
     * Internal helper class to hold data related to a successful authentication event.
     * Contains the authenticated {@link User} entity, the generated JWT access token string,
     * and a list of the user's role names (as strings). This object is returned by
     * the authentication service method to the controller, which then maps it to a response DTO.
     */
    public static class AuthDetails {
        private final User user;
        private final String accessToken;
        private final List<String> roleNames;

        public AuthDetails(User user, String accessToken, List<String> roleNames) {
            this.user = user;
            this.accessToken = accessToken;
            this.roleNames = roleNames != null ? List.copyOf(roleNames) : Collections.emptyList();
        }
        public User getUser() { return user; }
        public String getAccessToken() { return accessToken; }
        public List<String> getRoleNames() { return roleNames; }
    }

    /**
     * Internal helper class to hold the new JWT access token string after a successful token refresh operation.
     * This object is returned by the token refresh service method to the controller.
     */
    public static class RefreshedTokenDetails {
        private final String newAccessToken;
        public RefreshedTokenDetails(String newAccessToken) { this.newAccessToken = newAccessToken; }
        public String getNewAccessToken() { return newAccessToken; }
    }
}