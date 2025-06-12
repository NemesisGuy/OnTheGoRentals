package za.ac.cput.repository;

/*import com.ons.securitylayerJwt.models.User;*/

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.security.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User findUserByEmail(String email);

    @Override
    Optional<User> findById(Integer integer);

    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByEmailAndDeletedFalse(String email);

    Optional<User> findByIdAndDeletedFalse(Integer id);

    List<User> findByDeletedFalse();

    Optional<User> findByUuidAndDeletedFalse(UUID uuid);

    boolean existsByEmailAndIdNot(String email, int userId);

}


