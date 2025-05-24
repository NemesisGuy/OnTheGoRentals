package za.ac.cput.controllers.security;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Rental; // For RentalServiceImpl return type
import za.ac.cput.domain.dto.request.UserUpdateRequestDTO; // Using the new DTO
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.security.User;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IRentalService; // Using interface
import za.ac.cput.service.IUserService;   // Using interface

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/*
* URL Changes for Auth Endpoints:

Register: POST /api/v1/user/register -> POST /api/v1/auth/register

Login: POST /api/v1/user/authenticate -> POST /api/v1/auth/login

Refresh Token: POST /api/v1/user/refresh -> POST /api/v1/auth/refresh

Logout: POST /api/v1/user/logout -> POST /api/v1/auth/logout
*
* URL Changes for User Profile Endpoints:

Get Profile: GET /api/v1/user/profile/read/profile -> GET /api/v1/users/me/profile

Update Profile: PUT /api/v1/user/profile/update -> PUT /api/v1/users/me/profile

Rental History: GET /api/v1/user/profile/rental-history -> GET /api/v1/users/me/rental-history
*
*
*
*
*
* */


@RestController
@RequestMapping("/api/v1/users/me") // Base path for "my" user resources
// @CrossOrigin(...) // Prefer global CORS configuration
public class UserController { // Renamed from original UserController, or you could create UserProfileController

    private final IUserService userService;
    private final IRentalService rentalService;

    @Autowired
    public UserController(IUserService userService, IRentalService rentalService) {
        this.userService = userService;
        this.rentalService = rentalService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Get email of authenticated user

        User userProfileEntity = userService.read(userEmail); // Service returns User entity
        if (userProfileEntity == null) {
            throw new ResourceNotFoundException("User profile not found for authenticated user: " + userEmail);
        }
        return ResponseEntity.ok(UserMapper.toDto(userProfileEntity)); // Map to DTO in controller
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateRequestDTO userUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUserEntity = userService.read(userEmail); // Fetch current user entity

        if (currentUserEntity == null) {
            throw new ResourceNotFoundException("User not found for update: " + userEmail);
        }

        // Apply changes from DTO to the fetched entity
        // This logic can also be moved to a dedicated service method like:
        // User updatedUserEntity = userService.updateUserProfile(currentUserEntity.getId(), userUpdateDTO);
        boolean changed = false;
        if (userUpdateDTO.getFirstName() != null && !userUpdateDTO.getFirstName().equals(currentUserEntity.getFirstName())) {
            currentUserEntity.setFirstName(userUpdateDTO.getFirstName());
            changed = true;
        }
        if (userUpdateDTO.getLastName() != null && !userUpdateDTO.getLastName().equals(currentUserEntity.getLastName())) {
            currentUserEntity.setLastName(userUpdateDTO.getLastName());
            changed = true;
        }
        // Note: Email and password updates are not handled here for simplicity and security.
        // They should have their own dedicated, more secure flows if needed.

        if (changed) {
            // Call the existing service update method that takes the User entity
            User updatedUserEntity = userService.update(currentUserEntity.getId(), currentUserEntity);
            return ResponseEntity.ok(UserMapper.toDto(updatedUserEntity));
        } else {
            // If no actual changes were made based on DTO content, return current profile
            return ResponseEntity.ok(UserMapper.toDto(currentUserEntity));
        }
    }

    @GetMapping("/rental-history")
    public ResponseEntity<List<RentalResponseDTO>> getCurrentUserRentalHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUserEntity = userService.read(userEmail); // Fetch User entity

        if (currentUserEntity == null) {
            throw new ResourceNotFoundException("User not found for retrieving rental history: " + userEmail);
        }

        // Your existing service method takes the User entity
        List<Rental> rentalHistoryEntities = rentalService.getRentalHistoryByUser(currentUserEntity);
        if (rentalHistoryEntities.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(rentalHistoryEntities));
    }
}