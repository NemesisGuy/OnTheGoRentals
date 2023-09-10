package za.ac.cput.repository;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Add additional methods as we need them

}
