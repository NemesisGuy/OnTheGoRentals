package za.ac.cput.controllers.admin;

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
import za.ac.cput.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * AdminDriverController.java
 * Controller for administrators to manage Driver entities.
 * Allows admins to create, retrieve, update, and delete drivers.
 * External identification of drivers is by UUID. Internal service operations
 * primarily use integer IDs. This controller bridges that gap.
 *
 * Author: Peter Buckingham
 * Updated by: System/AI
 * Date: [Original Date - e.g., 2023-XX-XX]
 * Updated: [Current Date - e.g., 2024-05-28]
 */
@RestController
@RequestMapping("/api/v1/admin/drivers")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
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
     * Allows an admin to create a new driver.
     *
     * @param driverCreateDTO The {@link DriverCreateDTO} containing the data for the new driver.
     * @return A ResponseEntity containing the created {@link DriverResponseDTO} and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverCreateDTO driverCreateDTO) {
        log.info("Admin request to create a new driver with DTO: {}", driverCreateDTO);
        Driver driverToCreate = DriverMapper.toEntity(driverCreateDTO);
        log.debug("Mapped DTO to Driver entity for creation: {}", driverToCreate);

        Driver createdDriverEntity = driverService.create(driverToCreate);
        log.info("Successfully created driver with ID: {} and UUID: {}", createdDriverEntity.getId(), createdDriverEntity.getUuid());
        DriverResponseDTO responseDto = DriverMapper.toDto(createdDriverEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific driver by their UUID.
     *
     * @param driverUuid The UUID of the driver to retrieve.
     * @return A ResponseEntity containing the {@link DriverResponseDTO} if found.
     * @throws ResourceNotFoundException if the driver with the given UUID is not found (handled by service).
     */
    @GetMapping("/{driverUuid}")
    public ResponseEntity<DriverResponseDTO> getDriverByUuid(@PathVariable UUID driverUuid) {
        log.info("Admin request to get driver by UUID: {}", driverUuid);
        Driver driverEntity = driverService.read(driverUuid);
        // The driverService.read(UUID) method is expected to throw ResourceNotFoundException if not found.
        log.info("Successfully retrieved driver with ID: {} for UUID: {}", driverEntity.getId(), driverEntity.getUuid());
        return ResponseEntity.ok(DriverMapper.toDto(driverEntity));
    }

    /**
     * Allows an admin to update an existing driver identified by their UUID.
     *
     * @param driverUuid      The UUID of the driver to update.
     * @param driverUpdateDTO The {@link DriverUpdateDTO} containing the updated data for the driver.
     * @return A ResponseEntity containing the updated {@link DriverResponseDTO}.
     * @throws ResourceNotFoundException if the driver with the given UUID is not found (handled by service).
     */
    @PutMapping("/{driverUuid}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable UUID driverUuid,
            @Valid @RequestBody DriverUpdateDTO driverUpdateDTO
    ) {
        log.info("Admin request to update driver with UUID: {}. Update DTO: {}", driverUuid, driverUpdateDTO);
        Driver existingDriver = driverService.read(driverUuid);
        log.debug("Found existing driver with ID: {} and UUID: {}", existingDriver.getId(), existingDriver.getUuid());

        Driver driverWithUpdates = DriverMapper.applyUpdateDtoToEntity(driverUpdateDTO, existingDriver);
        log.debug("Applied DTO updates to Driver entity: {}", driverWithUpdates);

        Driver persistedUpdatedDriver = driverService.update(driverWithUpdates);
        log.info("Successfully updated driver with ID: {} and UUID: {}", persistedUpdatedDriver.getId(), persistedUpdatedDriver.getUuid());
        return ResponseEntity.ok(DriverMapper.toDto(persistedUpdatedDriver));
    }

    /**
     * Allows an admin to soft-delete a driver by their UUID.
     * The controller first retrieves the driver by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param driverUuid The UUID of the driver to delete.
     * @return A ResponseEntity with no content if successful, or not found if the driver doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the driver with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{driverUuid}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID driverUuid) {
        log.info("Admin request to delete driver with UUID: {}", driverUuid);
        // First read to ensure it exists and to get the internal ID for the service's delete(id) method.
        Driver driverToDelete = driverService.read(driverUuid);
        log.debug("Found driver with ID: {} (UUID: {}) for deletion.", driverToDelete.getId(), driverToDelete.getUuid());

        // The service's delete method should take the internal integer ID.
        boolean deleted = driverService.delete(driverToDelete.getId());

        if (!deleted) {
            // This case is usually if the service.delete(id) returns false for some reason
            // other than "not found", since the read(UUID) should have already thrown if it didn't exist.
            // Or if the service's delete(UUID) was called directly and it handles "not found" by returning false.
            // For consistency with `delete(driverToDelete.getId())`, `ResourceNotFoundException` during the read phase
            // is the primary way to handle "not found".
            log.warn("Driver with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", driverToDelete.getId(), driverToDelete.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully soft-deleted driver with ID: {} (UUID: {}).", driverToDelete.getId(), driverToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a list of all drivers for administrative view.
     * Depending on the service implementation, this might include drivers
     * that have been soft-deleted.
     *
     * @return A ResponseEntity containing a list of {@link DriverResponseDTO}s, or no content if none exist.
     */
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        log.info("Admin request to get all drivers.");
        List<Driver> drivers = driverService.getAll();
        if (drivers.isEmpty()) {
            log.info("No drivers found.");
            return ResponseEntity.noContent().build();
        }
        List<DriverResponseDTO> driverDTOs = DriverMapper.toDtoList(drivers);
        log.info("Successfully retrieved {} drivers.", driverDTOs.size());
        return ResponseEntity.ok(driverDTOs);
    }
}