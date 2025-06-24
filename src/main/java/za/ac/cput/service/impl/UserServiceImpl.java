package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.AuthProvider;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.IRefreshTokenService;
import za.ac.cput.service.IUserService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserServiceImpl.java
 * Implementation of the {@link IUserService} interface.
 * Manages User entity CRUD operations and related data management,
 * such as role persistence and password encoding.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenService refreshTokenService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           IRoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           IRefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        log.info("UserServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User userDetails, List<Role> roles) {
        String email = userDetails.getEmail();
        log.info("Attempting to create new user. Email: '{}'", email);

        if (userRepository.existsByEmail(email)) {
            log.warn("User creation failed for email '{}'. Email already exists.", email);
            throw new EmailAlreadyExistsException("Email " + email + " is already taken!");
        }

        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        log.debug("Password encoded for new user '{}'", email);

        if (roles == null || roles.isEmpty()) {
            log.warn("No roles provided for new user '{}'. Assigning default USER role.", email);
            Role defaultUserRole = roleRepository.findByRoleName(RoleName.USER);
            if (defaultUserRole == null) {
                log.error("CRITICAL - Default USER role not found in database. Cannot assign default role.");
                throw new IllegalStateException("Default USER role not found. System configuration error.");
            }
            userDetails.setRoles(Collections.singletonList(defaultUserRole));
        } else {
            userDetails.setRoles(roles);
        }

        User savedUser = userRepository.save(userDetails);
        log.info("Successfully created user. ID: {}, UUID: '{}', Email: '{}'",
                savedUser.getId(), savedUser.getUuid(), savedUser.getEmail());
        return savedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User read(Integer id) {
        log.debug("Attempting to read user by internal ID: {}", id);
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User read(UUID uuid) {
        log.debug("Attempting to read user by UUID: '{}'", uuid);
        return userRepository.findByUuidAndDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with UUID: " + uuid));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User read(String email) {
        log.debug("Attempting to read user by email: '{}'", email);
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }


    /**
     * Updates a user's information. This method is now robustly designed to handle
     * "sparse" or "partial" update objects. It only modifies fields that are explicitly
     * provided (not null) in the userUpdates object. This is the single source of truth
     * for update logic and prevents accidental password corruption.
     */
    @Override
    public User update(int userId, User userUpdates) {
        log.info("Attempting to update user with internal ID: {}", userId);
        User existingUser = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for update with id: " + userId));

        boolean needsSave = false;

        // --- Standard Field Updates ---
        if (userUpdates.getFirstName() != null && !userUpdates.getFirstName().equals(existingUser.getFirstName())) {
            log.debug("User ID {}: Updating first name.", userId);
            existingUser.setFirstName(userUpdates.getFirstName());
            needsSave = true;
        }
        if (userUpdates.getLastName() != null && !userUpdates.getLastName().equals(existingUser.getLastName())) {
            log.debug("User ID {}: Updating last name.", userId);
            existingUser.setLastName(userUpdates.getLastName());
            needsSave = true;
        }

        // --- THE DEFINITIVE PASSWORD FIX ---
        // This logic now correctly handles all cases.
        // It checks if the `userUpdates` object contains a password field.
        // A password will ONLY be present if it came from a DTO with an explicit password set.
        if (userUpdates.getPassword() != null && !userUpdates.getPassword().isEmpty()) {

            // This is a new, RAW password. We only update if it's different from the current one.
            if (!passwordEncoder.matches(userUpdates.getPassword(), existingUser.getPassword())) {
                log.info("User ID {}: New password provided. Encoding and updating.", userId);
                existingUser.setPassword(passwordEncoder.encode(userUpdates.getPassword()));
                needsSave = true;
            } else {
                log.info("User ID {}: Password provided matches existing password. No update performed.", userId);
            }
        } else {
            // This log will now correctly fire for image uploads and profile updates without a password change.
            log.info("User ID {}: No new password provided in this update operation.", userId);
        }

        // --- Profile Image Update Logic ---
        if (userUpdates.getProfileImageFileName() != null) {
            log.debug("User ID {}: Updating profile image.", userId);
            // Optional: You can add logic here to delete the old file if it exists
            // fileStorageService.delete(existingUser.getProfileImageFileName());

            existingUser.setProfileImageFileName(userUpdates.getProfileImageFileName());
            existingUser.setProfileImageType(userUpdates.getProfileImageType());
            existingUser.setProfileImageUploadedAt(userUpdates.getProfileImageUploadedAt());
            needsSave = true;
        }

        // --- Other fields ---
        if (userUpdates.getRoles() != null && !userUpdates.getRoles().isEmpty()) {
            log.debug("User ID {}: Updating roles.", userId);

            List<Role> newRoles = userUpdates.getRoles().stream()
                    .map(tempRole -> {
                        // Get the enum name from the temporary role object passed by the mapper.
                        RoleName roleNameEnum = tempRole.getRoleNameEnum();

                        // Find the managed Role entity in the database. This returns Role or null.
                        Role foundRole = roleRepository.findByRoleName(roleNameEnum);

                        // Manually check for null and throw if the role doesn't exist in the DB.
                        if (foundRole == null) {
                            throw new ResourceNotFoundException("Role not found: " + roleNameEnum.name());
                        }
                        return foundRole;
                    })
                    .collect(Collectors.toList());

            // Replace the existing roles with the new set of managed roles.
            existingUser.setRoles(newRoles);
            needsSave = true;
        }

        if (needsSave) {
            User savedUser = userRepository.save(existingUser);
            log.info("Successfully persisted updates for user ID: {}", savedUser.getId());
            return savedUser;
        } else {
            log.info("No updatable changes were detected for user ID: {}. No action taken.", userId);
            return existingUser;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int userId) {
        log.info("Attempting to soft-delete user with internal ID: {}", userId);
        return userRepository.findByIdAndDeletedFalse(userId).map(user -> {
            user.setDeleted(true);
            userRepository.save(user);
            refreshTokenService.deleteByUserId(userId);
            log.info("Successfully soft-deleted user ID: {} and invalidated tokens.", userId);
            return true;
        }).orElse(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getAll() {
        log.debug("Fetching all non-deleted users.");
        return userRepository.findByDeletedFalse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role saveRole(Role role) {
        log.debug("Saving role: '{}'", role.getRoleName());
        return roleRepository.save(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(User user) {
        log.debug("Executing generic save for user with email: '{}'", user.getEmail());
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public User save(User user) {
        log.warn("Deprecated save(User) method called. Delegating to saveUser(User).");
        return saveUser(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if user exists by email: '{}'", email);
        boolean userExists = userRepository.existsByEmail(email);
        log.info("User existence check by email '{}': {}", email, userExists);
        return userExists;
    }

    /**
     * Processes the OAuth2 post-login logic.
     * This method is called after a successful OAuth2 authentication.
     * It should find an existing user or create a new one based on the provided details.
     *
     * @param email     The email address of the authenticated user.
     * @param firstName The first name of the authenticated user.
     * @param lastName  The last name of the authenticated user.
     * @return The {@link User} entity representing the authenticated user.
     */
    @Override
    public User processOAuthPostLogin(String email, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            log.info("OAuth user found in DB: {}", email);
            // Optionally update user's name if it has changed in Google
            User existingUser = read(email);
            existingUser.setFirstName(firstName);
            existingUser.setLastName(lastName);
            return userRepository.save(existingUser);
        } else {
            log.info("OAuth user not found. Creating new user: {}", email);
            User newUser = User.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Generate random password
                    .authProvider(AuthProvider.GOOGLE)
                    .build();

            // Assign default 'USER' role
            Role userRole = roleRepository.findByRoleName(RoleName.USER);
            newUser.setRoles(Collections.singletonList(userRole));

            return userRepository.save(newUser);
        }
    }
}