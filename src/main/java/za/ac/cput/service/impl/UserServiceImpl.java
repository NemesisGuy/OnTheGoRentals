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
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.service.IRefreshTokenService;
import za.ac.cput.service.IUserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenService refreshTokenService;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository,
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
     * {@inheritDoc}
     */
    @Override
    public User update(int userId, User userUpdates) {
        log.info("Attempting to update user with internal ID: {}", userId);
        User existingUser = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for update with id: " + userId));

        boolean needsSave = false;

        if (userUpdates.getFirstName() != null && !userUpdates.getFirstName().equals(existingUser.getFirstName())) {
            existingUser.setFirstName(userUpdates.getFirstName());
            needsSave = true;
        }
        if (userUpdates.getLastName() != null && !userUpdates.getLastName().equals(existingUser.getLastName())) {
            existingUser.setLastName(userUpdates.getLastName());
            needsSave = true;
        }
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(userUpdates.getEmail(), userId)) {
                throw new EmailAlreadyExistsException("Email " + userUpdates.getEmail() + " is already taken.");
            }
            existingUser.setEmail(userUpdates.getEmail());
            needsSave = true;
        }
        if (userUpdates.getPassword() != null && !userUpdates.getPassword().isEmpty() && !passwordEncoder.matches(userUpdates.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userUpdates.getPassword()));
            needsSave = true;
        }

        // Handle profile image fields
        if (userUpdates.getProfileImageFileName() != null && !userUpdates.getProfileImageFileName().equals(existingUser.getProfileImageFileName())) {
            existingUser.setProfileImageFileName(userUpdates.getProfileImageFileName());
            existingUser.setProfileImageType(userUpdates.getProfileImageType());
            existingUser.setProfileImageUploadedAt(userUpdates.getProfileImageUploadedAt());
            log.debug("User ID {}: Profile image updated to '{}'", userId, userUpdates.getProfileImageFileName());
            needsSave = true;
        }

        if (userUpdates.isDeleted() != existingUser.isDeleted()) {
            existingUser.setDeleted(userUpdates.isDeleted());
            log.info("User ID {}: Deleted status updated to '{}'", userId, userUpdates.isDeleted());
            if (userUpdates.isDeleted()) {
                refreshTokenService.deleteByUserId(userId);
            }
            needsSave = true;
        }

        if (needsSave) {
            User savedUser = userRepository.save(existingUser);
            log.info("Successfully updated user ID: {}", savedUser.getId());
            return savedUser;
        } else {
            log.info("No updatable changes detected for user ID: {}", userId);
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
}