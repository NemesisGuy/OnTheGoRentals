// src/main/java/za/ac/cput/DefaultDataInitializer.java
package za.ac.cput.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.AuthProvider;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.IUserService;

import java.util.*;

@Component
public class DefaultDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataInitializer.class);

    private final IUserService userService;
    private final IRoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultDataInitializer(IUserService userService,
                                  IRoleRepository roleRepository,
                                  UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        System.out.println("RUNNER --- DefaultDataInitializer Constructor ---");
        System.out.println("RUNNER - Injected IUserService hash (constructor): " + System.identityHashCode(this.userService));
        System.out.println("RUNNER - Injected IRoleRepository hash (constructor): " + System.identityHashCode(this.roleRepository));
        System.out.println("RUNNER - Injected IUserRepository hash (constructor): " + System.identityHashCode(this.userRepository));
        System.out.println("RUNNER - Injected PasswordEncoder hash (constructor): " + System.identityHashCode(this.passwordEncoder));
        System.out.println("RUNNER --- End of DefaultDataInitializer Constructor ---");
    }

    // @Transactional // Keep commented for this test iteration
    @Override
    public void run(String... args) throws Exception {
        System.out.println("RUNNER --- DefaultDataInitializer run() method ---");
        log.info("CommandLineRunner (DefaultDataInitializer): Initializing default roles and users idempotently...");
        System.out.println("RUNNER - IUserService hash (run method): " + System.identityHashCode(this.userService));

        List<RoleName> roleNamesToEnsure = Arrays.asList(RoleName.USER, RoleName.ADMIN, RoleName.SUPERADMIN);

        for (RoleName roleNameEnum : roleNamesToEnsure) {
            String roleNameString = roleNameEnum.name();
            log.debug("Ensuring role: {}", roleNameString);

            Role roleEntity = this.roleRepository.findByRoleName(roleNameEnum);
            if (roleEntity == null) {
                log.info("Role '{}' not found. Creating role...", roleNameString);
                Role newRoleObject = new Role(roleNameEnum);

                System.out.println("RUNNER - Before calling saveRole for: " + roleNameEnum.name());
                if (newRoleObject != null) {
                    System.out.println("RUNNER - newRoleObject.getRoleNameEnum() = " + (newRoleObject.getRoleNameEnum() != null ? newRoleObject.getRoleNameEnum().name() : "null from getter"));
                    System.out.println("RUNNER - newRoleObject hash for saveRole: " + System.identityHashCode(newRoleObject));
                } else {
                    System.out.println("RUNNER - newRoleObject IS NULL before saveRole call!");
                }

                roleEntity = this.userService.saveRole(newRoleObject); // This is the call we are testing
                log.info("RUNNER: After userService.saveRole, roleEntity is: " + roleEntity +
                        (roleEntity != null ? ", ID: " + roleEntity.getId() + ", RoleName: " + (roleEntity.getRoleNameEnum() != null ? roleEntity.getRoleNameEnum().name() : "null enum in returned entity") : ""));


                if (roleEntity == null || roleEntity.getId() == null) {
                    log.error("CRITICAL: Failed to save role '{}'. Seeding cannot continue reliably.", roleNameEnum.name());
                    continue;
                }
                log.info("Created role: '{}' with ID: {}", roleEntity.getRoleNameEnum(), roleEntity.getId());
            } else {
                log.info("Role '{}' already exists with ID: {}", roleEntity.getRoleNameEnum(), roleEntity.getId());
            }

            String email = roleNameString.toLowerCase() + "@gmail.com";
            String firstName = "Default-" + roleNameString.toLowerCase() + "-user";
            String plainPassword = roleNameString.toLowerCase() + "password";

            Optional<User> userOpt = this.userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                User existingUser = userOpt.get();
                log.info("Default user for role '{}' with email '{}' found. User ID: {}, Deleted: {}",
                        roleNameString, email, existingUser.getId(), existingUser.isDeleted());

                boolean userNeedsSave = false;
                if (existingUser.isDeleted()) {
                    log.info("Reactivating deleted default user: {}", email);
                    existingUser.setDeleted(false);
                    userNeedsSave = true;
                }

                final Role finalRoleEntity = roleEntity;
                List<Role> currentUserRoles = existingUser.getRoles();
                if (currentUserRoles == null) {
                    currentUserRoles = new ArrayList<>();
                    existingUser.setRoles(currentUserRoles);
                }

                if (finalRoleEntity != null && finalRoleEntity.getId() != null) {
                    boolean hasRole = currentUserRoles.stream()
                            .anyMatch(r -> r.getId() != null && r.getId().equals(finalRoleEntity.getId()));
                    if (!hasRole) {
                        log.info("Default user '{}' is missing role '{}'. Adding role.", email, roleNameString);
                        currentUserRoles.add(finalRoleEntity);
                        userNeedsSave = true;
                    }
                } else {
                    log.warn("Cannot assign role '{}' to user '{}' because the role entity or its ID is null. Skipping role check.", roleNameString, email);
                }

                if (userNeedsSave) {
                    this.userService.saveUser(existingUser);
                    log.info("Updated existing default user: {}", email);
                }

            } else {
                if (roleEntity == null || roleEntity.getId() == null) {
                    log.error("Cannot create default user for role '{}' with email '{}' because its corresponding role entity is invalid.", roleNameString, email);
                    continue;
                }

                log.info("Default user for role '{}' with email '{}' not found. Creating...", roleNameString, email);
                List<Role> rolesForNewUser = Collections.singletonList(roleEntity);
                User newUserDetails = User.builder()
                        .firstName(firstName)
                        .lastName("User")
                        .email(email)
                        .password(plainPassword)
                        .authProvider(AuthProvider.LOCAL)
                        .deleted(false)
                        .build();
                try {
                    User createdUser = this.userService.createUser(newUserDetails, rolesForNewUser);
                    log.info("Created default user '{}' with email '{}' and assigned role '{}'. User ID: {}",
                            createdUser.getFirstName(), createdUser.getEmail(), roleNameString, createdUser.getId());
                } catch (Exception e) {
                    log.error("Error creating default user for role '{}' with email '{}': {}",
                            roleNameString, email, e.getMessage(), e);
                }
            }
        }
        log.info("CommandLineRunner (DefaultDataInitializer): Default roles and users initialization process complete.");
        System.out.println("RUNNER --- End of DefaultDataInitializer run() method ---");
    }
}