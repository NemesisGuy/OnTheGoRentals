package za.ac.cput.controllers.security;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // For JWT expiration
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.api.response.ApiResponse;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.dto.response.TokenRefreshResponseDto;
import za.ac.cput.domain.entity.security.RoleName; // For default role in registration
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.service.IAuthService; // Import new IAuthService
import za.ac.cput.service.impl.AuthServiceImpl; // To access nested DTO-like classes
import za.ac.cput.utils.SecurityUtils;

import java.util.List; // For roles in AuthResponseDto

/**
 * AuthController.java
 * Controller for handling user authentication and authorization processes.
 * Utilizes {@link IAuthService} for core authentication logic.
 * Includes endpoints for user registration, login, token refresh, and logout.
 *
 * Author: [Original Author Name - Please specify if known]
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final IAuthService authService; // Inject IAuthService

    @Value("${jwt.expiration}") // To populate AuthResponseDto
    private Long accessTokenExpirationMs;

    /**
     * Constructs an AuthController with the necessary Authentication service.
     *
     * @param authService The service implementation for authentication operations.
     */
    @Autowired
    public AuthController(IAuthService authService) {
        this.authService = authService;
        log.info("AuthController initialized with IAuthService.");
    }

    /**
     * Registers a new user in the system.
     *
     * @param registerDto The {@link RegisterDto} containing user registration details.
     * @return A ResponseEntity containing an {@link AuthResponseDto} with the access token
     *         and user information upon successful registration. Cookies for refresh token
     *         are set by the AuthService via HttpServletResponse.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterDto registerDto,
            HttpServletResponse httpServletResponse // Pass to service for cookie setting
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier(); // Will be GUEST
        log.info("Requester [{}]: Attempting to register new user with email: {}", requesterId, registerDto.getEmail());

        // AuthService's registerUser method now handles User creation, password encoding, default role assignment.
        // We call loginUser afterwards to get tokens and set cookies, simulating an auto-login after registration.
        User registeredUser = authService.registerUser(
                registerDto.getFirstName(),
                registerDto.getLastName(),
                registerDto.getEmail(),
                registerDto.getPassword(),
                RoleName.USER // Default role for new registrations
        );

        log.info("Requester [{}]: User '{}' registered successfully. Proceeding to log in.", requesterId, registeredUser.getEmail());

        // After successful registration, automatically log the user in to get tokens/cookies
        AuthServiceImpl.AuthDetails authDetails = authService.loginUser(
                registeredUser.getEmail(), // Use registered email
                registerDto.getPassword(), // Use plain password from DTO for login
                httpServletResponse
        );

        AuthResponseDto authResponseDto = new AuthResponseDto(
                authDetails.getAccessToken(),
                "Bearer",
                accessTokenExpirationMs,
                authDetails.getUser().getEmail(),
                authDetails.getRoleNames()
        );

        log.info("Requester [{}]: Successfully registered and logged in user: {}. Response DTO created.",
                requesterId, authDetails.getUser().getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDto);
    }

    /**
     * Authenticates an existing user and provides authentication tokens.
     * Access token is returned in the response body; refresh token is set as an HTTP-only cookie.
     *
     * @param loginDto            The {@link LoginDto} containing user login credentials.
     * @param httpServletResponse The HttpServletResponse used by the auth service to set the refresh token cookie.
     * @return A ResponseEntity containing an {@link AuthResponseDto} with the access token and user info.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginDto loginDto,
            HttpServletResponse httpServletResponse
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier(); // Will be GUEST
        log.info("Requester [{}]: Attempting to login user with email: {}", requesterId, loginDto.getEmail());

        AuthServiceImpl.AuthDetails authDetails = authService.loginUser(
                loginDto.getEmail(),
                loginDto.getPassword(),
                httpServletResponse
        );

        AuthResponseDto authResponseDto = new AuthResponseDto(
                authDetails.getAccessToken(),
                "Bearer",
                accessTokenExpirationMs,
                authDetails.getUser().getEmail(),
                authDetails.getRoleNames()
        );

        log.info("Requester [{}]: Successfully authenticated user: {}. Access token and refresh cookie generated.",
                requesterId, authDetails.getUser().getEmail());
        return ResponseEntity.ok(authResponseDto);
    }

    /**
     * Refreshes an access token using a valid refresh token from an HTTP-only cookie.
     * A new refresh token (and cookie) is also issued (token rotation).
     *
     * @param refreshTokenFromCookie The refresh token string retrieved from the cookie.
     * @param httpServletResponse    The HttpServletResponse used by the auth service to set the new refresh token cookie.
     * @return A ResponseEntity containing a {@link TokenRefreshResponseDto} with the new access token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @CookieValue(name = "${app.security.refresh-cookie.name}") String refreshTokenFromCookie,
            HttpServletResponse httpServletResponse
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to refresh access token.", requesterId);

        if (refreshTokenFromCookie == null || refreshTokenFromCookie.isEmpty()) {
            log.warn("Requester [{}]: Refresh token cookie is missing or empty. Cannot refresh.", requesterId);
            // Consider returning ApiResponse for consistency, even for direct error responses
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            //       .body(new ApiResponse<>(List.of(new FieldErrorDto("refreshToken", "Refresh token is missing."))));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Simpler for now
        }

        log.debug("Requester [{}]: Refresh token received from cookie.", requesterId);
        AuthServiceImpl.RefreshedTokenDetails refreshedTokenDetails = authService.refreshAccessToken(
                refreshTokenFromCookie,
                httpServletResponse
        );

        TokenRefreshResponseDto responseDto = new TokenRefreshResponseDto(
                refreshedTokenDetails.getNewAccessToken(),
                "Bearer",
                accessTokenExpirationMs
        );

        log.info("Requester [{}]: Successfully refreshed access token. New refresh cookie set.", requesterId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Logs out the currently authenticated user.
     * Invalidates server-side refresh tokens and instructs the client to clear authentication cookies.
     *
     * @param httpServletResponse The HttpServletResponse used by the auth service to send cookie clearing headers.
     * @return A ResponseEntity containing an {@link ApiResponse} with a success message.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse httpServletResponse) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to logout.", requesterId);
        String logoutMessage;

        if ("GUEST".equals(requesterId) || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            log.info("Requester [{}]: No active user session to logout or already logged out.", requesterId);
            authService.clearAuthCookies(httpServletResponse); // Attempt to clear cookies anyway
            logoutMessage = "No active user session to logout or already logged out. Cookies cleared if any.";
        } else {
            // Assuming requesterId is the email/username from SecurityContextHolder
            // The authService.logoutUser needs the internal integer ID.
            // If your User entity is the principal, you can get it.
            // Otherwise, you might need a userService.findByUsername(requesterId) call here if not done in authService.
            // For simplicity, if authService.logoutUser can take username, that's easier.
            // Or, if your UserDetails principal is your User entity:
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            int userIdToLogout = -1; // Default to indicate not found
            if (principal instanceof User) { // Assuming your User entity implements UserDetails
                userIdToLogout = ((User) principal).getId();
            } else if (principal instanceof org.springframework.security.core.userdetails.User) {
                // If using Spring's User, and you store your internal ID elsewhere or need to look it up
                // This might require a call to IUserService:
                // User appUser = userService.read(requesterId); // Assuming requesterId is email
                // if (appUser != null) userIdToLogout = appUser.getId();
                log.warn("Requester [{}]: Principal is Spring's UserDetails, not application User entity. Logout might be incomplete if internal ID is needed by AuthService and not derivable.", requesterId);
                // For this example, we assume `authService.logoutUser` can handle it, or the SecurityContext will be cleared regardless.
            }


            if (userIdToLogout != -1) {
                boolean logoutSuccess = authService.logoutUser(userIdToLogout, httpServletResponse);
                if (logoutSuccess) {
                    logoutMessage = "Logout successful.";
                    log.info("Requester [{}]: Logout process completed for user ID: {}.", requesterId, userIdToLogout);
                } else {
                    logoutMessage = "Logout processed with potential issues (e.g., server-side token invalidation). Cookies cleared.";
                    log.warn("Requester [{}]: Logout for user ID: {} processed with potential issues.", requesterId, userIdToLogout);
                }
            } else {
                // Fallback if we couldn't get a specific user ID but user was authenticated
                authService.clearAuthCookies(httpServletResponse); // Clear cookies
                SecurityContextHolder.clearContext(); // Clear context
                logoutMessage = "Authenticated session cleared. Cookies cleared.";
                log.info("Requester [{}]: Authenticated session cleared (specific user ID not resolved for service logout). Cookies cleared.", requesterId);
            }
        }
        return ResponseEntity.ok(new ApiResponse<>(logoutMessage));
    }
}