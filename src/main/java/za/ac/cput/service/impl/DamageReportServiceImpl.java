package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.DamageReport;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IDamageReportRepository;
import za.ac.cput.service.IDamageReportService;
import za.ac.cput.service.IRentalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DamageReportServiceImpl.java
 * Service Class for the Damage Report, implementing {@link IDamageReportService}.
 * Manages CRUD operations for DamageReport entities.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 * <p>
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service("damageReportServiceImpl") // Explicit bean name, ensure class name consistency if this matters
public class DamageReportServiceImpl implements IDamageReportService { // Corrected class name

    private static final Logger log = LoggerFactory.getLogger(DamageReportServiceImpl.class);

    private final IDamageReportRepository damageReportRepository; // Renamed repository
    // private final RentalRepository rentalRepository; // If needed for validation, inject IRentalRepository
    private final IRentalService rentalService; // For validation if a rental must exist

    /**
     * Constructs the DamageReportServiceImpl.
     *
     * @param damageReportRepository The repository for damage report persistence.
     * @param rentalService          The service for rental-related operations (e.g., validating rental existence).
     */
    @Autowired
    public DamageReportServiceImpl(IDamageReportRepository damageReportRepository, IRentalService rentalService) {
        this.damageReportRepository = damageReportRepository;
        this.rentalService = rentalService; // Injected for potential validation
        log.info("DamageReportServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     * Creates a new damage report.
     * Ensures the entity is not marked as deleted and assigns a UUID if not present.
     * Validates that the associated rental exists.
     *
     * @throws ResourceNotFoundException if the associated Rental does not exist.
     */
    @Override
    @Transactional // Spring's Transactional
    public DamageReport create(DamageReport damageReport) {
        log.info("Attempting to create new damage report for Rental UUID: {}",
                damageReport.getRental() != null ? damageReport.getRental().getUuid() : "null");

        if (damageReport.getRental() == null || damageReport.getRental().getUuid() == null) {
            log.error("Damage report creation failed: Rental or Rental UUID is null.");
            throw new IllegalArgumentException("Rental and Rental UUID must be provided for a damage report.");
        }
        // Validate that the rental exists
        if (rentalService.read(damageReport.getRental().getUuid()) == null) {
            log.warn("Damage report creation failed: Associated Rental with UUID '{}' not found.", damageReport.getRental().getUuid());
            throw new ResourceNotFoundException("Associated Rental not found with UUID: " + damageReport.getRental().getUuid());
        }

        DamageReport entityToSave;
        if (damageReport.getUuid() == null) {
            entityToSave = new DamageReport.Builder().copy(damageReport)
                    .setUuid(UUID.randomUUID())
                    .setDeleted(false)
                    .build();
            log.debug("Generated UUID '{}' for new damage report.", entityToSave.getUuid());
        } else {
            entityToSave = new DamageReport.Builder().copy(damageReport)
                    .setDeleted(false)
                    .build();
        }
        // DateAndTime is often set here or by @PrePersist
        if (entityToSave.getDateAndTime() == null) {
            entityToSave = new DamageReport.Builder().copy(entityToSave).setDateAndTime(java.time.LocalDateTime.now()).build();
            log.debug("Set current date and time for new damage report UUID '{}'", entityToSave.getUuid());
        }


        DamageReport savedReport = damageReportRepository.save(entityToSave);
        log.info("Successfully created damage report. ID: {}, UUID: '{}', for Rental UUID: '{}'",
                savedReport.getId(), savedReport.getUuid(), savedReport.getRental().getUuid());
        return savedReport;
    }

    /**
     * Reads a damage report by its internal integer ID. This method is deprecated in the interface,
     * but implemented here if the repository still supports it. Prefer reading by UUID.
     *
     * @param id The internal integer ID.
     * @return An Optional containing the {@link DamageReport} if found and not deleted, otherwise empty.
     * @deprecated Prefer {@link #read(Integer id)} which returns the entity or null, or {@link #read(UUID)}.
     */
    @Deprecated
    public Optional<DamageReport> read(int id) { // Method name changed to avoid clash if interface changes
        log.warn("Deprecated readOld(int id) method called for DamageReport ID: {}. Prefer read(Integer) or read(UUID).", id);
        return damageReportRepository.findByIdAndDeletedFalse(id);
    }


    /**
     * {@inheritDoc}
     * Reads a damage report by its UUID.
     */
    @Override
    public DamageReport read(UUID uuid) {
        log.debug("Attempting to read damage report by UUID: '{}'", uuid);
        // The original code returned the entity directly from findByUuidAndDeletedFalse(uuid)
        // which itself likely returns Optional. Let's ensure it unwraps.
        DamageReport report = damageReportRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (report == null) {
            log.warn("Damage report not found or is deleted for UUID: '{}'", uuid);
        } else {
            log.debug("Damage report found for UUID: '{}'. ID: {}", uuid, report.getId());
        }
        return report;
    }

    /**
     * {@inheritDoc}
     * Reads a damage report by its internal integer ID.
     */
    @Override
    public DamageReport read(Integer id) { // Parameter name changed from integer
        log.debug("Attempting to read damage report by ID: {}", id);
        DamageReport report = damageReportRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (report == null) {
            log.warn("Damage report not found or is deleted for ID: {}", id);
        } else {
            log.debug("Damage report found for ID: {}. UUID: '{}'", id, report.getUuid());
        }
        return report;
    }

    /**
     * {@inheritDoc}
     * Updates an existing damage report.
     * The input {@code damageReportWithUpdates} should be the complete new state.
     *
     * @throws ResourceNotFoundException if the damage report with the given ID does not exist or is deleted.
     * @throws IllegalArgumentException  if the associated Rental is changed or missing.
     */
    @Override
    @Transactional // Spring's Transactional
    public DamageReport update(DamageReport damageReportWithUpdates) { // Parameter name implies it's the desired new state
        Integer reportId = damageReportWithUpdates.getId();
        log.info("Attempting to update damage report with ID: {}", reportId);

        if (reportId == null) {
            log.error("Update failed: DamageReport ID cannot be null.");
            throw new IllegalArgumentException("DamageReport ID cannot be null for update.");
        }
        DamageReport existingReport = damageReportRepository.findByIdAndDeletedFalse(reportId)
                .orElseThrow(() -> {
                    log.warn("Update failed: DamageReport not found or is deleted for ID: {}", reportId);
                    return new ResourceNotFoundException("DamageReport not found with ID: " + reportId + " for update.");
                });

        // Business rule: Rental associated with a damage report typically should not change.
        if (damageReportWithUpdates.getRental() == null ||
                !damageReportWithUpdates.getRental().getUuid().equals(existingReport.getRental().getUuid())) {
            log.error("Update failed for damage report ID {}: Attempt to change associated Rental is not allowed.", reportId);
            throw new IllegalArgumentException("The associated Rental of a DamageReport cannot be changed post-creation.");
        }

        log.debug("Updating damage report ID: {}. New Description: '{}'",
                reportId, damageReportWithUpdates.getDescription());
        // 'damageReportWithUpdates' IS the new state, built by the controller (ensuring Rental is not changed).

        DamageReport savedReport = damageReportRepository.save(damageReportWithUpdates);
        log.info("Successfully updated damage report. ID: {}, UUID: '{}'",
                savedReport.getId(), savedReport.getUuid());
        return savedReport; // Original code returned null, corrected to return saved entity
    }

    /**
     * Soft-deletes a damage report by its internal integer ID.
     * This method was named `deleteById` in the original code, but the interface likely expects `delete(Integer)`.
     *
     * @param id The internal integer ID of the damage report to delete.
     * @return {@code true} if deleted successfully, {@code false} otherwise.
     */
    @Transactional // Spring's Transactional
    public boolean deleteById(int id) { // Kept original name for compatibility if directly called
        log.info("Attempting to soft-delete damage report by ID (deleteById method): {}", id);
        Optional<DamageReport> reportOpt = damageReportRepository.findByIdAndDeletedFalse(id);
        if (reportOpt.isPresent()) {
            DamageReport report = reportOpt.get();
            DamageReport deletedReport = new DamageReport.Builder().copy(report).setDeleted(true).build();
            damageReportRepository.save(deletedReport);
            log.info("Successfully soft-deleted damage report with ID: {} (via deleteById).", id);
            return true;
        }
        log.warn("Soft-delete failed (via deleteById): Damage report not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     * Soft-deletes a damage report by its internal integer ID.
     */
    @Override
    @Transactional // Spring's Transactional
    public boolean delete(Integer id) { // Matches interface
        log.info("Attempting to soft-delete damage report with ID: {}", id);
        // Delegate to deleteById or reimplement to ensure consistency
        return deleteById(id); // Assuming deleteById is the intended logic
    }

    /**
     * {@inheritDoc}
     * Retrieves all non-deleted damage reports.
     */
    @Override
    public List<DamageReport> getAll() {
        log.debug("Fetching all non-deleted damage reports.");
        List<DamageReport> reports = damageReportRepository.findByDeletedFalse();
        log.debug("Found {} non-deleted damage reports.", reports.size());
        return reports;
    }
}