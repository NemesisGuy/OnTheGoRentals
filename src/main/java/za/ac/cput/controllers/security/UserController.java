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

    @Autowired
    public UserController(IUserService userService, IRentalService rentalService, IFileStorageService fileStorageService) {
        this.userService = userService;
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        log.info("UserController initialized.");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] requesting their profile.", userEmail);

        User user = userService.read(userEmail);
        // Use the injected mapper instance to call the method
        return ResponseEntity.ok(UserMapper.toDto(user, fileStorageService));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] requesting to update their profile with DTO: {}", userEmail, userUpdateDTO);

        User currentUser = userService.read(userEmail);
        // The applyUpdateDtoToEntity method is static and can be called directly
        User userWithUpdates = UserMapper.applyUpdateDtoToEntity(userUpdateDTO, currentUser);

        User updatedUser = userService.update(currentUser.getId(), userWithUpdates);
        // Use the injected mapper instance for the response
        return ResponseEntity.ok(UserMapper.toDto(updatedUser, fileStorageService));
    }

    @PostMapping("/profile-image")
    public ResponseEntity<UserResponseDTO> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String userEmail = SecurityUtils.getRequesterIdentifier();
        log.info("User [{}] is uploading a profile image.", userEmail);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image file cannot be empty.");
        }

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

        log.info("Successfully uploaded profile image '{}' for user [{}].", filename, userEmail);
        // Use the injected mapper instance for the final response
        return ResponseEntity.ok(UserMapper.toDto(updatedUser, fileStorageService));

    }

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
        // RentalMapper can remain static if it doesn't need external services
        return ResponseEntity.ok(RentalMapper.toDtoList(rentalHistory, fileStorageService));
    }
}