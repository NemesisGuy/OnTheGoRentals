package za.ac.cput.controllers.security; // Package implies security/user-specific context

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // For more detailed user info
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.dto.request.UserUpdateRequestDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService;

import java.util.List;
import java.util.UUID; // For logging UUID if available

/**
 * UserController.java
 * Controller for authenticated users to manage their own profile information and related data.
 * Endpoints include retrieving/updating their profile and viewing their rental history.
 *
 * Author: [Original Author Name] // Please specify
 * Updated by: Peter Buckingham
 * Date: [Original Date]
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/users/me") // Base path for authenticated user's resources
// @CrossOrigin(...) // Prefer global CORS configuration
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;
    private final IRentalService rentalService;

    /**
     * Constructs a UserController with necessary service dependencies.
     *
     * @param userService   The user service for profile operations.
     * @param rentalService The rental service for retrieving rental history.
     */
    @Autowired
    public UserController(IUserService userService, IRentalService rentalService) {
        this.userService = userService;
        this.rentalService = rentalService;
        log.info("UserController initialized.");
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     * The user is identified by their email from the security context.
     *
     * @return A ResponseEntity containing the {@link UserResponseDTO} of the authenticated user.
     * @throws ResourceNotFoundException if the user's profile cannot be found.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Typically the username (email in this case)
        log.info("User [{}] requesting their profile.", userEmail);

        User userProfileEntity = userService.read(userEmail);
        if (userProfileEntity == null) {
            log.warn("User profile not found for authenticated user: {}", userEmail);
            throw new ResourceNotFoundException("User profile not found for authenticated user: " + userEmail);
        }
        log.info("Successfully retrieved profile for user [{}], ID: {}, UUID: {}", userEmail, userProfileEntity.getId(), userProfileEntity.getUuid());
        return ResponseEntity.ok(UserMapper.toDto(userProfileEntity));
    }

    /**
     * Updates the profile of the currently authenticated user.
     * Only allows updates to first name and last name as per the {@link UserUpdateRequestDTO}.
     * Email and password changes should have dedicated, secure flows.
     *
     * @param userUpdateDTO The {@link UserUpdateRequestDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link UserResponseDTO}.
     * @throws ResourceNotFoundException if the user's profile cannot be found for update.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateRequestDTO userUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("User [{}] requesting to update their profile with DTO: {}", userEmail, userUpdateDTO);

        User currentUserEntity = userService.read(userEmail);
        if (currentUserEntity == null) {
            log.warn("User profile not found for update for authenticated user: {}", userEmail);
            throw new ResourceNotFoundException("User not found for update: " + userEmail);
        }
        log.debug("Found user profile for update: ID: {}, UUID: {}", currentUserEntity.getId(), currentUserEntity.getUuid());

        boolean changed = false;
        if (userUpdateDTO.getFirstName() != null && !userUpdateDTO.getFirstName().equals(currentUserEntity.getFirstName())) {
            log.debug("Updating first name for user [{}] from '{}' to '{}'", userEmail, currentUserEntity.getFirstName(), userUpdateDTO.getFirstName());
            currentUserEntity.setFirstName(userUpdateDTO.getFirstName());
            changed = true;
        }
        if (userUpdateDTO.getLastName() != null && !userUpdateDTO.getLastName().equals(currentUserEntity.getLastName())) {
            log.debug("Updating last name for user [{}] from '{}' to '{}'", userEmail, currentUserEntity.getLastName(), userUpdateDTO.getLastName());
            currentUserEntity.setLastName(userUpdateDTO.getLastName());
            changed = true;
        }
        // Email/password updates are intentionally omitted here for simplicity and security.

        if (changed) {
            log.debug("Persisting profile changes for user [{}]", userEmail);
            // Assuming userService.update(id, entity) is the correct signature.
            User updatedUserEntity = userService.update(currentUserEntity.getId(), currentUserEntity);
            log.info("Successfully updated profile for user [{}], ID: {}, UUID: {}", userEmail, updatedUserEntity.getId(), updatedUserEntity.getUuid());
            return ResponseEntity.ok(UserMapper.toDto(updatedUserEntity));
        } else {
            log.info("No changes detected in profile update request for user [{}]. Returning current profile.", userEmail);
            return ResponseEntity.ok(UserMapper.toDto(currentUserEntity));
        }
    }

    /**
     * Retrieves the rental history for the currently authenticated user.
     *
     * @return A ResponseEntity containing a list of {@link RentalResponseDTO}s representing the user's rental history,
     *         or no content if the user has no rental history.
     * @throws ResourceNotFoundException if the user's profile cannot be found.
     */
    @GetMapping("/rental-history")
    public ResponseEntity<List<RentalResponseDTO>> getCurrentUserRentalHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("User [{}] requesting their rental history.", userEmail);

        User currentUserEntity = userService.read(userEmail);
        if (currentUserEntity == null) {
            log.warn("User profile not found for retrieving rental history for authenticated user: {}", userEmail);
            throw new ResourceNotFoundException("User not found for retrieving rental history: " + userEmail);
        }
        log.debug("Found user profile for rental history: ID: {}, UUID: {}", currentUserEntity.getId(), currentUserEntity.getUuid());

        List<Rental> rentalHistoryEntities = rentalService.getRentalHistoryByUser(currentUserEntity);
        if (rentalHistoryEntities.isEmpty()) {
            log.info("No rental history found for user [{}].", userEmail);
            return ResponseEntity.noContent().build();
        }
        List<RentalResponseDTO> dtoList = RentalMapper.toDtoList(rentalHistoryEntities);
        log.info("Successfully retrieved {} rental history entries for user [{}].", dtoList.size(), userEmail);
        return ResponseEntity.ok(dtoList);
    }

    // Helper method to get user identifier from Authentication for logging
    // This can be expanded or made more generic if needed
    private String getAuthenticatedUserIdentifier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "ANONYMOUS";
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Standard Spring Security
        } else if (principal instanceof String) {
            return (String) principal; // If getName() returns a simple string
        }
        // Could also try to cast to your custom UserPrincipal if you have one to get ID/UUID
        // e.g., if (principal instanceof MyCustomUserPrincipal) return ((MyCustomUserPrincipal)principal).getEmail();
        return authentication.getName(); // Fallback
    }
}