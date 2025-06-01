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
import za.ac.cput.domain.enums.AuthProvider;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.service.IUserService;

import java.util.*;

/**
 * BackendApplication.java
 * Main class for the OnTheGoRentals backend application.
 * Initializes Spring Boot and includes a CommandLineRunner for default data seeding.
 * The seeding process is designed to be idempotent:
 * - Creates roles (USER, ADMIN, SUPERADMIN) if they don't exist.
 * - Creates corresponding default users (e.g., user@gmail.com) if they don't exist.
 * - If a default user exists but is soft-deleted, it reactivates the user.
 * - Ensures default users have their respective roles.
 *
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@SpringBootApplication
public class BackendApplication {

    private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        log.info("BackendApplication started successfully.");
    }

    @Bean
    @Transactional
    CommandLineRunner run(IUserService userService,
                          IRoleRepository roleRepository,
                          IUserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        return args -> {
            log.info("CommandLineRunner: Initializing default roles and users idempotently...");

            List<RoleName> roleNamesToEnsure = Arrays.asList(RoleName.USER, RoleName.ADMIN, RoleName.SUPERADMIN);

            for (RoleName roleNameEnum : roleNamesToEnsure) {
                String roleNameString = roleNameEnum.name();
                log.debug("Ensuring role: {}", roleNameString);

                // 1. Ensure Role Exists (Idempotent Role Creation)
                Role roleEntity = roleRepository.findByRoleName(roleNameEnum); // Expects RoleName enum
                if (roleEntity == null) {
                    log.info("Role '{}' not found. Creating role...", roleNameString);
                    Role newRoleObject = new Role(roleNameEnum); // Create with RoleName enum
                    // userService.saveRole persists and returns the managed entity with an ID
                    roleEntity = userService.saveRole(newRoleObject);
                    log.info("Created role: '{}' with ID: {}", roleEntity.getRoleName(), roleEntity.getId());
                } else {
                    log.info("Role '{}' already exists with ID: {}", roleEntity.getRoleName(), roleEntity.getId());
                }

                // 2. Ensure Default User for this Role Exists
                String email = roleNameString.toLowerCase() + "@gmail.com";
                String firstName = "Default-" + roleNameString.toLowerCase() + "-user";
                String plainPassword = roleNameString.toLowerCase() + "password";

                Optional<User> userOpt = userRepository.findByEmail(email); // Find by email regardless of deleted status

                if (userOpt.isPresent()) {
                    User existingUser = userOpt.get();
                    log.info("Default user for role '{}' with email '{}' found. User ID: {}, Deleted: {}",
                            roleNameString, email, existingUser.getId(), existingUser.isDeleted());

                    boolean userNeedsSave = false;
                    if (existingUser.isDeleted()) {
                        log.info("Reactivating deleted default user: {}", email);
                        existingUser.setDeleted(false); // Directly modify the managed entity
                        userNeedsSave = true;
                    }

                    final Role finalRoleEntity = roleEntity; // Must be effectively final for stream
                    List<Role> currentUserRoles = existingUser.getRoles();
                    // Ensure roles list is initialized
                    if (currentUserRoles == null) {
                        currentUserRoles = new ArrayList<>();
                        existingUser.setRoles(currentUserRoles);
                    }
                    boolean hasRole = currentUserRoles.stream()
                            .anyMatch(r -> r.getId() != null && r.getId().equals(finalRoleEntity.getId()));

                    if (!hasRole) {
                        log.info("Default user '{}' is missing role '{}'. Adding role.", email, roleNameString);
                        currentUserRoles.add(finalRoleEntity); // Add the managed Role entity
                        userNeedsSave = true;
                    }

                    if (userNeedsSave) {
                        // Use userService.update if you want full update logic (like password re-encoding if changed),
                        // or userService.saveUser for a direct save of the modified managed entity.
                        // Since only 'deleted' and 'roles' are changed here, saveUser is fine.
                        userService.saveUser(existingUser);
                        log.info("Updated existing default user: {}", email);
                    }

                } else {
                    log.info("Default user for role '{}' with email '{}' not found. Creating...", roleNameString, email);

                    List<Role> rolesForNewUser = Collections.singletonList(roleEntity); // Contains the managed roleEntity

                    User newUserDetails = User.builder()
                            .firstName(firstName)
                            .lastName("User") // Default last name
                            .email(email)
                            .password(plainPassword) // userService.createUser will encode this
                            .authProvider(AuthProvider.LOCAL) // Explicitly set default
                            .deleted(false)                  // Explicitly set
                            // roles will be passed as the second parameter to userService.createUser
                            .build();
                    // UUID is typically handled by User entity's @PrePersist

                    try {
                        // UserServiceImpl.createUser is responsible for encoding password, setting roles,
                        // ensuring UUID, deleted=false, and other @PrePersist logic via repository.save().
                        User createdUser = userService.createUser(newUserDetails, rolesForNewUser);
                        log.info("Created default user '{}' with email '{}' and assigned role '{}'. User ID: {}",
                                createdUser.getFirstName(), createdUser.getEmail(), roleNameString, createdUser.getId());
                    } catch (Exception e) {
                        log.error("Error creating default user for role '{}' with email '{}': {}",
                                roleNameString, email, e.getMessage(), e);
                    }
                }
            }
            log.info("CommandLineRunner: Default roles and users initialization process complete.");
        };
    }
}