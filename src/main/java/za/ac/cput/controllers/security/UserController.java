package za.ac.cput.controllers.security;

import jakarta.validation.Valid; // For validating request body
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // For logout
import org.springframework.security.core.context.SecurityContextHolder; // For logout
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.RentalDTO;
import za.ac.cput.domain.dto.LoginDto;
import za.ac.cput.domain.dto.RegisterDto;
import za.ac.cput.domain.dto.AuthResponseDto; // Import
import za.ac.cput.domain.dto.TokenRefreshRequestDto; // Import
import za.ac.cput.domain.dto.TokenRefreshResponseDto; // Import
import za.ac.cput.domain.security.User;
import za.ac.cput.service.IRentalService; // Keep this
import za.ac.cput.service.impl.RentalServiceImpl; // Keep this
import za.ac.cput.service.impl.UserService; // Change to concrete type or keep interface if preferred

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
// @RequiredArgsConstructor // If using this, ensure all final fields are in constructor
public class UserController {

    private final RentalServiceImpl rentalService; // Assuming direct injection
    // private final IRentalService iRentalService; // You have both, choose one or clarify
    private final UserService userService; // Use concrete type to access new methods directly

    @Autowired // If not using @RequiredArgsConstructor for all
    public UserController(RentalServiceImpl rentalService, UserService userService) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    //RessourceEndPoint:http://localhost:8080/api/user/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto) {
        System.out.println("Register called, registerDto = " + registerDto);
        // UserService.register now returns ResponseEntity<AuthResponseDto> or ResponseEntity<String>
        return userService.register(registerDto); // This should now return the ResponseEntity with AuthResponseDto
    }


    //RessourceEndPoint:http://localhost:8080/api/user/authenticate
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(@Valid @RequestBody LoginDto loginDto) {
        System.out.println("Authenticate called, loginDto = " + loginDto);
        AuthResponseDto authResponse = userService.authenticateAndGenerateTokens(loginDto);
        return ResponseEntity.ok(authResponse);
    }

    // NEW ENDPOINT for refreshing token
    //RessourceEndPoint:http://localhost:8080/api/user/refresh
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(@Valid @RequestBody TokenRefreshRequestDto request) {
        String requestRefreshToken = request.getRefreshToken();
        TokenRefreshResponseDto response = userService.refreshToken(requestRefreshToken);
        return ResponseEntity.ok(response);
    }

    // OPTIONAL LOGOUT ENDPOINT
    //RessourceEndPoint:http://localhost:8080/api/user/logout
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        System.out.println("Logout called");
        // Get current user (principal) to invalidate their refresh token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // usually the email or username
            User user = userService.read(username);
            if (user != null) {
                userService.logoutUser(user.getId());
                return ResponseEntity.ok("User logged out successfully and refresh token invalidated.");
            }
        }

        return ResponseEntity.badRequest().body("Could not logout user.");
    }


    // Endpoint to get user profile
    @GetMapping("/profile/read/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId) {
        System.out.println("Get user profile called, userId = " + userId);
        User userProfile = userService.read(userId);
        if (userProfile != null) {
            // Optionally, use a DTO to avoid sending sensitive info like password hash
            // UserDTO userDto = userService.readDTO(userId);
            return ResponseEntity.ok(userProfile); // or userDto
        } else {
            return ResponseEntity.status(404).body("User profile not found");
        }
    }

    // Endpoint to update user profile
    @PutMapping("/profile/update/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Integer userId, @RequestBody User user) {
        User updatedUser = userService.update(userId, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.status(404).body("User not found or update failed.");
    }

    @GetMapping("/profile/{userId}/rental-history")
    public ResponseEntity<List<RentalDTO>> getRentalHistory(@PathVariable Integer userId) {
        User user = userService.read(userId);
        if (user == null) {
            return ResponseEntity.status(404).body(null); // Or an empty list with appropriate message
        }
        List<RentalDTO> rentalHistory = rentalService.getRentalHistoryByUser(user);
        return ResponseEntity.ok(rentalHistory);
    }
}