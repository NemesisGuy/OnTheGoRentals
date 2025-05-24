package za.ac.cput.controllers.security;

/*
package za.ac.cput.controllers.auth; // Example new package
*/

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.dto.response.TokenRefreshResponseDto;
import za.ac.cput.domain.security.User;
import za.ac.cput.service.IUserService; // Using interface
import za.ac.cput.service.impl.UserService;
/*
* URL Changes for Auth Endpoints:
*
* Register: POST /api/v1/user/register -> POST /api/v1/auth/register
* Login: POST /api/v1/user/authenticate -> POST /api/v1/auth/login
* Refresh Token: POST /api/v1/user/refresh -> POST /api/v1/auth/refresh
* Logout: POST /api/v1/user/logout -> POST /api/v1/auth/logout
*
* URL Changes for User Profile Endpoints:
*
* Get Profile: GET /api/v1/user/profile/read/profile -> GET /api/v1/users/me/profile
* Update Profile: PUT /api/v1/user/profile/update -> PUT /api/v1/users/me/profile
* Rental History: GET /api/v1/user/profile/rental-history -> GET /api/v1/users/me/rental-history
*
* */
@RestController
@RequestMapping("/api/v1/auth") // New base path for authentication
// @CrossOrigin(...) // Global CORS config is preferred
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        // Assuming userService.registerAndReturnAuthResponse correctly handles cookie setting via its own response entity
        // OR that it returns AuthResponseDto and we construct ResponseEntity here.
        // For now, let's stick to your service method that likely returns ResponseEntity<AuthResponseDto>
        // which itself calls the method that takes HttpServletResponse.
        // If service returns AuthResponseDto:
        // AuthResponseDto authResponse = userService.registerAndSetCookie(registerDto, httpServletResponse);
        // return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        // If service returns ResponseEntity<AuthResponseDto>:
        return userService.registerAndReturnAuthResponse(registerDto); // Assumes this method in IUserService
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse) {
        System.out.println("AuthController: Login called, loginDto = " + loginDto);
        AuthResponseDto authResponse = userService.authenticateAndGenerateTokens(loginDto, httpServletResponse);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @CookieValue(name = "${app.security.refresh-cookie.name}") String refreshTokenFromCookie,
            HttpServletResponse httpServletResponse) {

        if (refreshTokenFromCookie == null || refreshTokenFromCookie.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // No body for 401 usually
        }
        System.out.println("AuthController: Refresh token from cookie received.");
        TokenRefreshResponseDto responseDto = userService.refreshToken(refreshTokenFromCookie, httpServletResponse);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse httpServletResponse) {
        System.out.println("AuthController: Logout called");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            String username = authentication.getName();
            User user = userService.read(username); // Service method read(String email) returns User entity
            if (user != null) {
                // Assuming userService.logoutUserAndClearCookie takes int userId
                return userService.logoutUserAndClearCookie(user.getId(), httpServletResponse);
            } else {
                SecurityContextHolder.clearContext();
                // Logic to clear cookie even if user not found, if your service doesn't do this.
                // For now, relying on service method.
                // ResponseCookie cleanCookie = userService.getCleanRefreshTokenCookieForLogout(); // If service has this helper
                // httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());
                return ResponseEntity.status(HttpStatus.OK).body("User context cleared (user not found in DB for explicit token invalidation).");
            }
        }
        return ResponseEntity.ok("No active user session to logout or already logged out.");
    }
}