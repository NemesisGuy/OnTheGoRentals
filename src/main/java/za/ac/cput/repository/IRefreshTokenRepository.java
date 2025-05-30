package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.entity.security.RefreshToken;
import za.ac.cput.domain.entity.security.User;

import java.util.Optional;

@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user); // Useful if one active token per user

    @Modifying // Required for delete or update operations
    int deleteByUser(User user);

    void deleteByToken(String token); // If you implement token rotation
}