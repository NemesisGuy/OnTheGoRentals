package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.security.RefreshToken;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.exception.TokenRefreshException;
import za.ac.cput.repository.IRefreshTokenRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.IRefreshTokenService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * RefreshTokenServiceImpl.java
 * Implementation of the {@link IRefreshTokenService} interface.
 * Manages the lifecycle of refresh tokens, including creation, verification,
 * and deletion. Refresh tokens are persisted in the database.
 * <p>
 * Author: Peter Buckingham
 * Date: 2025-04-02
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);
    private final IRefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository; // Corrected variable name
    @Value("${jwt.refresh-token.expiration-ms}")
    private Long refreshTokenDurationMs;

    /**
     * Constructs the RefreshTokenServiceImpl.
     *
     * @param refreshTokenRepository The repository for refresh token persistence.
     * @param userRepository         The repository for user data access.
     */
    public RefreshTokenServiceImpl(IRefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository; // Corrected assignment
        log.info("RefreshTokenServiceImpl initialized. Refresh token duration: {} ms", refreshTokenDurationMs);
    }

    /**
     * Finds a refresh token by its token string.
     *
     * @param token The refresh token string to search for.
     * @return An Optional containing the {@link RefreshToken} if found, otherwise an empty Optional.
     */
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        log.debug("Attempting to find refresh token by token string (ending with ...{}).", token.substring(Math.max(0, token.length() - 6)));
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken(token);
        if (foundToken.isPresent()) {
            log.debug("Refresh token found for user ID: {}", foundToken.get().getUser().getId());
        } else {
            log.debug("No refresh token found for the given token string.");
        }
        return foundToken;
    }

    /**
     * Creates or updates a refresh token for a given user.
     * If an existing refresh token is found for the user, it is updated with a new
     * token string and expiry date (token rotation). Otherwise, a new refresh token is created.
     *
     * @param userId The internal integer ID of the user for whom to create the refresh token.
     * @return The created or updated {@link RefreshToken} entity.
     * @throws ResourceNotFoundException if the user with the given ID is not found.
     */
    @Override
    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        log.info("Attempting to create/update refresh token for user ID: {}", userId);
        User user = userRepository.findByIdAndDeletedFalse(userId) // Use method that respects soft delete
                .orElseThrow(() -> {
                    log.error("Cannot create refresh token: User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId + " while creating refresh token.");
                });

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);
        RefreshToken refreshToken;

        if (existingTokenOpt.isPresent()) {
            refreshToken = existingTokenOpt.get();
            log.debug("Existing refresh token found for user ID: {}. Rotating token.", userId);
            refreshToken.setToken(UUID.randomUUID().toString()); // New token string
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        } else {
            log.debug("No existing refresh token for user ID: {}. Creating new token.", userId);
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        }

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Successfully created/updated refresh token (ID: {}) for user ID: {}. New token string (ending ...{}).",
                savedToken.getId(), userId, savedToken.getToken().substring(Math.max(0, savedToken.getToken().length() - 6)));
        return savedToken;
    }

    /**
     * Verifies a refresh token by checking its expiration date.
     * If the token is expired, it is deleted from the database.
     * Additional device verification logic can be added here if needed.
     *
     * @param token The {@link RefreshToken} entity to verify.
     * @return The verified {@link RefreshToken} if it's valid and not expired.
     * @throws TokenRefreshException if the refresh token is expired.
     */
    @Override
    public RefreshToken verifyDeviceAndExpiration(RefreshToken token) {
        log.debug("Verifying expiration for refresh token ID: {} for user ID: {}", token.getId(), token.getUser().getId());
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            log.warn("Refresh token ID: {} for user ID: {} has expired (Expiry: {}). Deleting token.",
                    token.getId(), token.getUser().getId(), token.getExpiryDate());
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request.");
        }
        // Placeholder for device verification:
        // if (!isValidDevice(token.getDeviceInfo())) {
        //     log.warn("Refresh token ID: {} has invalid device information.", token.getId());
        //     throw new TokenRefreshException(token.getToken(), "Device verification failed for refresh token.");
        // }
        log.debug("Refresh token ID: {} successfully verified.", token.getId());
        return token;
    }

    /**
     * Deletes all refresh tokens associated with a specific user ID.
     * This is typically called during user logout or when a user is deleted.
     *
     * @param userId The internal integer ID of the user whose refresh tokens should be deleted.
     * @throws ResourceNotFoundException if the user with the given ID is not found (to prevent operations on non-existent users).
     */

    @Override
    @Transactional
    public void deleteByUserId(Integer userId) {
        log.info("Attempting to delete refresh token(s) for user ID: {}", userId);
        User user = userRepository.findById(userId) // Use findById to get user even if marked deleted
                .orElseThrow(() -> {
                    log.warn("Cannot delete refresh tokens: User not found with ID: {}", userId);
                    // This exception will now be caught by the GlobalExceptionHandler if it occurs
                    return new ResourceNotFoundException("User not found with id: " + userId + " when trying to delete refresh tokens.");
                });
        int deletedCount = refreshTokenRepository.deleteByUser(user);
        if (deletedCount > 0) {
            log.info("Successfully deleted {} refresh token(s) for user ID: {}", deletedCount, userId);
        } else {
            log.info("No refresh tokens found to delete for user ID: {}", userId);
        }
    }

    /**
     * Deletes all refresh tokens associated with a given User object.
     * This is preferred when the User object is already available to avoid an extra fetch.
     *
     * @param user The User entity whose refresh tokens are to be deleted.
     */
    @Override
    @Transactional
    public void deleteByUser(User user) {
        if (user == null) {
            log.warn("Attempted to delete refresh tokens for a null user object.");
            return;
        }
        log.info("Attempting to delete all refresh tokens for User ID: {}", user.getId());
        int deletedCount = refreshTokenRepository.deleteByUser(user);
        if (deletedCount > 0) {
            log.info("Successfully deleted {} refresh token(s) for User ID: {}", deletedCount, user.getId());
        } else {
            log.info("No refresh tokens found to delete for User ID: {}", user.getId());
        }
    }
// ...

    /**
     * Deletes a specific refresh token by its token string.
     * This is typically called when a refresh token is used for rotation.
     *
     * @param token The refresh token string to delete.
     */
    @Override
    @Transactional
    public void deleteByToken(String token) {
        log.info("Attempting to delete refresh token by token string (ending with ...{}).", token.substring(Math.max(0, token.length() - 6)));
        refreshTokenRepository.deleteByToken(token); // Assuming this method exists and works
        log.info("Refresh token (ending with ...{}) deleted from database if it existed.", token.substring(Math.max(0, token.length() - 6)));
    }
}