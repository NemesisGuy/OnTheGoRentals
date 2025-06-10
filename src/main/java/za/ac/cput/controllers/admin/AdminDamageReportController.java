package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.DamageReportCreateDTO;
import za.ac.cput.domain.dto.request.DamageReportUpdateDTO;
import za.ac.cput.domain.dto.response.DamageReportResponseDTO;
import za.ac.cput.domain.entity.DamageReport;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.mapper.DamageReportMapper;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IDamageReportService;
import za.ac.cput.service.IRentalService;

import java.util.List;
import java.util.UUID;

/**
 * AdminDamageReportController.java
 * Controller for administrators to manage Damage Reports.
 * Allows admins to create, retrieve, update, and delete damage reports,
 * which are associated with specific rentals.
 * External identification of damage reports is by UUID. Internal service operations
 * primarily use integer IDs. This controller bridges that gap.
 * <p>
 * Author: Cwenga Dlova (214310671)
 * Updated by: System/AI
 * Date: 08/09/2023
 * Updated: [Your Current Date - e.g., 2024-05-28]
 */
@RestController
@RequestMapping("/api/v1/admin/damage-reports")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminDamageReportController {

    private static final Logger log = LoggerFactory.getLogger(AdminDamageReportController.class);
    private final IDamageReportService damageReportService;
    private final IRentalService rentalService;

    /**
     * Constructs an AdminDamageReportController with necessary service dependencies.
     *
     * @param damageReportService The service for damage report operations.
     * @param rentalService       The service for rental operations (to fetch associated rentals).
     */
    @Autowired
    public AdminDamageReportController(IDamageReportService damageReportService, IRentalService rentalService) {
        this.damageReportService = damageReportService;
        this.rentalService = rentalService;
        log.info("AdminDamageReportController initialized.");
    }

    /**
     * Allows an admin to create a new damage report.
     * The report must be associated with an existing rental, identified by its UUID.
     *
     * @param createDto The {@link DamageReportCreateDTO} containing details for the new damage report.
     * @return A ResponseEntity containing the created {@link DamageReportResponseDTO} and HTTP status CREATED.
     * @throws ResourceNotFoundException if the associated Rental with the given UUID is not found.
     */
    @PostMapping
    public ResponseEntity<DamageReportResponseDTO> createDamageReport(@Valid @RequestBody DamageReportCreateDTO createDto) {
        log.info("Admin request to create a new damage report with DTO: {}", createDto);

        log.debug("Fetching rental with UUID: {} for damage report.", createDto.getRentalUuid());
        Rental rentalEntity = rentalService.read(createDto.getRentalUuid());
        // The rentalService.read(UUID) method is expected to throw ResourceNotFoundException if rental not found.
        log.debug("Found rental with ID: {} for damage report.", rentalEntity.getId());

        DamageReport reportToCreate = DamageReportMapper.toEntity(createDto, rentalEntity);
        log.debug("Mapped DTO to DamageReport entity for creation: {}", reportToCreate);

        DamageReport createdEntity = damageReportService.create(reportToCreate);
        // Assuming 'getReportId()' or 'getUuid()' is the method in DamageReport to get its UUID. Adjust if different.
        log.info("Successfully created damage report with ID: {} and UUID: {}", createdEntity.getId(), createdEntity.getUuid());
        return new ResponseEntity<>(DamageReportMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific damage report by its UUID.
     *
     * @param reportUuid The UUID of the damage report to retrieve.
     * @return A ResponseEntity containing the {@link DamageReportResponseDTO} if found.
     * @throws ResourceNotFoundException if the damage report with the given UUID is not found (handled by service).
     */
    @GetMapping("/{reportUuid}")
    public ResponseEntity<DamageReportResponseDTO> getDamageReportByUuid(@PathVariable UUID reportUuid) {
        log.info("Admin request to get damage report by UUID: {}", reportUuid);
        DamageReport reportEntity = damageReportService.read(reportUuid);
        // The damageReportService.read(UUID) method is expected to throw ResourceNotFoundException if not found.
        log.info("Successfully retrieved damage report with ID: {} for UUID: {}", reportEntity.getId(), reportUuid);
        return ResponseEntity.ok(DamageReportMapper.toDto(reportEntity));
    }

    /**
     * Allows an admin to update an existing damage report.
     * The associated rental for a damage report typically does not change via this update.
     *
     * @param reportUuid The UUID of the damage report to update.
     * @param updateDto  The {@link DamageReportUpdateDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link DamageReportResponseDTO}.
     * @throws ResourceNotFoundException if the damage report with the given UUID is not found (handled by service).
     */
    @PutMapping("/{reportUuid}")
    public ResponseEntity<DamageReportResponseDTO> updateDamageReport(
            @PathVariable UUID reportUuid,
            @Valid @RequestBody DamageReportUpdateDTO updateDto
    ) {
        log.info("Admin request to update damage report with UUID: {}. Update DTO: {}", reportUuid, updateDto);
        DamageReport existingReport = damageReportService.read(reportUuid);
        log.debug("Found existing damage report with ID: {} for UUID: {}", existingReport.getId(), reportUuid);

        // Note: The rental associated with the damage report is typically immutable after creation.
        // If it could change, the updateDto would need a rentalUuid, and you'd fetch the new Rental here.
        // The DamageReportMapper.applyUpdateDtoToEntity should correctly handle the existing rental from 'existingReport'.
        DamageReport reportWithUpdates = DamageReportMapper.applyUpdateDtoToEntity(updateDto, existingReport);
        log.debug("Applied DTO updates to DamageReport entity: {}", reportWithUpdates);

        DamageReport persistedReport = damageReportService.update(reportWithUpdates);
        log.info("Successfully updated damage report with ID: {} and UUID: {}", persistedReport.getId(), persistedReport.getUuid());
        return ResponseEntity.ok(DamageReportMapper.toDto(persistedReport));
    }

    /**
     * Retrieves all damage reports.
     * Depending on the service implementation, this might include reports
     * irrespective of their status or soft-deletion state.
     *
     * @return A ResponseEntity containing a list of {@link DamageReportResponseDTO}s, or no content if none exist.
     */
    @GetMapping
    public ResponseEntity<List<DamageReportResponseDTO>> getAllDamageReports() {
        log.info("Admin request to get all damage reports.");
        List<DamageReport> reportList = damageReportService.getAll();
        if (reportList.isEmpty()) {
            log.info("No damage reports found.");
            return ResponseEntity.noContent().build();
        }
        List<DamageReportResponseDTO> dtoList = DamageReportMapper.toDtoList(reportList);
        log.info("Successfully retrieved {} damage reports.", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Allows an admin to soft-delete a damage report by its UUID.
     * The controller first retrieves the report by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param reportUuid The UUID of the damage report to delete.
     * @return A ResponseEntity with no content if successful, or not found if the report doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the damage report with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{reportUuid}")
    public ResponseEntity<Void> deleteDamageReport(@PathVariable UUID reportUuid) {
        log.info("Admin request to delete damage report with UUID: {}", reportUuid);
        DamageReport existingReport = damageReportService.read(reportUuid);
        log.debug("Found damage report with ID: {} (UUID: {}) for deletion.", existingReport.getId(), reportUuid);

        boolean deleted = damageReportService.delete(existingReport.getId());
        if (!deleted) {
            log.warn("Damage report with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", existingReport.getId(), reportUuid);
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully soft-deleted damage report with ID: {} (UUID: {}).", existingReport.getId(), reportUuid);
        return ResponseEntity.noContent().build();
    }

}