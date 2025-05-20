package za.ac.cput.controllers.admin;

/**
 * AdminUserController.java
 * Controller for user entity management
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.UserDTO;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.security.Role;
import za.ac.cput.domain.security.User;
import za.ac.cput.exception.RoleNotFoundException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.service.impl.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users as UserDTO (no passwords exposed)
    @GetMapping("/list/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(UserMapper.toDto(user));
        }
        return ResponseEntity.ok(userDTOs);
    }

    // Get user by id, returns UserDTO or 404
    @GetMapping("/read/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        User user = userService.read(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    // Create new user with role validation
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        List<Role> matchingRoles = validateAndGetRoles(user.getRoles());
        user.setRoles(matchingRoles);
        User createdUser = userService.create(user);
        return ResponseEntity.ok(UserMapper.toDto(createdUser));
    }

    // Update existing user by ID with role validation
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        User existingUser = userService.read(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        List<Role> matchingRoles = validateAndGetRoles(updatedUser.getRoles());
        existingUser.setRoles(matchingRoles);

        User savedUser = userService.update(existingUser);
        return ResponseEntity.ok(UserMapper.toDto(savedUser));
    }

    // Delete user by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        boolean deleted = userService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // List all roles available in the system
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }



    // Helper to validate requested roles against DB roles, throws exception if none found
    private List<Role> validateAndGetRoles(List<Role> requestedRoles) {
        List<Role> allRoles = roleRepository.findAll();
        List<Role> matchingRoles = new ArrayList<>();

        for (Role reqRole : requestedRoles) {
            allRoles.stream()
                    .filter(dbRole -> dbRole.getRoleName().equals(reqRole.getRoleName()))
                    .findFirst()
                    .ifPresent(matchingRoles::add);
        }

        if (matchingRoles.isEmpty()) {
            throw new RoleNotFoundException("Desired role(s) not found");
        }
        return matchingRoles;
    }
}
