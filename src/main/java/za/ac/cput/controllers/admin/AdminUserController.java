package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.UserCreateDTO; // Using generic Create DTO
import za.ac.cput.domain.dto.request.UserUpdateDTO; // Using generic Update DTO
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.security.Role;
import za.ac.cput.domain.security.User;
import za.ac.cput.repository.IRoleRepository; // For /roles endpoint
import za.ac.cput.service.IUserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
// @CrossOrigin(...)
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminUserController {

    private final IUserService userService;
    private final IRoleRepository roleRepository; // For listing all roles

    @Autowired
    public AdminUserController(IUserService userService, IRoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsersAdmin() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(UserMapper.toDtoList(users));
    }

    @GetMapping("/{userUuid}")
    public ResponseEntity<UserResponseDTO> getUserByUuidAdmin(@PathVariable UUID userUuid) {
        User userEntity = userService.read(userUuid);
        return ResponseEntity.ok(UserMapper.toDto(userEntity));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUserByAdmin(@Valid @RequestBody UserCreateDTO userCreateDto) {
        User userToCreate = UserMapper.toEntity(userCreateDto); // Password is plain here
        // Service handles password encoding and role entity fetching
        User createdUserEntity = userService.create(userToCreate);
        return new ResponseEntity<>(UserMapper.toDto(createdUserEntity), HttpStatus.CREATED);
    }

    @PutMapping("/{userUuid}")
    public ResponseEntity<UserResponseDTO> updateUserByAdmin(
            @PathVariable UUID userUuid,
            @Valid @RequestBody UserUpdateDTO userUpdateDto // Using generic UserUpdateDTO
    ) {
        User existingUser = userService.read(userUuid); // Fetch current state
        User userWithUpdates = UserMapper.applyUpdateDtoToEntity(userUpdateDto, existingUser); // Create new state

        // Service handles actual update logic, including password encoding if changed, role updates
        User persistedUser = userService.update(userWithUpdates.getId(),userWithUpdates);
        return ResponseEntity.ok(UserMapper.toDto(persistedUser));
    }

    @DeleteMapping("/{userUuid}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable UUID userUuid) {
        User user = userService.read(userUuid); // Fetch current state, throws ResourceNotFoundException if not found
        boolean deleted = userService.delete(user.getId());
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        // This returns Role ENTITIES. Consider RoleResponseDTO and RoleMapper for consistency.
        return ResponseEntity.ok(roleRepository.findAll());
    }
}