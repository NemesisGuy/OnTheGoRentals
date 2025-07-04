package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.entity.Driver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Integer> {
    List<Driver> findByDeletedFalse();


    Optional<Driver> findByIdAndDeletedFalse(Integer id);

    Optional<Driver> findByUuidAndDeletedFalse(UUID driverUuid);

    boolean existsByIdAndDeletedFalse(Integer driverId);
}
