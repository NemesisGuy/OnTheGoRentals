package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.UserCreateDTO;
import za.ac.cput.domain.dto.request.UserUpdateDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.ResourceNotFoundException; // Assuming this exists
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.service.IRoleService;
import za.ac.cput.service.IUserService;

import java.util.List;
import java.util.Set; // For roles if User entity uses Set<Role>
import java.util.UUID;
import java.util.stream.Collectors; // For logging role names

/**
 * AdminUserController.java
 * Controller for administrators to manage User entities.
 * Allows admins to create, retrieve, update, and delete users, and manage user roles.
 * External identification of users is by UUID. Internal service operations
 * primarily use integer IDs. This controller bridges that gap.
 *
 * Author: [Original Author Name] // Please specify if different
 * Updated by: System/AI
 * Date: [Original Date]
 * Updated: [Current Date - e.g., 2024-05-28]
 */
@RestController
@RequestMapping("/api/v1/admin/users")
// @CrossOrigin(...)
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final IUserService userService;
    private final IRoleService roleService;
    private final IRoleRepository roleRepository; // For direct access in /roles endpoint

    /**
     * Constructs an AdminUserController with necessary service and repository dependencies.
     *
     * @param userService    The user service.
     * @param roleService    The role service (for resolving and managing roles).
     * @param roleRepository The role repository (for listing all available roles).
     */
    @Autowired
    public AdminUserController(IUserService userService, IRoleService roleService, IRoleRepository roleRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        log.info("AdminUserController initialized.");
    }

    /**
     * Retrieves all users for administrative view.
     * Depending on the service implementation, this might include users
     * with various statuses (e.g., active, inactive, soft-deleted).
     *
     * @return A ResponseEntity containing a list of {@link UserResponseDTO}s.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsersAdmin() {
        log.info("Admin request to get all users.");
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            log.info("No users found.");
            // Consider ResponseEntity.noContent().build() if an empty list is a "no content" scenario
            // For now, returning an empty list with 200 OK is also acceptable.
            return ResponseEntity.ok(List.of()); // Or UserMapper.toDtoList(users) which will be empty
        }
        List<UserResponseDTO> dtoList = UserMapper.toDtoList(users);
        log.info("Successfully retrieved {} users.", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Retrieves a specific user by their UUID for administrative view.
     *
     * @param userUuid The UUID of the user to retrieve.
     * @return A ResponseEntity containing the {@link UserResponseDTO} if found.
     * @throws ResourceNotFoundException if the user with the given UUID is not found (handled by service).
     */
    @GetMapping("/{userUuid}")
    public ResponseEntity<UserResponseDTO> getUserByUuidAdmin(@PathVariable UUID userUuid) {
        log.info("Admin request to get user by UUID: {}", userUuid);
        User userEntity = userService.read(userUuid);
        // userService.read(UUID) is expected to throw ResourceNotFoundException if not found.
        log.info("Successfully retrieved user with ID: {} for UUID: {}", userEntity.getId(), userEntity.getUuid());
        return ResponseEntity.ok(UserMapper.toDto(userEntity));
    }

    /**
     * Allows an admin to create a new user.
     * This method involves resolving role names from the DTO to {@link Role} entities
     * before calling the user service to create the user.
     *
     * @param userCreateDto The {@link UserCreateDTO} containing details for the new user, including role names.
     * @return A ResponseEntity containing the created {@link UserResponseDTO} and HTTP status CREATED.
     * @throws ResourceNotFoundException if any specified roles are not found.
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUserByAdmin(@Valid @RequestBody UserCreateDTO userCreateDto) {
        log.info("Admin request to create a new user with DTO: {}", userCreateDto);

        log.debug("Resolving roles from role names: {}", userCreateDto.getRoleNames());
        List<Role> resolvedRoles = roleService.resolveRoles(userCreateDto.getRoleNames());
        // roleService.resolveRoles should throw if roles not found or handle appropriately.
        log.debug("Resolved {} roles.", resolvedRoles.size());

        User userToCreate = UserMapper.toEntity(userCreateDto); // Password is plain here
        log.debug("Mapped DTO to User entity (pre-role assignment, pre-password encoding): {}", userToCreate);

        // The userService.createUser method is expected to handle password encoding.
        User createdUserEntity = userService.createUser(userToCreate, resolvedRoles);
        log.info("Successfully created user with ID: {} and UUID: {}", createdUserEntity.getId(), createdUserEntity.getUuid());

        UserResponseDTO responseDto = UserMapper.toDto(createdUserEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Allows an admin to update an existing user identified by their UUID.
     * The service layer is responsible for handling password encoding if the password is changed,
     * and for managing role updates.
     *
     * @param userUuid      The UUID of the user to update.
     * @param userUpdateDto The {@link UserUpdateDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link UserResponseDTO}.
     * @throws ResourceNotFoundException if the user with the given UUID is not found.
     */
    @PutMapping("/{userUuid}")
    public ResponseEntity<UserResponseDTO> updateUserByAdmin(
            @PathVariable UUID userUuid,
            @Valid @RequestBody UserUpdateDTO userUpdateDto
    ) {
        log.info("Admin request to update user with UUID: {}. Update DTO: {}", userUuid, userUpdateDto);
        User existingUser = userService.read(userUuid);
        log.debug("Found existing user with ID: {} and UUID: {}", existingUser.getId(), existingUser.getUuid());

        User userWithUpdates = UserMapper.applyUpdateDtoToEntity(userUpdateDto, existingUser);
        log.debug("Applied DTO updates to User entity (pre-service update): {}", userWithUpdates);

        // The userService.update method is expected to handle password encoding if password changed,
        // and to manage updates to roles if they are part of UserUpdateDTO and userWithUpdates.
        // The signature userService.update(id, entity) implies the ID from the path/existing entity is used
        // to identify the record, and 'userWithUpdates' contains the new state.
        User persistedUser = userService.update(existingUser.getId(), userWithUpdates);
        log.info("Successfully updated user with ID: {} and UUID: {}", persistedUser.getId(), persistedUser.getUuid());

        UserResponseDTO responseDto = UserMapper.toDto(persistedUser);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Allows an admin to soft-delete a user by their UUID.
     * The controller first retrieves the user by UUID to obtain their internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param userUuid The UUID of the user to delete.
     * @return A ResponseEntity with no content if successful, or not found if the user doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the user with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{userUuid}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable UUID userUuid) {
        log.info("Admin request to delete user with UUID: {}", userUuid);
        User userToDelete = userService.read(userUuid);
        log.debug("Found user with ID: {} (UUID: {}) for deletion.", userToDelete.getId(), userToDelete.getUuid());

        boolean deleted = userService.delete(userToDelete.getId());
        if (!deleted) {
            log.warn("User with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", userToDelete.getId(), userToDelete.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully soft-deleted user with ID: {} (UUID: {}).", userToDelete.getId(), userToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a list of all available roles in the system.
     * Note: This endpoint currently returns a list of {@link Role} entities directly.
     * For consistency with other API responses, consider creating a {@code RoleResponseDTO}
     * and using a {@code RoleMapper} to transform the entities before sending them in the response.
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
        // Logging role names for quick overview
        if (log.isDebugEnabled()) {
            String roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.joining(", "));
            log.debug("Retrieved roles: [{}]", roleNames);
        }
        return ResponseEntity.ok(roles);
    }
}