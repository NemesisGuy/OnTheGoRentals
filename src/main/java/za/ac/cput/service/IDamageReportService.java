package za.ac.cput.service;

import za.ac.cput.domain.entity.DamageReport;

import java.util.List;
import java.util.Optional; // Kept as per original method signature
import java.util.UUID;

/**
 * IDamageReportService.java
 * Interface defining the contract for Damage Report services.
 * Extends the generic {@link IService} for basic CRUD and adds specific retrieval methods.
 *
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface IDamageReportService extends IService<DamageReport, Integer> {

    // create(DamageReport) is inherited from IService
    // update(DamageReport) is inherited from IService
    // delete(Integer) is inherited from IService

    /**
     * Retrieves a damage report by its internal integer ID.
     * This method signature returns an {@link Optional} to explicitly handle the case
     * where a report might not be found.
     *
     * @param id The internal integer ID of the damage report.
     * @return An {@link Optional} containing the {@link DamageReport} if found and not deleted,
     *         otherwise an empty Optional.
     * @deprecated The generic {@link IService #read(Integer)} returns the entity or null, which is often simpler.
     *             Consider aligning if {@code Optional} is not strictly needed across all {@code read(id)} methods.
     */
    @Deprecated // If IService<T, Integer>.read(Integer) is preferred
    Optional<DamageReport> read(int id); // Original signature

    /**
     * Retrieves a damage report by its UUID.
     *
     * @param uuid The UUID of the damage report.
     * @return The {@link DamageReport} entity, or {@code null} if not found or soft-deleted.
     */
    DamageReport read(UUID uuid);

    /**
     * Soft-deletes a damage report by its internal integer ID.
     * This seems to be an alternative naming for the delete operation.
     *
     * @param id The internal integer ID of the damage report to delete.
     * @return {@code true} if the report was found and soft-deleted, {@code false} otherwise.
     * @deprecated Prefer the {@link IService #delete(Integer)} method for consistency.
     */
    @Deprecated
    boolean deleteById(int id);

    /**
     * Retrieves all non-deleted damage reports.
     *
     * @return A list of all non-deleted {@link DamageReport} entities. Can be empty.
     */
    List<DamageReport> getAll();
}