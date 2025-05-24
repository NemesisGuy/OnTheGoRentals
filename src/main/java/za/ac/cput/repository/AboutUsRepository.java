package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.AboutUs;

import java.util.List;
import java.util.Optional;

public interface AboutUsRepository extends JpaRepository<AboutUs, Integer> {
    boolean existsByIdAndDeletedFalse(int id);

    List<AboutUs> findByDeletedFalse();

    Optional<AboutUs> findByIdAndDeletedFalse(int id);
}

