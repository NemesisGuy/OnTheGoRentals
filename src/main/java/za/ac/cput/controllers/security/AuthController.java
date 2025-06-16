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
 * Provides endpoints for user registration, login, token refresh, and logout.
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, logout, and token management.")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final IAuthService authService;

    @Value("${jwt.expiration}")
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
     * Registers a new user account and automatically logs them in.
     *
     * @param registerDto         The DTO containing user registration details.
     * @param httpServletResponse The response object used to set the refresh token cookie.
     * @return A ResponseEntity containing an AuthResponseDto with the access token upon success.
     */
    @Operation(summary = "Register a new user", description = "Creates a new user account with the default 'USER' role and automatically logs them in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered and logged in successfully", content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: User with the provided email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto, HttpServletResponse httpServletResponse) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to register new user with email: {}", requesterId, registerDto.getEmail());

        User registeredUser = authService.registerUser(registerDto.getFirstName(), registerDto.getLastName(), registerDto.getEmail(), registerDto.getPassword(), RoleName.USER);
        AuthServiceImpl.AuthDetails authDetails = authService.loginUser(registeredUser.getEmail(), registerDto.getPassword(), httpServletResponse);

        AuthResponseDto authResponseDto = new AuthResponseDto(authDetails.getAccessToken(), "Bearer", accessTokenExpirationMs, authDetails.getUser().getEmail(), authDetails.getRoleNames());

        log.info("Requester [{}]: Successfully registered and logged in user: {}", requesterId, authDetails.getUser().getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDto);
    }

    /**
     * Authenticates an existing user and provides authentication tokens.
     * The access token is returned in the response body; the refresh token is set as an HTTP-only cookie.
     *
     * @param loginDto            The DTO containing user login credentials.
     * @param httpServletResponse The response object used to set the refresh token cookie.
     * @return A ResponseEntity containing an AuthResponseDto with the access token and user info.
     */
    @Operation(summary = "Authenticate a user", description = "Logs in a user with an email and password, returning a JWT and setting a refresh token cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to login user with email: {}", requesterId, loginDto.getEmail());

        AuthServiceImpl.AuthDetails authDetails = authService.loginUser(loginDto.getEmail(), loginDto.getPassword(), httpServletResponse);
        AuthResponseDto authResponseDto = new AuthResponseDto(authDetails.getAccessToken(), "Bearer", accessTokenExpirationMs, authDetails.getUser().getEmail(), authDetails.getRoleNames());

        log.info("Requester [{}]: Successfully authenticated user: {}", requesterId, authDetails.getUser().getEmail());
        return ResponseEntity.ok(authResponseDto);
    }

    /**
     * Refreshes an access token using a valid refresh token from an HTTP-only cookie.
     * A new refresh token is also issued and set as a new cookie (token rotation).
     *
     * @param refreshTokenFromCookie The refresh token string retrieved from the cookie.
     * @param httpServletResponse    The response object used to set the new refresh token cookie.
     * @return A ResponseEntity containing a DTO with the new access token.
     */
    @Operation(summary = "Refresh an access token", description = "Generates a new JWT access token using the refresh token from the HTTP-only cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully", content = @Content(schema = @Schema(implementation = TokenRefreshResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Refresh token is missing, invalid, or expired")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @Parameter(description = "The refresh token cookie, sent automatically by the browser.", in = ParameterIn.COOKIE, name = "${app.security.refresh-cookie.name}")
            @CookieValue(name = "${app.security.refresh-cookie.name}", required = false) String refreshTokenFromCookie,
            HttpServletResponse httpServletResponse) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to refresh access token.", requesterId);

        if (refreshTokenFromCookie == null || refreshTokenFromCookie.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthServiceImpl.RefreshedTokenDetails refreshedTokenDetails = authService.refreshAccessToken(refreshTokenFromCookie, httpServletResponse);
        TokenRefreshResponseDto responseDto = new TokenRefreshResponseDto(refreshedTokenDetails.getNewAccessToken(), "Bearer", accessTokenExpirationMs);

        log.info("Requester [{}]: Successfully refreshed access token.", requesterId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Logs out the currently authenticated user by invalidating their session and clearing authentication cookies.
     *
     * @param httpServletResponse The response object used to send cookie clearing headers.
     * @return A ResponseEntity containing a success message.
     */
    @Operation(summary = "Log out the current user", description = "Invalidates the user's session and clears authentication cookies.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Logout successful"))
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseWrapper<String>> logout(HttpServletResponse httpServletResponse) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to logout.", requesterId);
        String logoutMessage;

        if ("GUEST".equals(requesterId) || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            authService.clearAuthCookies(httpServletResponse);
            logoutMessage = "No active user session found. Cookies cleared if any.";
        } else {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User) {
                int userIdToLogout = ((User) principal).getId();
                authService.logoutUser(userIdToLogout, httpServletResponse);
                logoutMessage = "Logout successful.";
            } else {
                authService.clearAuthCookies(httpServletResponse);
                SecurityContextHolder.clearContext();
                logoutMessage = "Authenticated session cleared. Cookies cleared.";
            }
        }
        return ResponseEntity.ok(new ApiResponseWrapper<>(logoutMessage));
    }
}