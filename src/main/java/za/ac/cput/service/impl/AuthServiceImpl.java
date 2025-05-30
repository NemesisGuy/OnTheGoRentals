package za.ac.cput.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; // Explicitly add if not already
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
import za.ac.cput.exception.TokenRefreshException;
import za.ac.cput.repository.IRoleRepository;
// Assuming IUserRepository is your user repository interface
// import za.ac.cput.repository.IUserRepository;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IAuthService;
import za.ac.cput.service.IRefreshTokenService;
import za.ac.cput.service.IUserService; // To interact with User CRUD

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
 *
 * Author: Peter Buckingham
 * Date: 2025-05-28
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service
@Transactional // Apply transactionality to public methods
public class AuthServiceImpl implements IAuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final IUserService userService; // For user creation and retrieval
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

    @Value("${app.security.cookie.secure:true}")
    private boolean secureCookie;

    /**
     * Constructs the AuthServiceImpl with necessary dependencies.
     *
     * @param userService         Service for user data operations.
     * @param roleRepository      Repository for role data access.
     * @param passwordEncoder     Encoder for user passwords.
     * @param authenticationManager Spring's authentication manager.
     * @param jwtUtilities        Utility for JWT operations.
     * @param refreshTokenService Service for refresh token management.
     */
    @Autowired // Good practice for constructor injection
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
        log.info("AuthServiceImpl initialized. Secure cookie flag: {}", secureCookie);
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
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie getCleanHttpOnlyRefreshTokenCookie() {
        log.debug("Generating clean (expiring) HTTP-only refresh token cookie. Name: '{}', Path: '{}', Secure: {}",
                refreshTokenCookieName, refreshTokenCookiePath, secureCookie);
        return ResponseCookie.from(refreshTokenCookieName, "")
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
    @Override
    public User registerUser(String firstName, String lastName, String email, String plainPassword, RoleName defaultRoleName) {
        log.info("AuthService: Attempting to register new user with email: {}", email);
        if (userService.read(email) != null) {
            log.warn("AuthService: Registration failed. Email '{}' already exists.", email);
            throw new EmailAlreadyExistsException("Email " + email + " is already taken!");
        }

        User user = new User();
        // User's UUID will be set by userService.saveUser if not already set by @PrePersist or other means.
        // Or explicitly:
        if (user.getUuid() == null) {
            user.setUuid(UUID.randomUUID());
        }
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setDeleted(false);

        Role userRole = roleRepository.findByRoleName(RoleName.USER); // Use .name() for enum
        if (userRole == null) {
            log.error("AuthService: Default role '{}' not found for registration. This is a critical configuration error.", defaultRoleName);
            throw new IllegalStateException("Default role " + defaultRoleName + " not found. Please ensure roles are seeded/configured in the database.");
        }
        user.setRoles(Collections.singletonList(userRole));

        // Use IUserService to create the user with roles (if IUserService.createUser handles roles)
        // or save the user then assign roles.
        // Assuming IUserService.saveUser is a generic save.
        // If IUserService has a specific createUser(User userDetails, List<Role> roles), use that.
        // For now, using the generic userService.saveUser which assumes roles are already set on 'user' object.
        User savedUser = userService.saveUser(user); // Pass the fully prepared user
        log.info("AuthService: Successfully registered user. ID: {}, UUID: '{}', Email: '{}'",
                savedUser.getId(), savedUser.getUuid(), savedUser.getEmail());
        return savedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthDetails loginUser(String email, String plainPassword, HttpServletResponse httpServletResponse) {
        log.info("AuthService: Attempting to authenticate user with email: {}", email);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, plainPassword)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.read(authentication.getName());
            if (user == null) {
                log.error("AuthService: CRITICAL - Authenticated user '{}' not found by userService. Data inconsistency or UserDetailsService issue.", authentication.getName());
                throw new UsernameNotFoundException("User details for authenticated principal '" + authentication.getName() + "' not found in system.");
            }

            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getRoleName) // Assuming Role entity has getRoleName() returning String
                    .collect(Collectors.toList());

            String accessToken = jwtUtilities.generateToken(user, roleNames);
            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());

            ResponseCookie refreshTokenCookie = generateHttpOnlyRefreshTokenCookie(refreshTokenEntity.getToken());
            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            log.info("AuthService: Successfully authenticated user: '{}'. Access token generated and refresh token cookie set.", user.getEmail());
            return new AuthDetails(user, accessToken, roleNames);

        } catch (BadCredentialsException e) {
            log.warn("AuthService: Authentication failed for user '{}': Invalid credentials.", email);
            throw e;
        } catch (AuthenticationException e) {
            log.warn("AuthService: Authentication failed for user '{}': {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefreshedTokenDetails refreshAccessToken(String refreshTokenFromCookie, HttpServletResponse httpServletResponse) {
        log.info("AuthService: Attempting to refresh access token using refresh token from cookie.");
        // Avoid logging the full token value.
        // log.debug("AuthService: Received refresh token from cookie (partial): {}...", refreshTokenFromCookie.substring(0, Math.min(10, refreshTokenFromCookie.length())));


        return refreshTokenService.findByToken(refreshTokenFromCookie)
                .map(refreshTokenEntity -> {
                    log.debug("AuthService: Found refresh token entity (ID: {}) for user ID: {}", refreshTokenEntity.getId(), refreshTokenEntity.getUser().getId());
                    return refreshTokenService.verifyDeviceAndExpiration(refreshTokenEntity);
                })
                .map(validRefreshTokenEntity -> {
                    User user = validRefreshTokenEntity.getUser();
                    log.debug("AuthService: User ID: {} (Email: '{}') verified for token refresh. Proceeding to rotate refresh token.", user.getId(), user.getEmail());

                    refreshTokenService.deleteByToken(refreshTokenFromCookie); // Invalidate (delete) the used refresh token
                    log.debug("AuthService: Old refresh token (ending ...{}) invalidated from DB.", refreshTokenFromCookie.substring(Math.max(0, refreshTokenFromCookie.length() - 6)));

                    RefreshToken newRefreshTokenEntity = refreshTokenService.createRefreshToken(user.getId()); // Create a new one
                    ResponseCookie newRefreshTokenCookie = generateHttpOnlyRefreshTokenCookie(newRefreshTokenEntity.getToken());
                    httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString());
                    log.info("AuthService: New refresh token cookie set for user ID: {}", user.getId());

                    List<String> roleNames = user.getRoles().stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList());
                    String newAccessToken = jwtUtilities.generateToken(user, roleNames);
                    log.info("AuthService: New access token generated for user ID: {}", user.getId());

                    return new RefreshedTokenDetails(newAccessToken);
                })
                .orElseThrow(() -> {
                    log.warn("AuthService: Refresh token from cookie not found in database or is invalid/expired.");
                    return new TokenRefreshException(refreshTokenFromCookie, "Refresh token is not in database or invalid!");
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean logoutUser(int userId, HttpServletResponse httpServletResponse) {
        log.info("AuthService: Performing logout for user ID: {}", userId);
        try {
            refreshTokenService.deleteByUserId(userId);
            log.debug("AuthService: Server-side refresh tokens invalidated for user ID: {}.", userId);

            clearAuthCookies(httpServletResponse);

            SecurityContextHolder.clearContext();
            log.debug("AuthService: Security context cleared for user ID: {}.", userId);

            log.info("AuthService: Logout process completed successfully for user ID: {}.", userId);
            return true;
        } catch (Exception e) {
            log.error("AuthService: Error during logout process for user ID: {}. Attempting cookie cleanup as fallback.", userId, e);
            try {
                clearAuthCookies(httpServletResponse);
            } catch (Exception cookieEx) {
                log.error("AuthService: Further error while attempting to clear cookies during logout failure for user ID: {}:", userId, cookieEx);
            }
            return false; // Indicate logout might not have fully completed.
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAuthCookies(HttpServletResponse httpServletResponse) {
        log.debug("AuthService: Preparing to clear authentication cookies.");
        ResponseCookie cleanRtCookie = getCleanHttpOnlyRefreshTokenCookie();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cleanRtCookie.toString());
        // If an access token cookie is also used (less common for JWT access tokens in body):
        // ResponseCookie cleanAtCookie = getCleanHttpOnlyAccessTokenCookie(); // Define this helper if needed in JwtUtilities
        // httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cleanAtCookie.toString());
        log.debug("AuthService: Headers added to response to clear refresh token cookie.");
    }

    // --- Helper classes for returning structured data (internal to this service) ---
    // These are not web DTOs but internal data carriers.
    // The controller layer maps data from these into web DTOs.

    /**
     * Internal helper class to hold data related to a successful authentication.
     * Contains the authenticated {@link User} entity, the generated access token string,
     * and a list of the user's role names.
     */
    public static class AuthDetails {
        private final User user;
        private final String accessToken;
        private final List<String> roleNames;

        public AuthDetails(User user, String accessToken, List<String> roleNames) {
            this.user = user;
            this.accessToken = accessToken;
            this.roleNames = roleNames;
        }
        public User getUser() { return user; }
        public String getAccessToken() { return accessToken; }
        public List<String> getRoleNames() { return roleNames; }
    }

    /**
     * Internal helper class to hold the new access token string after a successful token refresh.
     */
    public static class RefreshedTokenDetails {
        private final String newAccessToken;
        public RefreshedTokenDetails(String newAccessToken) { this.newAccessToken = newAccessToken; }
        public String getNewAccessToken() { return newAccessToken; }
    }
}