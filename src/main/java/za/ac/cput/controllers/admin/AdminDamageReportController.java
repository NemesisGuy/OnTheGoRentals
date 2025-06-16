package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import za.ac.cput.service.IDamageReportService;
import za.ac.cput.service.IFileStorageService;
import za.ac.cput.service.IRentalService;

import java.util.List;
import java.util.UUID;

/**
 * AdminDamageReportController.java
 * Controller for administrators to manage Damage Reports.
 * Allows admins to perform CRUD operations on damage reports associated with specific rentals.
 *
 * @author Cwenga Dlova (214310671)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/damage-reports")
@Tag(name = "Admin: Damage Report Management", description = "Endpoints for administrators to manage damage reports.")
public class AdminDamageReportController {

    private static final Logger log = LoggerFactory.getLogger(AdminDamageReportController.class);
    private final IDamageReportService damageReportService;
    private final IRentalService rentalService;
    private final IFileStorageService fileStorageService;

    /**
     * Constructs an AdminDamageReportController with necessary service dependencies.
     *
     * @param damageReportService The service for damage report operations.
     * @param rentalService       The service for rental operations to fetch associated rentals.
     * @param fileStorageService  The service for generating image URLs for nested DTOs.
     */
    @Autowired
    public AdminDamageReportController(IDamageReportService damageReportService, IRentalService rentalService, IFileStorageService fileStorageService) {
        this.damageReportService = damageReportService;
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        log.info("AdminDamageReportController initialized.");
    }

    /**
     * Creates a new damage report and associates it with an existing rental.
     *
     * @param createDto The DTO containing the details of the new damage report.
     * @return A ResponseEntity containing the created damage report DTO.
     */
    @Operation(summary = "Create a new damage report", description = "Creates a new damage report and associates it with an existing rental by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Damage report created successfully", content = @Content(schema = @Schema(implementation = DamageReportResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "404", description = "Associated rental not found with the specified UUID")
    })
    @PostMapping
    public ResponseEntity<DamageReportResponseDTO> createDamageReport(@Valid @RequestBody DamageReportCreateDTO createDto) {
        log.info("Admin request to create a new damage report for rental UUID: {}", createDto.getRentalUuid());

        Rental rentalEntity = rentalService.read(createDto.getRentalUuid());
        DamageReport reportToCreate = DamageReportMapper.toEntity(createDto, rentalEntity);
        DamageReport createdReport = damageReportService.create(reportToCreate);

        log.info("Successfully created damage report with UUID: {}", createdReport.getUuid());
        return new ResponseEntity<>(DamageReportMapper.toDto(createdReport, fileStorageService), HttpStatus.CREATED);
    }

    /**
     * Retrieves all damage reports in the system.
     *
     * @return A ResponseEntity containing a list of all damage report DTOs.
     */
    @Operation(summary = "Get all damage reports", description = "Retrieves a list of all damage reports in the system for administrative review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "204", description = "No damage reports found")
    })
    @GetMapping
    public ResponseEntity<List<DamageReportResponseDTO>> getAllDamageReports() {
        log.info("Admin request to get all damage reports.");
        List<DamageReport> reportList = damageReportService.getAll();
        if (reportList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(DamageReportMapper.toDtoList(reportList, fileStorageService));
    }

    /**
     * Retrieves a specific damage report by its UUID.
     *
     * @param reportUuid The UUID of the damage report to retrieve.
     * @return A ResponseEntity containing the damage report DTO.
     */
    @Operation(summary = "Get damage report by UUID", description = "Retrieves a specific damage report by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Damage report found", content = @Content(schema = @Schema(implementation = DamageReportResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Damage report not found with the specified UUID")
    })
    @GetMapping("/{reportUuid}")
    public ResponseEntity<DamageReportResponseDTO> getDamageReportByUuid(
            @Parameter(description = "UUID of the damage report to retrieve.", required = true) @PathVariable UUID reportUuid) {
        log.info("Admin request to get damage report by UUID: {}", reportUuid);
        DamageReport reportEntity = damageReportService.read(reportUuid);
        return ResponseEntity.ok(DamageReportMapper.toDto(reportEntity, fileStorageService));
    }

    /**
     * Updates an existing damage report.
     *
     * @param reportUuid The UUID of the damage report to update.
     * @param updateDto  The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated damage report DTO.
     */
    @Operation(summary = "Update an existing damage report", description = "Updates the details of an existing damage report by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Damage report updated successfully", content = @Content(schema = @Schema(implementation = DamageReportResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "404", description = "Damage report not found with the specified UUID")
    })
    @PutMapping("/{reportUuid}")
    public ResponseEntity<DamageReportResponseDTO> updateDamageReport(
            @Parameter(description = "UUID of the damage report to update.", required = true) @PathVariable UUID reportUuid,
            @Valid @RequestBody DamageReportUpdateDTO updateDto) {
        log.info("Admin request to update damage report with UUID: {}", reportUuid);
        DamageReport existingReport = damageReportService.read(reportUuid);
        DamageReport reportWithUpdates = DamageReportMapper.applyUpdateDtoToEntity(updateDto, existingReport);
        DamageReport persistedReport = damageReportService.update(reportWithUpdates);
        log.info("Successfully updated damage report with UUID: {}", persistedReport.getUuid());
        return ResponseEntity.ok(DamageReportMapper.toDto(persistedReport, fileStorageService));
    }

    /**
     * Deletes a damage report by its UUID.
     *
     * @param reportUuid The UUID of the damage report to delete.
     * @return A ResponseEntity with status 204 No Content.
     */
    @Operation(summary = "Delete a damage report", description = "Deletes a damage report by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Damage report deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Damage report not found with the specified UUID")
    })
    @DeleteMapping("/{reportUuid}")
    public ResponseEntity<Void> deleteDamageReport(
            @Parameter(description = "UUID of the damage report to delete.", required = true) @PathVariable UUID reportUuid) {
        log.warn("ADMIN ACTION: Request to delete damage report with UUID: {}", reportUuid);
        DamageReport existingReport = damageReportService.read(reportUuid);
        damageReportService.delete(existingReport.getId());
        log.info("Successfully deleted damage report with UUID: {}.", reportUuid);
        return ResponseEntity.noContent().build();
    }
}