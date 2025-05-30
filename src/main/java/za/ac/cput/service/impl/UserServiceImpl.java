package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.repository.IRoleRepository; // For saveRole
import za.ac.cput.service.IRefreshTokenService; // For deleting tokens on user delete
import za.ac.cput.service.IUserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserServiceImpl.java
 * Implementation of the {@link IUserService} interface.
 * Focuses on User entity Create, Read, Update, Delete (CRUD) operations
 * and related user data management tasks, such as role persistence.
 * Authentication-specific logic is handled by a separate {@code AuthServiceImpl}.
 * Note: The User entity uses Lombok's @Builder and does not have a custom .copy() builder method.
 * Updates are handled by fetching the existing entity and selectively setting fields.
 *
 * Author: Peter Buckingham
 * Date: 2025-05-28
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
@Service
@Transactional // Apply transactionality to all public methods by default
public class UserServiceImpl implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenService refreshTokenService;

    /**
     * Constructs the UserServiceImpl with necessary dependencies.
     *
     * @param userRepository      The repository for user persistence.
     * @param roleRepository      The repository for role persistence.
     * @param passwordEncoder     The encoder for user passwords.
     * @param refreshTokenService The service for managing refresh tokens (used during user deletion).
     */
    @Autowired
    public UserServiceImpl(IUserRepository userRepository,
                           IRoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           IRefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        log.info("UserServiceImpl (CRUD focused) initialized.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User userDetails, List<Role> roles) {
        String email = userDetails.getEmail();
        List<String> roleNames = roles != null ? roles.stream().map(Role::getRoleName).collect(Collectors.toList()) : List.of();
        log.info("Attempting to create new user. Email: '{}', Assigned Roles: {}", email, roleNames);

        if (userRepository.existsByEmail(email)) {
            log.warn("User creation failed for email '{}'. Email already exists.", email);
            throw new EmailAlreadyExistsException("Email " + email + " is already taken!");
        }

        // User entity has @PrePersist for UUID, AuthProvider, and default Role if roles list is empty.
        // We still need to encode password and set provided roles.

        // Ensure password from userDetails is encoded
        if (userDetails.getPassword() == null || userDetails.getPassword().isEmpty()) {
            log.error("User creation failed for email '{}': Password cannot be null or empty.", email);
            throw new IllegalArgumentException("Password cannot be null or empty for new user creation.");
        }
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        log.debug("Password encoded for new user '{}'", email);

        // If roles are provided, set them. Otherwise, @PrePersist will handle default.
        if (roles != null && !roles.isEmpty()) {
            userDetails.setRoles(roles); // Roles should be validated and fetched (managed entities) before calling this
        }
        // userDetails.setUuid(null); // Allow @PrePersist to generate UUID if not already set
        // userDetails.setAuthProvider(null); // Allow @PrePersist to set default if not provided
        userDetails.setDeleted(false); // Explicitly set non-deleted

        User savedUser = userRepository.save(userDetails);
        log.info("Successfully created user. ID: {}, UUID: '{}', Email: '{}', Roles: {}",
                savedUser.getId(), savedUser.getUuid(), savedUser.getEmail(),
                savedUser.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));
        return savedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User read(Integer id) {
        log.debug("Attempting to read user by internal ID: {}", id);
        User user = userRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (user == null) {
            log.warn("User not found or is deleted for ID: {}", id);
        } else {
            log.debug("User found for ID: {}. Email: '{}'", id, user.getEmail());
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User read(UUID uuid) {
        log.debug("Attempting to read user by UUID: '{}'", uuid);
        User user = userRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (user == null) {
            log.warn("User not found or is deleted for UUID: '{}'", uuid);
        } else {
            log.debug("User found for UUID: '{}'. Email: '{}'", uuid, user.getEmail());
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User read(String email) {
        log.debug("Attempting to read user by email: '{}'", email);
        User user = userRepository.findByEmailAndDeletedFalse(email).orElse(null);
        if (user == null) {
            log.warn("User not found or is deleted for email: '{}'", email);
        } else {
            log.debug("User found for email: '{}'. ID: {}", email, user.getId());
        }
        return user;
    }

    /**
     * {@inheritDoc}
     * Updates are applied by fetching the existing User entity and then selectively
     * setting fields from the {@code userUpdates} parameter on the fetched entity.
     * The User entity uses Lombok's @Builder, so direct field setting is used here.
     */
    @Override
    public User update(int userId, User userUpdates) {
        log.info("Attempting to update user with internal ID: {}", userId);
        User existingUser = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> {
                    log.warn("Update failed. User not found or is deleted with ID: {}", userId);
                    return new ResourceNotFoundException("User not found for update with id: " + userId);
                });

        log.debug("Found existing user for update. ID: {}, Current Email: '{}', UUID: '{}'",
                existingUser.getId(), existingUser.getEmail(), existingUser.getUuid());
        boolean needsSave = false;

        // Selectively update fields on the managed 'existingUser' entity
        if (userUpdates.getFirstName() != null && !userUpdates.getFirstName().equals(existingUser.getFirstName())) {
            existingUser.setFirstName(userUpdates.getFirstName());
            log.debug("User ID {}: First name updated to '{}'", userId, userUpdates.getFirstName());
            needsSave = true;
        }
        if (userUpdates.getLastName() != null && !userUpdates.getLastName().equals(existingUser.getLastName())) {
            existingUser.setLastName(userUpdates.getLastName());
            log.debug("User ID {}: Last name updated to '{}'", userId, userUpdates.getLastName());
            needsSave = true;
        }
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            log.debug("User ID {}: Email change requested from '{}' to '{}'", userId, existingUser.getEmail(), userUpdates.getEmail());
            // ** CORRECTED EMAIL UNIQUENESS CHECK **
            if (userRepository.existsByEmailAndIdNot(userUpdates.getEmail(), userId)) {
                log.warn("Update failed for user ID {}. New email '{}' already exists for another user.", userId, userUpdates.getEmail());
                throw new EmailAlreadyExistsException("Email " + userUpdates.getEmail() + " is already taken by another user.");
            }
            existingUser.setEmail(userUpdates.getEmail());
            log.debug("User ID {}: Email updated to '{}'", userId, userUpdates.getEmail());
            needsSave = true;
        }
        if (userUpdates.getPassword() != null && !userUpdates.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(userUpdates.getPassword(), existingUser.getPassword()) &&
                    !userUpdates.getPassword().startsWith("$2a$")) { // Avoid re-encoding if it's already an encoded hash
                log.debug("User ID {}: New password provided. Encoding and updating password.", userId);
                existingUser.setPassword(passwordEncoder.encode(userUpdates.getPassword()));
                needsSave = true;
            } else if (userUpdates.getPassword().startsWith("$2a$") && !userUpdates.getPassword().equals(existingUser.getPassword())) {
                log.warn("User ID {}: An already encoded password was provided that differs from current. This scenario is unusual. Using provided encoded password.", userId);
                existingUser.setPassword(userUpdates.getPassword());
                needsSave = true;
            } else {
                log.debug("User ID {}: Password provided is same as current (or empty after check), or was already encoded and matched. No password update.", userId);
            }
        }
        if (userUpdates.getRoles() != null && !userUpdates.getRoles().isEmpty() && !areRoleListsEqual(userUpdates.getRoles(), existingUser.getRoles())) {
            // Roles passed in userUpdates should be managed entities, resolved by the caller
            existingUser.setRoles(userUpdates.getRoles());
            log.debug("User ID {}: Roles updated to: {}", userId, userUpdates.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));
            needsSave = true;
        }
        // Handle AuthProvider, googleId, profileImageUrl if they are updatable
        if (userUpdates.getAuthProvider() != null && !userUpdates.getAuthProvider().equals(existingUser.getAuthProvider())) {
            existingUser.setAuthProvider(userUpdates.getAuthProvider());
            log.debug("User ID {}: AuthProvider updated to '{}'", userId, userUpdates.getAuthProvider());
            needsSave = true;
        }
        if (userUpdates.getGoogleId() != null && !userUpdates.getGoogleId().equals(existingUser.getGoogleId())) {
            existingUser.setGoogleId(userUpdates.getGoogleId());
            log.debug("User ID {}: GoogleId updated.", userId); // Avoid logging sensitive ID
            needsSave = true;
        }
        if (userUpdates.getProfileImageUrl() != null && !userUpdates.getProfileImageUrl().equals(existingUser.getProfileImageUrl())) {
            existingUser.setProfileImageUrl(userUpdates.getProfileImageUrl());
            log.debug("User ID {}: ProfileImageUrl updated to '{}'", userId, userUpdates.getProfileImageUrl());
            needsSave = true;
        }
        // Admin might update the 'deleted' status (e.g., to reactivate a user)
        // The 'userUpdates' object might come from an AdminUserUpdateDTO that has a 'deleted' field
        if (userUpdates.isDeleted() != existingUser.isDeleted()) { // Check if 'deleted' status needs update
            existingUser.setDeleted(userUpdates.isDeleted());
            log.info("User ID {}: Deleted status updated to '{}'", userId, userUpdates.isDeleted());
            needsSave = true;
            if(userUpdates.isDeleted()){ // If user is being soft-deleted now
                refreshTokenService.deleteByUserId(userId);
                log.info("Invalidated refresh tokens for user ID {} due to soft deletion.", userId);
            }
        }


        if (needsSave) {
            User savedUser = userRepository.save(existingUser); // Save the modified 'existingUser'
            log.info("Successfully updated user ID: {}, UUID: '{}', Email: '{}'", savedUser.getId(), savedUser.getUuid(), savedUser.getEmail());
            return savedUser;
        } else {
            log.info("No updatable changes detected for user ID: {}. Returning existing user without save.", userId);
            return existingUser;
        }
    }

    // Helper to compare role lists (order might not matter, content does)
    private boolean areRoleListsEqual(List<Role> list1, List<Role> list2) {
        if (list1 == null && list2 == null) return true;
        if (list1 == null || list2 == null || list1.size() != list2.size()) return false;
        // Assuming Role has a proper equals/hashCode or we compare by a unique property like role name
        List<String> names1 = list1.stream().map(Role::getRoleName).sorted().collect(Collectors.toList());
        List<String> names2 = list2.stream().map(Role::getRoleName).sorted().collect(Collectors.toList());
        return names1.equals(names2);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int userId) {
        log.info("Attempting to soft-delete user with internal ID: {}", userId);
        return userRepository.findByIdAndDeletedFalse(userId).map(user -> {
            // Since User is not using custom builder with .copy(), we modify the fetched entity
            user.setDeleted(true);
            userRepository.save(user); // Save the modified managed entity
            log.debug("User ID: {} marked as deleted.", userId);
            refreshTokenService.deleteByUserId(userId);
            log.info("Successfully soft-deleted user ID: {} and invalidated associated refresh tokens.", userId);
            return true;
        }).orElseGet(() -> {
            log.warn("Soft-delete failed. User not found or already deleted with ID: {}", userId);
            return false;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getAll() {
        log.debug("Fetching all non-deleted users from repository.");
        return userRepository.findByDeletedFalse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role saveRole(Role role) {
        log.debug("Attempting to save role. Name: '{}'", role.getRoleName());
        // Role entity might also have @PrePersist for its own UUID if needed
        Role savedRole = roleRepository.save(role);
        log.info("Successfully saved role. ID: {}, Name: '{}'", savedRole.getId(), savedRole.getRoleName());
        return savedRole;
    }

    /**
     * {@inheritDoc}
     * This method is intended for generic save operations where the User entity
     * is assumed to be fully prepared by the caller (e.g., password already encoded,
     * UUID set if new, roles managed). Prefer more specific methods like
     * {@link #createUser(User, List)} or {@link #update(int, User)} for standard operations.
     */
    @Override
    public User saveUser(User user) {
        log.debug("Executing generic saveUser for user with email: '{}'. It is assumed the entity is fully prepared by the caller.", user.getEmail());
        // The User entity's @PrePersist should handle UUID generation if 'user.getUuid()' is null.
        // It also handles default AuthProvider and default Role if roles list is empty.
        // This method directly saves the provided user state.
        if (user.getId() == null && user.isDeleted() && (user.getUuid() == null || userRepository.findByUuidAndDeletedFalse(user.getUuid()).isEmpty())) {
            // If it's a new user (no ID) and it's marked as deleted (which is unusual for a direct save of a new entity),
            // and it's not an existing user being "undeleted" by UUID, then set deleted to false.
            // This is a safety net. Prefer explicit creation via createUser.
            log.warn("New user entity provided to generic saveUser was marked as deleted. Setting deleted to false. Email: '{}'", user.getEmail());
            user.setDeleted(false);
        }
        User savedUser = userRepository.save(user);
        log.info("Generic saveUser executed. Resulting User ID: {}, UUID: '{}', Email: '{}'",
                savedUser.getId(), savedUser.getUuid(), savedUser.getEmail());
        return savedUser;
    }

    /**
     * This method appears to be a duplicate of saveUser(User user).
     * Consolidating into saveUser(User user).
     * @deprecated Use {@link #saveUser(User)} instead.
     */
    @Override
    @Deprecated
    public User save(User user) {
        log.warn("Deprecated save(User user) method called. Delegating to saveUser(User user).");
        return saveUser(user);
    }
}