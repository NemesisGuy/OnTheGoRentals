package za.ac.cput.controllers.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import za.ac.cput.service.IFileStorageService;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService;
import za.ac.cput.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserController.java
 * Controller for authenticated users to manage their own profile and related data.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/users/me")
@Tag(name = "User Profile", description = "Endpoints for authenticated users to manage their own profile and related data.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;
    private final IRentalService rentalService;
    private final IFileStorageService fileStorageService;

    /**
     * Constructs the controller with necessary service dependencies.
     *
     * @param userService        The service for user-related operations.
     * @param rentalService      The service for rental history lookups.
     * @param fileStorageService The service for handling file storage.
     */
    @Autowired
    public UserController(IUserService userService, IRentalService rentalService, IFileStorageService fileStorageService) {
        this.userService = userService;
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        log.info("UserController initialized.");
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return A ResponseEntity containing the user's profile DTO.
     */
    @Operation(summary = "Get current user profile", description = "Retrieves the profile information of the currently authenticated user.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Profile retrieved successfully", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))))
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] requesting their profile.", userEmail);
        User user = userService.read(userEmail);
        return ResponseEntity.ok(UserMapper.toDto(user, fileStorageService));
    }

    /**
     * Updates the profile of the currently authenticated user.
     *
     * @param userUpdateDTO The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated user profile DTO.
     */
    @Operation(summary = "Update current user profile", description = "Updates the profile information (e.g., name, email) of the currently authenticated user.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))))
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] requesting to update their profile with DTO: {}", userEmail, userUpdateDTO);

        User currentUser = userService.read(userEmail);
        User userWithUpdates = UserMapper.applyUpdateDtoToEntity(userUpdateDTO, currentUser);
        User updatedUser = userService.update(currentUser.getId(), userWithUpdates);
        return ResponseEntity.ok(UserMapper.toDto(updatedUser, fileStorageService));
    }

    /**
     * Uploads or replaces the profile image for the currently authenticated user.
     *
     * @param file The image file sent as multipart/form-data.
     * @return A ResponseEntity containing the updated user profile DTO with the new image URL.
     */
    @Operation(summary = "Upload profile image", description = "Uploads or replaces the profile image for the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile image uploaded successfully", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "No file provided or file is empty")
    })
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> uploadProfileImage(
            @Parameter(description = "Profile image file to upload", required = true) @RequestParam("file") MultipartFile file) {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] is uploading a profile image.", userEmail);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image file cannot be empty.");
        }

        User currentUser = userService.read(userEmail);
        String fileKey = fileStorageService.save(file, ImageType.SELFIE.getFolder());
        String filename = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        User imageUpdatePayload = currentUser.toBuilder()
                .password(null) // Ensure password is not part of this update
                .profileImageFileName(filename)
                .profileImageType(ImageType.SELFIE.getFolder())
                .profileImageUploadedAt(LocalDateTime.now())
                .build();

        User updatedUser = userService.update(currentUser.getId(), imageUpdatePayload);
        log.info("Successfully uploaded profile image '{}' for user [{}].", filename, userEmail);
        return ResponseEntity.ok(UserMapper.toDto(updatedUser, fileStorageService));
    }

    /**
     * Retrieves the rental history for the currently authenticated user.
     *
     * @return A ResponseEntity containing a list of the user's past and current rentals.
     */
    @Operation(summary = "Get user rental history", description = "Retrieves the rental history for the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental history retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No rental history found for this user")
    })
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
        return ResponseEntity.ok(RentalMapper.toDtoList(rentalHistory, fileStorageService));
    }
}