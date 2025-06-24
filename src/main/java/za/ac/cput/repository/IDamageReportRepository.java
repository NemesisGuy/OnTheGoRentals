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

    /**
     * Finds all non-deleted damage reports.
     *
     * @return A list of all non-deleted {@link DamageReport} entities.
     */
    List<DamageReport> findByDeletedFalse();

    /**
     * Finds a damage report by its internal integer ID, ensuring it is not deleted.
     *
     * @param integer The internal integer ID of the damage report.
     * @return An {@link Optional} containing the {@link DamageReport} if found and not deleted,
     * otherwise an empty Optional.
     */
    Optional<DamageReport> findByIdAndDeletedFalse(Integer integer);

    /**
     * Finds a damage report by its UUID, ensuring it is not deleted.
     *
     * @param uuid The UUID of the damage report.
     * @return An {@link Optional} containing the {@link DamageReport} if found and not deleted,
     * otherwise an empty Optional.
     */
    Optional<DamageReport> findByUuidAndDeletedFalse(UUID uuid);
}
