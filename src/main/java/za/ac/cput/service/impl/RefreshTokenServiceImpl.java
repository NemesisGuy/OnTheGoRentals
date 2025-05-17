package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.security.RefreshToken;
import za.ac.cput.domain.security.User;
import za.ac.cput.exception.TokenRefreshException; // You'll need to create this custom exception
import za.ac.cput.repository.IRefreshTokenRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.service.IRefreshTokenService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    @Value("${jwt.refresh-token.expiration-ms}")
    private Long refreshTokenDurationMs;

    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;

    public RefreshTokenServiceImpl(IRefreshTokenRepository refreshTokenRepository, IUserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
//    @Transactional
//    public RefreshToken createRefreshToken(Integer userId) {
//        System.out.println("Creating refresh token for user ID: " + userId);
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//
//        // If using OneToOne, delete any existing refresh token for this user
//        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
//        // Or if ManyToOne, you might have different logic
//
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUser(user);
//        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
//        refreshToken.setToken(UUID.randomUUID().toString());
//
//        return refreshTokenRepository.save(refreshToken);
//    }

    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        System.out.println("Creating refresh token for user ID: " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Try to find an existing token for the user
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

        RefreshToken refreshToken;
        if (existingTokenOpt.isPresent()) {
            // Update the existing token
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(UUID.randomUUID().toString()); // Generate a new token string
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            // The user relationship is already set
        } else {
            // No existing token, create a new one
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        }

        return refreshTokenRepository.save(refreshToken); // This will be an UPDATE or INSERT
    }

    @Override
    public RefreshToken verifyDeviceAndExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        // You could add device verification logic here if needed
        return token;
    }

    @Override
    @Transactional
    public void deleteByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}