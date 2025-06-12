package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import za.ac.cput.service.FileStorageService;
import za.ac.cput.service.IRoleService;
import za.ac.cput.service.IUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AdminUserController.java
 * Controller for administrators to manage User entities.
 * Allows admins to create, retrieve, update, delete users, manage roles,
 * and upload profile images on behalf of users.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final IUserService userService;
    private final IRoleService roleService;
    private final IRoleRepository roleRepository;
    private final FileStorageService fileStorageService;

    /**
     * Constructs an AdminUserController with necessary service and repository dependencies.
     *
     * @param userService        The user service.
     * @param roleService        The role service for resolving and managing roles.
     * @param roleRepository     The role repository for listing all available roles.
     * @param fileStorageService The service for handling file uploads.
     */
    @Autowired
    public AdminUserController(IUserService userService, IRoleService roleService, IRoleRepository roleRepository, FileStorageService fileStorageService) {
        this.userService = userService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.fileStorageService = fileStorageService;
        log.info("AdminUserController initialized.");
    }

    /**
     * Retrieves all users for administrative view.
     *
     * @return A ResponseEntity containing a list of {@link UserResponseDTO}s.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsersAdmin() {
        log.info("Admin request to get all users.");
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            log.info("No users found.");
            return ResponseEntity.ok(List.of());
        }
        List<UserResponseDTO> dtoList = UserMapper.toDtoList(users);
        log.info("Successfully retrieved {} users.", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Retrieves a specific user by their UUID for administrative view.
     *
     * @param userUuid The UUID of the user to retrieve.
     * @return A ResponseEntity containing the {@link UserResponseDTO}.
     */
    @GetMapping("/{userUuid}")
    public ResponseEntity<UserResponseDTO> getUserByUuidAdmin(@PathVariable UUID userUuid) {
        log.info("Admin request to get user by UUID: {}", userUuid);
        User user = userService.read(userUuid);
        log.info("Successfully retrieved user with ID: {} for UUID: {}", user.getId(), user.getUuid());
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    /**
     * Allows an admin to create a new user.
     *
     * @param userCreateDto The DTO containing details for the new user.
     * @return A ResponseEntity containing the created {@link UserResponseDTO}.
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUserByAdmin(@Valid @RequestBody UserCreateDTO userCreateDto) {
        log.info("Admin request to create a new user with DTO: {}", userCreateDto);
        List<Role> resolvedRoles = roleService.resolveRoles(userCreateDto.getRoleNames());
        User userToCreate = UserMapper.toEntity(userCreateDto);
        User createdUser = userService.createUser(userToCreate, resolvedRoles);
        log.info("Successfully created user with ID: {} and UUID: {}", createdUser.getId(), createdUser.getUuid());
        return new ResponseEntity<>(UserMapper.toDto(createdUser), HttpStatus.CREATED);
    }

    /**
     * Allows an admin to update an existing user identified by their UUID.
     *
     * @param userUuid      The UUID of the user to update.
     * @param userUpdateDto The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated {@link UserResponseDTO}.
     */
    @PutMapping("/{userUuid}")
    public ResponseEntity<UserResponseDTO> updateUserByAdmin(
            @PathVariable UUID userUuid,
            @Valid @RequestBody UserUpdateDTO userUpdateDto
    ) {
        log.info("Admin request to update user with UUID: {}. Update DTO: {}", userUuid, userUpdateDto);
        User existingUser = userService.read(userUuid);
        User userWithUpdates = UserMapper.applyUpdateDtoToEntity(userUpdateDto, existingUser);

        // The service handles password encoding and role updates if necessary
        User persistedUser = userService.update(existingUser.getId(), userWithUpdates);
        log.info("Successfully updated user with ID: {} and UUID: {}", persistedUser.getId(), persistedUser.getUuid());
        return ResponseEntity.ok(UserMapper.toDto(persistedUser));
    }

    /**
     * [NEW] Allows an admin to upload or replace the profile image for a specific user.
     *
     * @param userUuid The UUID of the user whose profile image is being uploaded.
     * @param file     The image file sent as multipart/form-data.
     * @return A ResponseEntity containing the updated {@link UserResponseDTO} with the new image URL.
     */
    @PostMapping("/{userUuid}/profile-image")
    public ResponseEntity<UserResponseDTO> uploadUserProfileImageByAdmin(
            @PathVariable UUID userUuid,
            @RequestParam("file") MultipartFile file) {
        log.info("Admin request to upload profile image for user UUID: {}", userUuid);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image file cannot be empty.");
        }

        User existingUser = userService.read(userUuid);

        String filename = fileStorageService.save(ImageType.SELFIE.getFolder(), file);

        User.UserBuilder builder = existingUser.toBuilder()
                .profileImageFileName(filename)
                .profileImageType(ImageType.SELFIE.getFolder())
                .profileImageUploadedAt(LocalDateTime.now());

        User updatedUser = userService.update(existingUser.getId(), builder.build());

        log.info("Admin successfully uploaded profile image '{}' for user UUID [{}].", filename, userUuid);
        return ResponseEntity.ok(UserMapper.toDto(updatedUser));
    }

    /**
     * Allows an admin to soft-delete a user by their UUID.
     *
     * @param userUuid The UUID of the user to delete.
     * @return A ResponseEntity with no content.
     */
    @DeleteMapping("/{userUuid}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable UUID userUuid) {
        log.info("Admin request to delete user with UUID: {}", userUuid);
        User userToDelete = userService.read(userUuid);
        userService.delete(userToDelete.getId());
        log.info("Successfully soft-deleted user with ID: {} (UUID: {}).", userToDelete.getId(), userToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a list of all available roles in the system.
     *
     * @return A ResponseEntity containing a list of {@link Role} entities.
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        log.info("Admin request to get all available roles.");
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            log.info("No roles found in the system.");
            return ResponseEntity.noContent().build();
        }
        log.info("Successfully retrieved {} roles.", roles.size());
        return ResponseEntity.ok(roles);
    }
}