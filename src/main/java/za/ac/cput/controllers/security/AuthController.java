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
        log.info("AuthController initialized with IAuthService.");
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the default 'USER' role and automatically logs them in. " +
                    "Returns a JWT access token in the response body and sets a refresh token in an HTTP-only cookie."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered and logged in successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid registration data provided", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict: User with the provided email already exists", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterDto registerDto,
            HttpServletResponse httpServletResponse
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to register new user with email: {}", requesterId, registerDto.getEmail());

        User registeredUser = authService.registerUser(
                registerDto.getFirstName(),
                registerDto.getLastName(),
                registerDto.getEmail(),
                registerDto.getPassword(),
                RoleName.USER
        );
        log.info("Requester [{}]: User '{}' registered successfully. Proceeding to log in.", requesterId, registeredUser.getEmail());

        AuthServiceImpl.AuthDetails authDetails = authService.loginUser(
                registeredUser.getEmail(),
                registerDto.getPassword(),
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

    @Operation(
            summary = "Authenticate a user",
            description = "Logs in a user with an email and password. Returns a JWT access token in the response body " +
                    "and sets a refresh token in an HTTP-only cookie."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid login data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid credentials", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginDto loginDto,
            HttpServletResponse httpServletResponse
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
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

    @Operation(
            summary = "Refresh an access token",
            description = "Generates a new JWT access token using a valid refresh token from the HTTP-only cookie. " +
                    "A new refresh token is also issued (token rotation) and set in a new cookie."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenRefreshResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Refresh token is missing, invalid, or expired", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @Parameter(description = "The refresh token cookie. Automatically sent by the browser.", in = ParameterIn.COOKIE, name = "${app.security.refresh-cookie.name}")
            @CookieValue(name = "${app.security.refresh-cookie.name}", required = false) String refreshTokenFromCookie,
            HttpServletResponse httpServletResponse
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to refresh access token.", requesterId);

        if (refreshTokenFromCookie == null || refreshTokenFromCookie.isEmpty()) {
            log.warn("Requester [{}]: Refresh token cookie is missing or empty. Cannot refresh.", requesterId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

    @Operation(
            summary = "Log out the current user",
            description = "Invalidates the user's session by deleting the refresh token from the database and clearing the authentication cookie.",
            security = @SecurityRequirement(name = "bearerAuth") // This endpoint requires authentication
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: No active session to log out", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseWrapper<String>> logout(HttpServletResponse httpServletResponse) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to logout.", requesterId);
        String logoutMessage;

        if ("GUEST".equals(requesterId) || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            log.info("Requester [{}]: No active user session to logout or already logged out.", requesterId);
            authService.clearAuthCookies(httpServletResponse);
            logoutMessage = "No active user session to logout or already logged out. Cookies cleared if any.";
        } else {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            int userIdToLogout = -1;
            if (principal instanceof User) {
                userIdToLogout = ((User) principal).getId();
            } else {
                log.warn("Requester [{}]: Principal is not an instance of the application User entity. Logout might be incomplete if internal ID is needed.", requesterId);
            }

            if (userIdToLogout != -1) {
                boolean logoutSuccess = authService.logoutUser(userIdToLogout, httpServletResponse);
                if (logoutSuccess) {
                    logoutMessage = "Logout successful.";
                    log.info("Requester [{}]: Logout process completed for user ID: {}.", requesterId, userIdToLogout);
                } else {
                    logoutMessage = "Logout processed with potential issues. Cookies cleared.";
                    log.warn("Requester [{}]: Logout for user ID: {} processed with potential issues.", requesterId, userIdToLogout);
                }
            } else {
                authService.clearAuthCookies(httpServletResponse);
                SecurityContextHolder.clearContext();
                logoutMessage = "Authenticated session cleared. Cookies cleared.";
                log.info("Requester [{}]: Authenticated session cleared (specific user ID not resolved). Cookies cleared.", requesterId);
            }
        }
        return ResponseEntity.ok(new ApiResponseWrapper<>(logoutMessage));
    }
}