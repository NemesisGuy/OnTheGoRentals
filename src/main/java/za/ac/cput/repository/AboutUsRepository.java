package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.AboutUs;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AboutUsRepository extends JpaRepository<AboutUs, Integer> {
    boolean existsByIdAndDeletedFalse(int id);

    List<AboutUs> findByDeletedFalse();

    Optional<AboutUs> findByIdAndDeletedFalse(int id);

    Optional<AboutUs>  findByUuidAndDeletedFalse(UUID uuid);
}

