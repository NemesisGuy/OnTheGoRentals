package za.ac.cput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.service.IUserService; // Using IUserService for user operations

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * BackendApplication.java
 * Main class for the OnTheGoRentals backend application.
 * Initializes Spring Boot and includes a CommandLineRunner for default data seeding.
 * The seeding process is designed to be idempotent, creating roles (USER, ADMIN, SUPERADMIN)
 * and corresponding default users (e.g., user@gmail.com, admin@gmail.com, superadmin@gmail.com)
 * only if they do not exist. If a default user exists but is marked as deleted,
 * it will be reactivated and ensured to have the correct role.
 *
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
@SpringBootApplication
public class BackendApplication {

    private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

    /**
     * Main method to run the Spring Boot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        log.info("BackendApplication started successfully.");
    }

    /**
     * A {@link CommandLineRunner} bean that executes after the application context is loaded.
     * It ensures that default roles (USER, ADMIN, SUPERADMIN) and corresponding
     * default users are created in the database if they do not already exist.
     * If a default user exists but is marked as deleted, it reactivates the user
     * and ensures they have the appropriate role.
     * This process is transactional to ensure atomicity.
     *
     * @param userService     The service for user and role operations.
     * @param roleRepository  The repository for direct role lookups.
     * @param userRepository  The repository for direct user lookups.
     * @param passwordEncoder The encoder for hashing default user passwords.
     * @return A CommandLineRunner instance.
     */
    @Bean
    @Transactional // Ensures all DB operations within run() are part of a single transaction
    CommandLineRunner run(IUserService userService,
                          IRoleRepository roleRepository,
                          IUserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        return args -> {
            log.info("CommandLineRunner: Initializing default roles and users idempotently...");

            List<RoleName> roleNamesToEnsure = Arrays.asList(RoleName.USER, RoleName.ADMIN, RoleName.SUPERADMIN);

            for (RoleName roleNameEnum : roleNamesToEnsure) {
                String roleNameString = roleNameEnum.name(); // e.g., "USER", "ADMIN"
                log.debug("Ensuring role: {}", roleNameString);

                // 1. Ensure Role Exists (Idempotent Role Creation)
                Role roleEntity = roleRepository.findByRoleName(RoleName.valueOf(roleNameString));
                if (roleEntity == null) {
                    log.info("Role '{}' not found. Creating role...", roleNameString);
                    roleEntity = new Role(roleNameEnum); // Create new Role instance
                    roleEntity = userService.saveRole(roleEntity); // Persist via service
                    log.info("Created role: '{}' with ID: {}", roleEntity.getRoleName(), roleEntity.getId());
                } else {
                    log.info("Role '{}' already exists with ID: {}", roleEntity.getRoleName(), roleEntity.getId());
                }

                // 2. Ensure Default User for this Role Exists (Idempotent User Creation/Update)
                // Using your original naming pattern for default users
                String email = roleNameString.toLowerCase() + "@gmail.com"; // e.g., admin@gmail.com
                String firstName = "Default-" + roleNameString.toLowerCase() + "-user"; // e.g., Default-admin-user
                String plainPassword = roleNameString.toLowerCase() + "password"; // e.g., adminpassword

                Optional<User> userOpt = userRepository.findByEmail(email); // Find by email regardless of deleted status

                if (userOpt.isPresent()) {
                    User existingUser = userOpt.get();
                    log.info("Default user for role '{}' with email '{}' already exists. User ID: {}, Deleted: {}",
                            roleNameString, email, existingUser.getId(), existingUser.isDeleted());

                    boolean userWasModified = false;
                    // If user was deleted, reactivate them
                    if (existingUser.isDeleted()) {
                        log.info("Reactivating deleted default user: {}", email);
                        existingUser.setDeleted(false);
                        userWasModified = true;
                    }

                    // Ensure the user has this specific role
                    final Role finalRoleEntity = roleEntity; // Effectively final for lambda/stream
                    boolean hasRole = existingUser.getRoles() != null && existingUser.getRoles().stream()
                            .anyMatch(r -> r.getId() != null && r.getId().equals(finalRoleEntity.getId()));

                    if (!hasRole) {
                        log.info("Default user '{}' is missing role '{}'. Adding role.", email, roleNameString);
                        if (existingUser.getRoles() == null) { // Safety check
                            existingUser.setRoles(new ArrayList<>());
                        }
                        existingUser.getRoles().add(finalRoleEntity); // Add the managed Role entity
                        userWasModified = true;
                    }

                    if (userWasModified) {
                        userService.saveUser(existingUser); // Persist changes to existingUser
                        log.info("Updated existing default user: {}", email);
                    }

                } else {
                    log.info("Default user for role '{}' with email '{}' not found. Creating...", roleNameString, email);

                    List<Role> userRoles = new ArrayList<>();
                    userRoles.add(roleEntity); // Assign the managed Role entity

                    // Construct the new User entity. Password will be encoded by createUser.
                    User newUserDetails = User.builder()
                            .firstName(firstName)
                            .lastName("User") // Default last name, or use roleNameString if preferred
                            .email(email)
                            .password(plainPassword) // Pass plain password
                            // Let userService.createUser handle roles list assignment if it does,
                            // otherwise, ensure it's set before calling or roles are passed correctly.
                            // .roles(userRoles) // This will be handled by userService.createUser second param
                            .build();
                    // UUID, deleted=false, default AuthProvider are handled by User's @PrePersist
                    // or within the userService.createUser method.

                    try {
                        User createdUser = userService.createUser(newUserDetails, userRoles);
                        log.info("Created default user '{}' with email '{}' and assigned role '{}'. User ID: {}",
                                createdUser.getFirstName(), createdUser.getEmail(), roleNameString, createdUser.getId());
                    } catch (Exception e) {
                        log.error("Error creating default user for role '{}' with email '{}': {}",
                                roleNameString, email, e.getMessage(), e);
                        // Depending on severity, you might rethrow or allow application to continue
                    }
                }
            }
            log.info("CommandLineRunner: Default roles and users initialization process complete.");
        };
    }
}