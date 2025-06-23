package za.ac.cput.controllers;

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
import org.springframework.beans.factory.annotation.Value;
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
 * DamageReportController.java
 * Controller for handling operations related to Damage Reports.
 * Follows modern REST conventions and uses DTOs for all public-facing data.
 *
 * @author Cwenga Dlova (214310671)
 * @version 2.0
 * Updated by: Peter Buckingham (220165289)
 */
@RestController
@RequestMapping("/api/v1/damage-reports")
@Tag(name = "Damage Report Management", description = "Endpoints for creating and managing damage reports.")
public class DamageReportController {

    private static final Logger log = LoggerFactory.getLogger(DamageReportController.class);

    private final IDamageReportService damageReportService;
    private final IRentalService rentalService; // Needed to fetch the associated Rental entity
    private final IFileStorageService fileStorageService; // Needed for the mapper
    private final String publicApiUrl;

    /**
     * Constructs the controller with necessary service dependencies.
     *
     * @param damageReportService The service for damage report logic.
     * @param rentalService       The service to look up rental records.
     * @param fileStorageService  The service for generating image URLs for nested DTOs.
     */
    @Autowired
    public DamageReportController(IDamageReportService damageReportService, IRentalService rentalService, IFileStorageService fileStorageService,
                                    @Value("${app.public-api-url}") String publicApiUrl
     ) {
        this.damageReportService = damageReportService;
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        this.publicApiUrl = publicApiUrl; // Initialize the public API URL
        log.info("DamageReportController initialized.");
    }

    /**
     * Creates a new damage report associated with a specific rental.
     *
     * @param createDto The DTO containing the details of the damage report.
     * @return A ResponseEntity containing the created DamageReportResponseDTO.
     */
    @Operation(summary = "Create a new damage report", description = "Creates a new damage report and associates it with an existing rental.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Damage report created successfully", content = @Content(schema = @Schema(implementation = DamageReportResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "404", description = "Associated rental not found")
    })
    @PostMapping
    public ResponseEntity<DamageReportResponseDTO> createDamageReport(@Valid @RequestBody DamageReportCreateDTO createDto) {
        log.info("Request received to create a damage report for rental UUID: {}", createDto.getRentalUuid());

        Rental rentalEntity = rentalService.read(createDto.getRentalUuid()); // Throws ResourceNotFoundException if not found
        DamageReport damageReportToCreate = DamageReportMapper.toEntity(createDto, rentalEntity);
        DamageReport createdReport = damageReportService.create(damageReportToCreate);

        log.info("Successfully created damage report with UUID: {}", createdReport.getUuid());
        return new ResponseEntity<>(DamageReportMapper.toDto(createdReport, fileStorageService, publicApiUrl), HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific damage report by its UUID.
     *
     * @param reportUuid The UUID of the damage report to retrieve.
     * @return A ResponseEntity containing the DamageReportResponseDTO.
     */
    @Operation(summary = "Get a damage report by UUID", description = "Retrieves a specific damage report by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Damage report found", content = @Content(schema = @Schema(implementation = DamageReportResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Damage report not found")
    })
    @GetMapping("/{reportUuid}")
    public ResponseEntity<DamageReportResponseDTO> getDamageReportByUuid(
            @Parameter(description = "UUID of the damage report to retrieve", required = true) @PathVariable UUID reportUuid) {
        log.info("Request received to get damage report with UUID: {}", reportUuid);
        DamageReport report = damageReportService.read(reportUuid);
        return ResponseEntity.ok(DamageReportMapper.toDto(report, fileStorageService , publicApiUrl));
    }

    /**
     * Retrieves all damage reports in the system.
     *
     * @return A ResponseEntity containing a list of all DamageReportResponseDTOs.
     */
    @Operation(summary = "Get all damage reports", description = "Retrieves a list of all damage reports.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "204", description = "No damage reports found")
    })
    @GetMapping
    public ResponseEntity<List<DamageReportResponseDTO>> getAllDamageReports() {
        log.info("Request received to get all damage reports.");
        List<DamageReport> reports = damageReportService.getAll();
        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(DamageReportMapper.toDtoList(reports, fileStorageService, publicApiUrl));
    }

    /**
     * Updates an existing damage report.
     *
     * @param reportUuid The UUID of the damage report to update.
     * @param updateDto  The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated DamageReportResponseDTO.
     */
    @Operation(summary = "Update an existing damage report", description = "Updates the details of an existing damage report by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Damage report updated successfully", content = @Content(schema = @Schema(implementation = DamageReportResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "404", description = "Damage report not found")
    })
    @PutMapping("/{reportUuid}")
    public ResponseEntity<DamageReportResponseDTO> updateDamageReport(
            @Parameter(description = "UUID of the damage report to update", required = true) @PathVariable UUID reportUuid,
            @Valid @RequestBody DamageReportUpdateDTO updateDto) {
        log.info("Request received to update damage report with UUID: {}", reportUuid);
        DamageReport existingReport = damageReportService.read(reportUuid);
        DamageReport reportWithUpdates = DamageReportMapper.applyUpdateDtoToEntity(updateDto, existingReport);
        DamageReport updatedReport = damageReportService.update(reportWithUpdates);

        log.info("Successfully updated damage report with UUID: {}", updatedReport.getUuid());
        return ResponseEntity.ok(DamageReportMapper.toDto(updatedReport, fileStorageService,publicApiUrl));
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
            @ApiResponse(responseCode = "404", description = "Damage report not found")
    })
    @DeleteMapping("/{reportUuid}")
    public ResponseEntity<Void> deleteDamageReport(
            @Parameter(description = "UUID of the damage report to delete", required = true) @PathVariable UUID reportUuid) {
        log.warn("Request received to delete damage report with UUID: {}", reportUuid);

        DamageReport existingReport = damageReportService.read(reportUuid); // Throws ResourceNotFoundException if not found
        if (existingReport == null) {
            log.warn("Damage report with UUID {} not found for deletion.", reportUuid);
            return ResponseEntity.notFound().build();
        }
        log.debug("Found damage report with ID: {} for deletion.", existingReport.getId());
        damageReportService.delete(existingReport.getId()); // Assumes service method handles existence check
        log.info("Successfully deleted damage report with UUID: {}", reportUuid);
        return ResponseEntity.noContent().build();
    }
}