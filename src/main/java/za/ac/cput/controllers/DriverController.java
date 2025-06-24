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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.DriverCreateDTO;
import za.ac.cput.domain.dto.request.DriverUpdateDTO;
import za.ac.cput.domain.dto.response.DriverResponseDTO;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.mapper.DriverMapper;
import za.ac.cput.service.IDriverService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * DriverController.java
 * Controller for managing Driver entities. This controller provides CRUD operations for drivers.
 * Access control for these operations should be defined by Spring Security configurations.
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/drivers")
@Tag(name = "Driver Management", description = "Endpoints for creating and managing driver information.")
public class DriverController {

    private static final Logger log = LoggerFactory.getLogger(DriverController.class);
    private final IDriverService driverService;

    /**
     * Constructs a DriverController with the necessary Driver service.
     *
     * @param driverService The service implementation for driver operations.
     */
    @Autowired
    public DriverController(IDriverService driverService) {
        this.driverService = driverService;
        log.info("DriverController initialized.");
    }

    /**
     * Creates a new driver. Access control for this operation should be managed by Spring Security
     * (e.g., requiring an admin role).
     *
     * @param driverCreateDTO The DTO containing the data for the new driver.
     * @return A ResponseEntity containing the created driver's DTO and HTTP status 201 CREATED.
     */
    @Operation(summary = "Create a new driver", description = "Creates a new driver in the system. Requires appropriate admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Driver created successfully", content = @Content(schema = @Schema(implementation = DriverResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid driver data provided"),
            @ApiResponse(responseCode = "403", description = "User not authorized to create a driver")
    })
    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(
            @Valid @RequestBody DriverCreateDTO driverCreateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new driver with DTO: {}", requesterId, driverCreateDTO);

        Driver driverToCreate = DriverMapper.toEntity(driverCreateDTO);
        Driver createdDriverEntity = driverService.create(driverToCreate);

        log.info("Requester [{}]: Successfully created driver with UUID: {}", requesterId, createdDriverEntity.getUuid());
        return new ResponseEntity<>(DriverMapper.toDto(createdDriverEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific driver by their UUID.
     *
     * @param driverUuid The UUID of the driver to retrieve.
     * @return A ResponseEntity containing the driver's DTO if found.
     */
    @Operation(summary = "Get driver by UUID", description = "Retrieves a specific driver by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver found", content = @Content(schema = @Schema(implementation = DriverResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Driver not found with the specified UUID")
    })
    @GetMapping("/{driverUuid}")
    public ResponseEntity<DriverResponseDTO> getDriverByUuid(
            @Parameter(description = "UUID of the driver to retrieve", required = true) @PathVariable UUID driverUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get driver by UUID: {}", requesterId, driverUuid);

        Driver driverEntity = driverService.read(driverUuid); // Service should throw ResourceNotFoundException
        return ResponseEntity.ok(DriverMapper.toDto(driverEntity));
    }

    /**
     * Retrieves a list of all drivers in the system.
     *
     * @return A ResponseEntity containing a list of all driver DTOs, or 204 No Content if none exist.
     */
    @Operation(summary = "Get all drivers", description = "Retrieves a list of all drivers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of drivers"),
            @ApiResponse(responseCode = "204", description = "No drivers found in the system")
    })
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all drivers.", requesterId);

        List<Driver> drivers = driverService.getAll();
        if (drivers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(DriverMapper.toDtoList(drivers));
    }

    /**
     * Updates an existing driver identified by their UUID.
     *
     * @param driverUuid      The UUID of the driver to update.
     * @param driverUpdateDTO The DTO containing the updated data.
     * @return A ResponseEntity containing the updated driver's DTO.
     */
    @Operation(summary = "Update an existing driver", description = "Updates the details of an existing driver by their UUID. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver updated successfully", content = @Content(schema = @Schema(implementation = DriverResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data provided"),
            @ApiResponse(responseCode = "404", description = "Driver not found with the specified UUID")
    })
    @PutMapping("/{driverUuid}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @Parameter(description = "UUID of the driver to update", required = true) @PathVariable UUID driverUuid,
            @Valid @RequestBody DriverUpdateDTO driverUpdateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update driver with UUID: {}", requesterId, driverUuid);

        Driver existingDriver = driverService.read(driverUuid);
        Driver driverWithUpdates = DriverMapper.applyUpdateDtoToEntity(driverUpdateDTO, existingDriver);
        Driver updatedDriverEntity = driverService.update(driverWithUpdates);

        log.info("Requester [{}]: Successfully updated driver with UUID: {}", requesterId, updatedDriverEntity.getUuid());
        return ResponseEntity.ok(DriverMapper.toDto(updatedDriverEntity));
    }

    /**
     * Soft-deletes a driver by their UUID.
     *
     * @param driverUuid The UUID of the driver to delete.
     * @return A ResponseEntity with status 204 No Content if successful.
     */
    @Operation(summary = "Delete a driver by UUID", description = "Soft-deletes a driver by their UUID. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Driver deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found with the specified UUID")
    })
    @DeleteMapping("/{driverUuid}")
    public ResponseEntity<Void> deleteDriver(
            @Parameter(description = "UUID of the driver to delete", required = true) @PathVariable UUID driverUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("ADMIN ACTION: Requester [{}] attempting to delete driver with UUID: {}", requesterId, driverUuid);

        Driver driverToDelete = driverService.read(driverUuid);
        driverService.delete(driverToDelete.getId());

        log.info("Requester [{}]: Successfully soft-deleted driver with UUID: {}.", requesterId, driverUuid);
        return ResponseEntity.noContent().build();
    }
}