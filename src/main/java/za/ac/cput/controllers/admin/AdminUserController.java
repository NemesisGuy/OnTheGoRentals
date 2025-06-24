package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.domain.dto.request.UserCreateDTO;
import za.ac.cput.domain.dto.request.UserUpdateDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.ImageType;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.service.IFileStorageService;
import za.ac.cput.service.IRoleService;
import za.ac.cput.service.IUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AdminUserController.java
 * Controller for administrators to manage User entities.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.1
 */
@Tag(name = "Admin: User Management", description = "APIs for administrators to manage users")
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final IUserService userService;
    private final IRoleService roleService;
    private final IRoleRepository roleRepository;
    private final IFileStorageService fileStorageService;
    private final String publicApiUrl; // <-- Add this field

    /**
     * Constructs an AdminUserController with necessary service, repository, and configuration dependencies.
     *
     * @param userService        The user service.
     * @param roleService        The role service.
     * @param roleRepository     The role repository.
     * @param fileStorageService The file storage service.
     * @param publicApiUrl       The public base URL of the API.
     */
    @Autowired
    public AdminUserController(
            IUserService userService,
            IRoleService roleService,
            IRoleRepository roleRepository,
            IFileStorageService fileStorageService,
            @Value("${app.public-api-url}") String publicApiUrl // <-- Inject the property
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.fileStorageService = fileStorageService;
        this.publicApiUrl = publicApiUrl; // <-- Initialize it
        log.info("AdminUserController initialized.");
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsersAdmin() {
        log.info("Admin request to get all users.");
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        // THE FIX: Pass publicApiUrl to the mapper
        return ResponseEntity.ok(UserMapper.toDtoList(users, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Get user by UUID")
    @GetMapping("/{userUuid}")
    public ResponseEntity<UserResponseDTO> getUserByUuidAdmin(@PathVariable UUID userUuid) {
        log.info("Admin request to get user by UUID: {}", userUuid);
        User user = userService.read(userUuid);
        // THE FIX: Pass publicApiUrl to the mapper
        return ResponseEntity.ok(UserMapper.toDto(user, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUserByAdmin(@Valid @RequestBody UserCreateDTO userCreateDto) {
        log.info("Admin request to create a new user with DTO: {}", userCreateDto);
        List<Role> resolvedRoles = roleService.resolveRoles(userCreateDto.getRoleNames());
        User userToCreate = UserMapper.toEntity(userCreateDto);
        User createdUser = userService.createUser(userToCreate, resolvedRoles);
        log.info("Successfully created user with UUID: {}", createdUser.getUuid());
        // THE FIX: Pass publicApiUrl to the mapper
        return new ResponseEntity<>(UserMapper.toDto(createdUser, fileStorageService, publicApiUrl), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a user")
    @PutMapping("/{userUuid}")
    public ResponseEntity<UserResponseDTO> updateUserByAdmin(@PathVariable UUID userUuid, @Valid @RequestBody UserUpdateDTO userUpdateDto) {
        log.info("Admin request to update user with UUID: {}", userUuid);
        User existingUser = userService.read(userUuid);
        User userWithUpdates = UserMapper.applyUpdateDtoToEntity(userUpdateDto, existingUser);
        User persistedUser = userService.update(existingUser.getId(), userWithUpdates);
        log.info("Successfully updated user with UUID: {}", persistedUser.getUuid());
        // THE FIX: Pass publicApiUrl to the mapper
        return ResponseEntity.ok(UserMapper.toDto(persistedUser, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Upload user profile image")
    @PostMapping("/{userUuid}/profile-image")
    public ResponseEntity<UserResponseDTO> uploadUserProfileImageByAdmin(@PathVariable UUID userUuid, @RequestParam("file") MultipartFile file) {
        log.info("Admin request to upload profile image for user UUID: {}", userUuid);
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image file cannot be empty.");
        }
        User existingUser = userService.read(userUuid);
        String fileKey = fileStorageService.save(file, ImageType.SELFIE.getFolder());
        String filename = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        User imageUpdatePayload = existingUser.toBuilder()
                .profileImageFileName(filename)
                .profileImageType(ImageType.SELFIE.getFolder())
                .profileImageUploadedAt(LocalDateTime.now())
                .build();
        User updatedUser = userService.update(existingUser.getId(), imageUpdatePayload);
        log.info("Admin successfully uploaded profile image for user UUID [{}].", userUuid);
        // THE FIX: Pass publicApiUrl to the mapper
        return ResponseEntity.ok(UserMapper.toDto(updatedUser, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{userUuid}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable UUID userUuid) {
        log.info("Admin request to delete user with UUID: {}", userUuid);
        User userToDelete = userService.read(userUuid);
        userService.delete(userToDelete.getId());
        log.info("Successfully soft-deleted user with UUID: {}.", userUuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all roles")
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        log.info("Admin request to get all available roles.");
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }
}