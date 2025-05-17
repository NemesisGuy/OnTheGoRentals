package za.ac.cput.service;

import za.ac.cput.domain.security.RefreshToken;
import java.util.Optional;

public interface IRefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(Integer userId);
    RefreshToken verifyDeviceAndExpiration(RefreshToken token); // Renamed for clarity
    void deleteByUserId(Integer userId);
    void deleteByToken(String token); // For rotation
}