package za.ac.cput.controllers.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@RestController
@RequestMapping("/api/v1/users/me")
@Tag(name = "User Profile", description = "Endpoints for authenticated users to manage their own profile and related data.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;
    private final IRentalService rentalService;
    private final IFileStorageService fileStorageService;
    private final String publicApiUrl; // <-- Add this field

    /**
     * Constructs the controller with necessary service dependencies and configuration properties.
     *
     * @param userService        The service for user-related operations.
     * @param rentalService      The service for rental history lookups.
     * @param fileStorageService The service for handling file storage.
     * @param publicApiUrl       The public base URL of the API, injected from application properties.
     */
    @Autowired
    public UserController(
            IUserService userService,
            IRentalService rentalService,
            IFileStorageService fileStorageService,
            @Value("${app.public-api-url}") String publicApiUrl // <-- Inject the property
    ) {
        this.userService = userService;
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        this.publicApiUrl = publicApiUrl; // <-- Initialize it
        log.info("UserController initialized.");
    }

    @Operation(summary = "Get current user profile", description = "Retrieves the profile information of the currently authenticated user.")
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        User user = userService.read(userEmail);
        // THE FIX: Pass the publicApiUrl to the mapper
        return ResponseEntity.ok(UserMapper.toDto(user, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Update current user profile", description = "Updates the profile information of the currently authenticated user.")
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        User currentUser = userService.read(userEmail);
        User userWithUpdates = UserMapper.applyUpdateDtoToEntity(userUpdateDTO, currentUser);
        User updatedUser = userService.update(currentUser.getId(), userWithUpdates);
        // THE FIX: Pass the publicApiUrl to the mapper
        return ResponseEntity.ok(UserMapper.toDto(updatedUser, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Upload profile image", description = "Uploads or replaces the profile image for the currently authenticated user.")
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image file cannot be empty.");
        }
        String userEmail = SecurityUtils.getRequesterIdentifier();
        User currentUser = userService.read(userEmail);
        String fileKey = fileStorageService.save(file, ImageType.SELFIE.getFolder());
        String filename = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        User imageUpdatePayload = currentUser.toBuilder()
                .password(null)
                .profileImageFileName(filename)
                .profileImageType(ImageType.SELFIE.getFolder())
                .profileImageUploadedAt(LocalDateTime.now())
                .build();

        User updatedUser = userService.update(currentUser.getId(), imageUpdatePayload);
        // THE FIX: Pass the publicApiUrl to the mapper
        return ResponseEntity.ok(UserMapper.toDto(updatedUser, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Get user rental history", description = "Retrieves the rental history for the currently authenticated user.")
    @GetMapping("/rental-history")
    public ResponseEntity<List<RentalResponseDTO>> getCurrentUserRentalHistory() {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        User currentUser = userService.read(userEmail);
        List<Rental> rentalHistory = rentalService.getRentalHistoryByUser(currentUser);

        if (rentalHistory.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // THE FIX: Pass the publicApiUrl to the mapper
        return ResponseEntity.ok(RentalMapper.toDtoList(rentalHistory, fileStorageService, publicApiUrl));
    }
}