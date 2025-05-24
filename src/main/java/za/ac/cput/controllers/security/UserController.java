package za.ac.cput.controllers.security;

import jakarta.servlet.http.HttpServletResponse; // Import for setting cookies
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Rental;
import za.ac.cput.domain.dto.dual.RentalDTO;
import za.ac.cput.domain.dto.dual.UserDTO;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.dto.request.UserRequestDTO;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.dto.response.TokenRefreshResponseDto;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.security.User;
import za.ac.cput.service.impl.RentalServiceImpl;
import za.ac.cput.service.impl.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "*", allowCredentials = "true") // allowCredentials needed for cookies across origins
//@CrossOrigin(origins = {"http://localhost:5173", "https://otgr.nemesisnet.co.za"}, allowCredentials = "true")

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final RentalServiceImpl rentalService;
    private final UserService userService;

    @Value("${app.security.refresh-cookie.name}") // Get cookie name from properties
    private String refreshTokenCookieName;

    public UserController(RentalServiceImpl rentalService, UserService userService) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto, HttpServletResponse response) {
        // The service method now directly returns ResponseEntity with cookie logic inside
        // For better separation, controller could build ResponseEntity and call service for DTO only.
        // But for simplicity, let's assume service handles the ResponseEntity construction with cookie.
        // We'd need to modify service.register to accept HttpServletResponse or return headers.
        // Simpler: Call the new service method that returns ResponseEntity<AuthResponseDto>
        return userService.registerAndReturnAuthResponse(registerDto); // Assuming this method handles cookie
        // OR you set cookie here based on service response
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(@Valid @RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse) {
        System.out.println("Authenticate called, loginDto = " + loginDto);
        AuthResponseDto authResponse = userService.authenticateAndGenerateTokens(loginDto, httpServletResponse); // Pass response to set cookie
        // Cookie is set by the service method. We just return the DTO in the body.
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(
            @CookieValue(name = "${app.security.refresh-cookie.name}") String refreshTokenFromCookie, // Read from cookie
            HttpServletResponse httpServletResponse) {

        if (refreshTokenFromCookie == null || refreshTokenFromCookie.isEmpty()) {
            // Handle missing refresh token cookie appropriately
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Or a specific error DTO
        }
        System.out.println("Controller: Refresh token from cookie received (first 5 chars): " + refreshTokenFromCookie.substring(0, Math.min(5, refreshTokenFromCookie.length())) + "...");
        TokenRefreshResponseDto responseDto = userService.refreshToken(refreshTokenFromCookie, httpServletResponse); // Pass response to set new cookie
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletResponse httpServletResponse) { // Pass response to clear cookie
        System.out.println("Logout called");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            String username = authentication.getName();
            User user = userService.read(username); // Read by email
            if (user != null) {
                return userService.logoutUserAndClearCookie(user.getId(), httpServletResponse);
            } else {
                // User not found, but still try to clear context and potentially cookie if one exists
                SecurityContextHolder.clearContext();
                // Consider sending a cookie-clearing header even if user not found, just in case
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found for logout, context cleared.");
            }
        }
        // If no authenticated user, just return OK or a message indicating no active session
        return ResponseEntity.ok("No active user session to logout or already logged out.");
    }

    // User profile methods don't need direct cookie handling, as JWT access token is used
    @GetMapping("/profile/read/profile") // Changed from /read/{userId} to get current user's profile
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = authentication.getName();
        System.out.println("Get current user profile called for email = " + userEmail);
        User userProfile = userService.read(userEmail);
        if (userProfile != null) {
            return ResponseEntity.ok(UserMapper.toDto(userProfile));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // UserDTO is null
        }
    }

    @PutMapping("/profile/update") // Update current user's profile
    public ResponseEntity<UserResponseDTO> updateUserProfile(@Valid @RequestBody UserRequestDTO userUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = authentication.getName();
        User currentUser = userService.read(userEmail);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Map DTO to entity, applying updates to currentUser
        // This mapping should only touch fields allowed to be updated via this DTO
        // Example:
        currentUser.setFirstName(userUpdateDTO.getFirstName());
        currentUser.setLastName(userUpdateDTO.getLastName());
        // Handle password update separately or via a specific DTO/endpoint.
        // If userUpdateDTO contains password and it's not empty:
        // if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
        //     currentUser.setPassword(userUpdateDTO.getPassword()); // UserService.update will encode it
        // }

        User updatedUser = userService.update(currentUser.getId(), currentUser); // Pass the modified entity
        return ResponseEntity.ok(UserMapper.toDto(updatedUser));
    }

    @GetMapping("/profile/rental-history")
    public ResponseEntity<List<RentalResponseDTO>> getRentalHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.read(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        List<Rental> rentalHistory = rentalService.getRentalHistoryByUser(user);
        List<RentalResponseDTO> rentalHistoryDTO = rentalHistory.stream()
                .map(RentalMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rentalHistoryDTO);
    }
}