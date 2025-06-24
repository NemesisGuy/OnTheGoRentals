package za.ac.cput.service;

import za.ac.cput.domain.entity.security.RefreshToken;
import za.ac.cput.domain.entity.security.User;

import java.util.Optional;

/**
 * IRefreshTokenService.java
 * Interface defining the contract for managing refresh tokens.
 * This includes creating, finding, verifying, and deleting refresh tokens.
 * <p>
 * Author: Peter Buckingham // Assuming based on consistent authorship
 * Date: [Original Date - Please specify if known, e.g., 2025-05-28 from previous context]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface IRefreshTokenService {

    /**
     * Finds a refresh token by its token string.
     *
     * @param token The token string to search for.
     * @return An {@link Optional} containing the {@link RefreshToken} if found, otherwise empty.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Creates or updates a refresh token for a given user ID.
     * If an existing token is found, it's typically updated (e.g., new token string, new expiry).
     * Otherwise, a new token is created.
     *
     * @param userId The internal integer ID of the user.
     * @return The created or updated {@link RefreshToken}.
     * @throws za.ac.cput.exception.ResourceNotFoundException if the user with userId is not found.
     */
    RefreshToken createRefreshToken(Integer userId);

    /**
     * Verifies the validity of a refresh token, primarily checking its expiration.
     * May include other checks like device verification in future implementations.
     * If the token is expired, it's typically deleted.
     *
     * @param token The {@link RefreshToken} to verify.
     * @return The verified {@link RefreshToken} if valid.
     * @throws za.ac.cput.exception.TokenRefreshException if the token is expired or otherwise invalid.
     */
    RefreshToken verifyDeviceAndExpiration(RefreshToken token);

    /**
     * Deletes all refresh tokens associated with a given user ID.
     * Used, for example, when a user logs out or their account is deleted.
     *
     * @param userId The internal integer ID of the user.
     * @throws za.ac.cput.exception.ResourceNotFoundException if the user is not found (to ensure operation context).
     */
    void deleteByUserId(Integer userId);

    /**
     * Deletes a refresh token by its specific token string.
     * Used during refresh token rotation to invalidate the used token.
     *
     * @param token The token string of the refresh token to delete.
     */
    void deleteByToken(String token);

    /**
     * Deletes all refresh tokens associated with a given {@link User} object.
     * Preferred when the User object is already available to avoid an extra database lookup for the user.
     *
     * @param user The {@link User} entity whose refresh tokens are to be deleted.
     */
    void deleteByUser(User user);
}