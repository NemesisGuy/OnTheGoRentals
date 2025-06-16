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
import za.ac.cput.domain.dto.request.DriverCreateDTO;
import za.ac.cput.domain.dto.request.DriverUpdateDTO;
import za.ac.cput.domain.dto.response.DriverResponseDTO;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.mapper.DriverMapper;
import za.ac.cput.service.IDriverService;

import java.util.List;
import java.util.UUID;

/**
 * AdminDriverController.java
 * Controller for administrators to manage Driver entities.
 * Allows admins to perform full CRUD operations on drivers.
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/drivers")
@Tag(name = "Admin: Driver Management", description = "Endpoints for administrators to manage driver information.")
public class AdminDriverController {

    private static final Logger log = LoggerFactory.getLogger(AdminDriverController.class);
    private final IDriverService driverService;

    /**
     * Constructs an AdminDriverController with the necessary Driver service.
     *
     * @param driverService The service implementation for driver operations.
     */
    @Autowired
    public AdminDriverController(IDriverService driverService) {
        this.driverService = driverService;
        log.info("AdminDriverController initialized.");
    }

    /**
     * Creates a new driver.
     *
     * @param driverCreateDTO The DTO containing the data for the new driver.
     * @return A ResponseEntity containing the created driver's DTO and HTTP status 201 Created.
     */
    @Operation(summary = "Create a new driver", description = "Allows an administrator to add a new driver to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Driver created successfully", content = @Content(schema = @Schema(implementation = DriverResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid driver data provided")
    })
    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverCreateDTO driverCreateDTO) {
        log.info("Admin request to create a new driver with DTO: {}", driverCreateDTO);
        Driver driverToCreate = DriverMapper.toEntity(driverCreateDTO);
        Driver createdDriverEntity = driverService.create(driverToCreate);
        log.info("Successfully created driver with UUID: {}", createdDriverEntity.getUuid());
        return new ResponseEntity<>(DriverMapper.toDto(createdDriverEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves a list of all drivers for administrative view.
     *
     * @return A ResponseEntity containing a list of all driver DTOs.
     */
    @Operation(summary = "Get all drivers", description = "Retrieves a list of all drivers in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of drivers"),
            @ApiResponse(responseCode = "204", description = "No drivers found in the system")
    })
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        log.info("Admin request to get all drivers.");
        List<Driver> drivers = driverService.getAll();
        if (drivers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(DriverMapper.toDtoList(drivers));
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
        log.info("Admin request to get driver by UUID: {}", driverUuid);
        Driver driverEntity = driverService.read(driverUuid);
        return ResponseEntity.ok(DriverMapper.toDto(driverEntity));
    }

    /**
     * Updates an existing driver identified by their UUID.
     *
     * @param driverUuid      The UUID of the driver to update.
     * @param driverUpdateDTO The DTO containing the updated data for the driver.
     * @return A ResponseEntity containing the updated driver's DTO.
     */
    @Operation(summary = "Update an existing driver", description = "Updates the details of an existing driver by their UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver updated successfully", content = @Content(schema = @Schema(implementation = DriverResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data provided"),
            @ApiResponse(responseCode = "404", description = "Driver not found with the specified UUID")
    })
    @PutMapping("/{driverUuid}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @Parameter(description = "UUID of the driver to update", required = true) @PathVariable UUID driverUuid,
            @Valid @RequestBody DriverUpdateDTO driverUpdateDTO) {
        log.info("Admin request to update driver with UUID: {}", driverUuid);
        Driver existingDriver = driverService.read(driverUuid);
        Driver driverWithUpdates = DriverMapper.applyUpdateDtoToEntity(driverUpdateDTO, existingDriver);
        Driver updatedDriverEntity = driverService.update(driverWithUpdates);
        log.info("Successfully updated driver with UUID: {}", updatedDriverEntity.getUuid());
        return ResponseEntity.ok(DriverMapper.toDto(updatedDriverEntity));
    }

    /**
     * Soft-deletes a driver by their UUID.
     *
     * @param driverUuid The UUID of the driver to delete.
     * @return A ResponseEntity with status 204 No Content if successful.
     */
    @Operation(summary = "Delete a driver by UUID", description = "Soft-deletes a driver by their UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Driver deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found with the specified UUID")
    })
    @DeleteMapping("/{driverUuid}")
    public ResponseEntity<Void> deleteDriver(
            @Parameter(description = "UUID of the driver to delete", required = true) @PathVariable UUID driverUuid) {
        log.warn("ADMIN ACTION: Request to delete driver with UUID: {}", driverUuid);
        Driver driverToDelete = driverService.read(driverUuid);
        driverService.delete(driverToDelete.getId());
        log.info("Successfully soft-deleted driver with UUID: {}.", driverUuid);
        return ResponseEntity.noContent().build();
    }
}