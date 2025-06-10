package za.ac.cput.controllers.security;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.domain.dto.request.UserUpdateDTO;

import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.ImageType;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.FileStorageService;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService;
import za.ac.cput.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserController.java
 * Controller for authenticated users to manage their own profile and related data.
 * This includes updating profile info, uploading a profile image, and viewing rental history.
 *
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;
    private final IRentalService rentalService;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserController(IUserService userService, IRentalService rentalService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        log.info("UserController initialized.");
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return A ResponseEntity containing the {@link UserResponseDTO}.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] requesting their profile.", userEmail);

        User user = userService.read(userEmail); // Throws exception if not found
        log.info("Successfully retrieved profile for user [{}].", userEmail);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    /**
     * Updates the profile of the currently authenticated user.
     *
     * @param userUpdateDTO The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated {@link UserResponseDTO}.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] requesting to update their profile with DTO: {}", userEmail, userUpdateDTO);

        User currentUser = userService.read(userEmail);
        User userWithUpdates = UserMapper.applyUpdateDtoToEntity(userUpdateDTO, currentUser);

        User updatedUser = userService.update(currentUser.getId(), userWithUpdates);
        log.info("Successfully updated profile for user [{}].", userEmail);
        return ResponseEntity.ok(UserMapper.toDto(updatedUser));
    }

    /**
     * [NEW] Uploads or replaces the profile image for the currently authenticated user.
     *
     * @param file The image file sent as multipart/form-data.
     * @return A ResponseEntity containing the updated {@link UserResponseDTO} with the new image URL.
     */
    @PostMapping("/profile-image")
    public ResponseEntity<UserResponseDTO> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] is uploading a profile image.", userEmail);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image file cannot be empty.");
        }

        User currentUser = userService.read(userEmail);

        // Save the new file
        String filename = fileStorageService.save(ImageType.SELFIE.getFolder(), file);

        // Update the user entity with the new image details
        User.UserBuilder builder = currentUser.toBuilder()
                .profileImageFileName(filename)
                .profileImageType(ImageType.SELFIE.getFolder())
                .profileImageUploadedAt(LocalDateTime.now());

        User updatedUser = userService.update(currentUser.getId(), builder.build());

        log.info("Successfully uploaded profile image '{}' for user [{}].", filename, userEmail);
        return ResponseEntity.ok(UserMapper.toDto(updatedUser));
    }


    /**
     * Retrieves the rental history for the currently authenticated user.
     *
     * @return A ResponseEntity containing a list of the user's rentals.
     */
    @GetMapping("/rental-history")
    public ResponseEntity<List<RentalResponseDTO>> getCurrentUserRentalHistory() {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] requesting their rental history.", userEmail);

        User currentUser = userService.read(userEmail);

        List<Rental> rentalHistory = rentalService.getRentalHistoryByUser(currentUser);
        if (rentalHistory.isEmpty()) {
            log.info("No rental history found for user [{}].", userEmail);
            return ResponseEntity.noContent().build();
        }
        log.info("Successfully retrieved {} rental history entries for user [{}].", rentalHistory.size(), userEmail);
        return ResponseEntity.ok(RentalMapper.toDtoList(rentalHistory));
    }

    // The old, incorrect selfie endpoint is removed.
}