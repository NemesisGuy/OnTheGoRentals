package za.ac.cput.controllers.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.api.response.ApiResponseWrapper;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.dto.response.TokenRefreshResponseDto;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.service.IAuthService;
import za.ac.cput.service.impl.AuthServiceImpl;
import za.ac.cput.utils.SecurityUtils;

/**
 * AuthController.java
 * Controller for handling user authentication and authorization processes.
 *
 * @author Peter Buckingham
 * @version 2.1
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, logout, and token management.")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final IAuthService authService;

    @Value("${jwt.expiration}")
    private Long accessTokenExpirationMs;

    @Autowired
    public AuthController(IAuthService authService) {
        this.authService = authService;
        log.info("AuthController initialized.");
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with the default 'USER' role and automatically logs them in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered and logged in successfully", content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: User with the provided email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto, HttpServletResponse httpServletResponse) {
        log.info("Requester [GUEST]: Attempting to register new user with email: {}", registerDto.getEmail());
        User registeredUser = authService.registerUser(registerDto.getFirstName(), registerDto.getLastName(), registerDto.getEmail(), registerDto.getPassword(), RoleName.USER);
        AuthServiceImpl.AuthDetails authDetails = authService.loginUser(registeredUser.getEmail(), registerDto.getPassword(), httpServletResponse);
        AuthResponseDto authResponseDto = new AuthResponseDto(authDetails.getAccessToken(), "Bearer", accessTokenExpirationMs, authDetails.getUser().getEmail(), authDetails.getRoleNames());
        log.info("Requester [GUEST]: Successfully registered and logged in user: {}", authDetails.getUser().getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDto);
    }

    @Operation(summary = "Authenticate a user", description = "Logs in a user with an email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse) {
        log.info("Requester [GUEST]: Attempting to login user with email: {}", loginDto.getEmail());
        AuthServiceImpl.AuthDetails authDetails = authService.loginUser(loginDto.getEmail(), loginDto.getPassword(), httpServletResponse);
        AuthResponseDto authResponseDto = new AuthResponseDto(authDetails.getAccessToken(), "Bearer", accessTokenExpirationMs, authDetails.getUser().getEmail(), authDetails.getRoleNames());
        log.info("Requester [GUEST]: Successfully authenticated user: {}", authDetails.getUser().getEmail());
        return ResponseEntity.ok(authResponseDto);
    }

    @Operation(summary = "Initiate password reset", description = "Sends a password reset link to the user's email address if the account exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent (if the user exists). Always returns OK to prevent email enumeration.")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Finalize password reset", description = "Sets a new password for a user using a valid reset token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password has been successfully reset."),
            @ApiResponse(responseCode = "400", description = "Bad Request: The token is invalid, expired, or the new password is weak.")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.finalizePasswordReset(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Refresh an access token", description = "Generates a new JWT access token using the refresh token from an HTTP-only cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully", content = @Content(schema = @Schema(implementation = TokenRefreshResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Refresh token is missing, invalid, or expired")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @Parameter(description = "The refresh token cookie, sent automatically by the browser.", in = ParameterIn.COOKIE, name = "${app.security.refresh-cookie.name}")
            @CookieValue(name = "${app.security.refresh-cookie.name}", required = false) String refreshTokenFromCookie,
            HttpServletResponse httpServletResponse) {
        if (refreshTokenFromCookie == null || refreshTokenFromCookie.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthServiceImpl.RefreshedTokenDetails refreshedTokenDetails = authService.refreshAccessToken(refreshTokenFromCookie, httpServletResponse);
        TokenRefreshResponseDto responseDto = new TokenRefreshResponseDto(refreshedTokenDetails.getNewAccessToken(), "Bearer", accessTokenExpirationMs);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Log out the current user", description = "Invalidates the user's session and clears authentication cookies.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Logout successful"))
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseWrapper<String>> logout(HttpServletResponse httpServletResponse) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to logout.", requesterId);
        String logoutMessage;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            authService.logoutUser(user.getId(), httpServletResponse);
            logoutMessage = "Logout successful.";
        } else {
            authService.clearAuthCookies(httpServletResponse);
            logoutMessage = "No active user session found. Cookies cleared if any.";
        }
        return ResponseEntity.ok(new ApiResponseWrapper<>(logoutMessage));
    }

    // --- DTO Records for Password Reset ---
    public record ForgotPasswordRequest(
            @Schema(description = "The email address of the user who forgot their password.", example = "user@example.com") String email) {
    }

    public record ResetPasswordRequest(
            @Schema(description = "The password reset token received via email.", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8") String token,
            @Schema(description = "The new password for the user account.", example = "newStrongPassword123!") String newPassword) {
    }
}