package za.ac.cput.repository;
/**
 * IDamageReportRepository.java
 * Repository Interface for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.entity.DamageReport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IDamageReportRepository extends JpaRepository<DamageReport, Integer> {

    List<DamageReport> findByDeletedFalse();

    Optional<DamageReport> findByIdAndDeletedFalse(Integer integer);

    DamageReport findByUuidAndDeletedFalse(UUID uuid);
}
