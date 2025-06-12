package za.ac.cput.service;

// No HttpServletResponse needed here as this service won't handle cookies directly.
// That will be the responsibility of AuthServiceImpl.

import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.User;

import java.util.List;
import java.util.UUID;

/**
 * IUserService.java
 * Interface defining the contract for user data management services.
 * This includes User entity Create, Read, Update, Delete (CRUD) operations,
 * retrieving user collections, and managing associated roles.
 * <p>
 * Authentication-specific logic (login, registration process, token generation, logout)
 * is handled by a separate IAuthService.
 * <p>
 * Author: Peter Buckingham
 * Date: 2025-05-28
 */
public interface IUserService {

    /**
     * Creates a new user with the given details and roles.
     * This method is responsible for encoding the password before persistence
     * and ensuring the user entity is correctly initialized.
     *
     * @param userDetails The {@link User} entity containing user details (with plain password).
     * @param roles       A list of {@link Role} entities to assign to the user. These roles should be managed entities.
     * @return The created and persisted {@link User} entity.
     * @throws za.ac.cput.exception.EmailAlreadyExistsException if the email in userDetails is already in use.
     */
    User createUser(User userDetails, List<Role> roles);

    /**
     * Retrieves a user by their internal integer ID, if they are not marked as deleted.
     *
     * @param id The internal integer ID of the user.
     * @return The {@link User} entity, or {@code null} if not found or marked as deleted.
     */
    User read(Integer id);

    /**
     * Retrieves a user by their UUID, if they are not marked as deleted.
     *
     * @param uuid The UUID of the user.
     * @return The {@link User} entity, or {@code null} if not found or marked as deleted.
     */
    User read(UUID uuid);

    /**
     * Retrieves a user by their email address, if they are not marked as deleted.
     *
     * @param email The email address of the user.
     * @return The {@link User} entity, or {@code null} if not found or marked as deleted.
     */
    User read(String email);

    /**
     * Updates an existing user identified by their internal integer ID.
     * Selectively updates fields based on the provided {@code userUpdates} entity.
     * Handles password encoding if a new plain password is provided.
     *
     * @param userId      The internal integer ID of the user to update.
     * @param userUpdates A {@link User} entity containing the fields to update.
     *                    Password should be plain text if being changed. Roles should be managed entities.
     * @return The updated and persisted {@link User} entity.
     * @throws za.ac.cput.exception.ResourceNotFoundException   if the user with {@code userId} is not found.
     * @throws za.ac.cput.exception.EmailAlreadyExistsException if the new email in {@code userUpdates} is already taken by another user.
     */
    User update(int userId, User userUpdates);

    /**
     * Soft-deletes a user by their internal integer ID.
     * Marks the user as deleted and triggers invalidation of their refresh tokens via {@link IRefreshTokenService}.
     *
     * @param userId The internal integer ID of the user to delete.
     * @return {@code true} if the user was found and soft-deleted, {@code false} otherwise.
     */
    boolean delete(int userId);

    /**
     * Retrieves all users who are not marked as deleted.
     *
     * @return A list of non-deleted {@link User} entities.
     */
    List<User> getAll();

    /**
     * Saves or updates a {@link Role} entity.
     * This method might be part of a dedicated IRoleService in a larger application.
     *
     * @param role The role to save.
     * @return The saved or updated role entity.
     */
    Role saveRole(Role role);

    /**
     * A generic method to save (create or update) a {@link User} entity.
     * If creating a new user or changing a password, it is the caller's responsibility
     * to ensure the password in the passed {@code user} entity is already encoded.
     * For new users, ensure UUID is set or will be set by this method if null.
     * It's generally preferred to use {@link #createUser(User, List)} for robust new user creation
     * and {@link #update(int, User)} for targeted updates.
     *
     * @param user The user entity to save.
     * @return The persisted user entity.
     */
    User saveUser(User user);


    User save(User user);

    /**
     * Checks if a user with the given email already exists in the system.
     * This is useful for validation during user creation or updates.
     *
     * @param email The email address to check.
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);
}