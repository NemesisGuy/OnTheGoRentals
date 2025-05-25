package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // Keep if methods here modify state directly
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.server.ResponseStatusException; // Prefer custom exceptions or service layer handling
import za.ac.cput.domain.dto.request.DamageReportCreateDTO;
import za.ac.cput.domain.dto.request.DamageReportUpdateDTO;
import za.ac.cput.domain.dto.response.DamageReportResponseDTO;
import za.ac.cput.domain.entity.DamageReport;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.mapper.DamageReportMapper;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IDamageReportService;
import za.ac.cput.service.IRentalService;     // Inject interface to get Rental by UUID

import java.util.List;
import java.util.UUID;

/**
 * AdminDamageReportController.java
 * Controller Class for Admin to manage Damage Reports.
 * Author: Cwenga Dlova (214310671) // Updated by: [Your Name]
 * Date: 08/09/2023 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/admin/damage-reports") // Standardized base path
// @CrossOrigin(...) // Prefer global CORS
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminDamageReportController {

    private final IDamageReportService damageReportService;
    private final IRentalService rentalService; // To fetch Rental entity by UUID

    @Autowired
    public AdminDamageReportController(IDamageReportService damageReportService, IRentalService rentalService) {
        this.damageReportService = damageReportService;
        this.rentalService = rentalService;
    }

    /**
     * Admin creates a new damage report associated with a rental.
     */
    @PostMapping
    public ResponseEntity<DamageReportResponseDTO> createDamageReport(@Valid @RequestBody DamageReportCreateDTO createDto) {
        System.out.println("Admin creating damage report for rental UUID: " + createDto.getRentalUuid());
        Rental rentalEntity = rentalService.read(createDto.getRentalUuid()); // Fetch Rental entity by UUID
        // readByUuid should throw ResourceNotFoundException if rental not found

        DamageReport reportToCreate = DamageReportMapper.toEntity(createDto, rentalEntity);
        DamageReport createdEntity = damageReportService.create(reportToCreate); // Service takes entity
        return new ResponseEntity<>(DamageReportMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    /**
     * Admin retrieves a specific damage report by its UUID.
     */
    @GetMapping("/{reportUuid}")
    public ResponseEntity<DamageReportResponseDTO> getDamageReportByUuid(@PathVariable UUID reportUuid) {
        DamageReport reportEntity = damageReportService.read(reportUuid); // Fetch by UUID
        // Service's readByUuid should throw ResourceNotFoundException if not found
        return ResponseEntity.ok(DamageReportMapper.toDto(reportEntity));
    }

    /**
     * Admin updates an existing damage report.
     */
    @PutMapping("/{reportUuid}")
    public ResponseEntity<DamageReportResponseDTO> updateDamageReport(
            @PathVariable UUID reportUuid,
            @Valid @RequestBody DamageReportUpdateDTO updateDto
    ) {
        DamageReport existingReport = damageReportService.read(reportUuid); // Fetch current entity
        // The Rental associated with a damage report typically does not change.
        // If it could, you'd fetch the new Rental entity here based on a rentalUuid in updateDto.
        // For this mapper, we assume rental is not changed by this DTO.
        DamageReport reportWithUpdates = DamageReportMapper.applyUpdateDtoToEntity(updateDto, existingReport); // Pass existing rental
        DamageReport persistedReport = damageReportService.update(reportWithUpdates); // Service saves new state
        return ResponseEntity.ok(DamageReportMapper.toDto(persistedReport));
    }

    /**
     * Admin retrieves all damage reports (could include soft-deleted based on service logic).
     */
    @GetMapping
    public ResponseEntity<List<DamageReportResponseDTO>> getAllDamageReports() {
        List<DamageReport> reportList = damageReportService.getAll(); // Or getAllNonDeleted()
        List<DamageReportResponseDTO> dtoList = DamageReportMapper.toDtoList(reportList);
        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Admin soft-deletes a damage report by its UUID.
     */
    @DeleteMapping("/{reportUuid}")
    public ResponseEntity<Void> deleteDamageReport(@PathVariable UUID reportUuid) {
        DamageReport existingReport = damageReportService.read(reportUuid);
        boolean deleted = damageReportService.delete(existingReport.getId());
        if (!deleted) {
            // This case is hit if service returns false (e.g., not found to delete).
            // If service throws ResourceNotFoundException, a @ControllerAdvice would handle it.
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // Your original endpoints using Integer ID, now commented or removed in favor of UUID.
    /*
    @GetMapping("/read/{id}") // Original
    public ResponseEntity<DamageReportResponseDTO> read(@PathVariable int id) {
        DamageReport report = damageReportService.read(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        return ResponseEntity.ok(DamageReportMapper.toDto(report));
    }

    @DeleteMapping("/delete/{id}") // Original
    public ResponseEntity<Void> delete(@PathVariable int id) {
        damageReportService.deleteById(id); // Assumes this is hard delete or service handles soft delete
        return new ResponseEntity<>(HttpStatus.OK); // OK for delete is fine, or No Content
    }
    */
}