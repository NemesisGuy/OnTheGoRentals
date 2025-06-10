package za.ac.cput.controllers;

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
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IDriverService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * DriverController.java
 * Controller for managing Driver entities.
 * This controller provides CRUD operations for drivers.
 * The specific access control (e.g., public, authenticated user, specific role)
 * for these operations would be defined by Spring Security configurations.
 * <p>
 * Author: Peter Buckingham
 * Date: 20/10/2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/drivers")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize(...) // Add if specific authorization is needed for the whole controller or per method
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
     * Creates a new driver.
     * The requester's identity is logged. Access control for this operation
     * should be managed by Spring Security (e.g., requiring admin or a specific role).
     *
     * @param driverCreateDTO The {@link DriverCreateDTO} containing the data for the new driver.
     * @return A ResponseEntity containing the created {@link DriverResponseDTO} and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverCreateDTO driverCreateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new driver with DTO: {}", requesterId, driverCreateDTO);

        Driver driverToCreate = DriverMapper.toEntity(driverCreateDTO);
        log.debug("Requester [{}]: Mapped DTO to Driver entity for creation: {}", requesterId, driverToCreate);

        Driver createdDriverEntity = driverService.create(driverToCreate);
        log.info("Requester [{}]: Successfully created driver with ID: {} and UUID: {}",
                requesterId, createdDriverEntity.getId(), createdDriverEntity.getUuid());
        DriverResponseDTO responseDto = DriverMapper.toDto(createdDriverEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific driver by their UUID.
     * This endpoint might be public or require authentication depending on security configuration.
     *
     * @param driverUuid The UUID of the driver to retrieve.
     * @return A ResponseEntity containing the {@link DriverResponseDTO} if found.
     * @throws ResourceNotFoundException if the driver with the given UUID is not found (handled by service).
     */
    @GetMapping("/{driverUuid}")
    public ResponseEntity<DriverResponseDTO> getDriverByUuid(@PathVariable UUID driverUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get driver by UUID: {}", requesterId, driverUuid);

        // driverService.read(UUID) is expected to throw ResourceNotFoundException if not found.
        Driver driverEntity = driverService.read(driverUuid);
        log.info("Requester [{}]: Successfully retrieved driver with ID: {} for UUID: {}",
                requesterId, driverEntity.getId(), driverEntity.getUuid());
        DriverResponseDTO responseDto = DriverMapper.toDto(driverEntity);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Updates an existing driver identified by their UUID.
     * Access control for this operation should be managed by Spring Security.
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
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update driver with UUID: {}. Update DTO: {}",
                requesterId, driverUuid, driverUpdateDTO);

        // Fetch existing entity; service.read(UUID) should throw if not found.
        Driver existingDriver = driverService.read(driverUuid);
        log.debug("Requester [{}]: Found existing driver ID: {}, UUID: {} for update.",
                requesterId, existingDriver.getId(), existingDriver.getUuid());

        // Apply DTO changes to the entity
        Driver driverWithUpdates = DriverMapper.applyUpdateDtoToEntity(driverUpdateDTO, existingDriver);
        log.debug("Requester [{}]: Mapped DTO to update Driver entity: {}", requesterId, driverWithUpdates);


        Driver updatedDriverEntity = driverService.update(driverWithUpdates); // Service saves the modified entity (which has ID)
        log.info("Requester [{}]: Successfully updated driver with ID: {} and UUID: {}",
                requesterId, updatedDriverEntity.getId(), updatedDriverEntity.getUuid());
        DriverResponseDTO responseDto = DriverMapper.toDto(updatedDriverEntity);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Soft-deletes a driver by their UUID.
     * Access control for this operation should be managed by Spring Security.
     * The controller first retrieves the driver by UUID to obtain their internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param driverUuid The UUID of the driver to delete.
     * @return A ResponseEntity with no content if successful, or 404 Not Found if the driver doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the driver with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{driverUuid}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID driverUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to delete driver with UUID: {}", requesterId, driverUuid);

        // Service's read(UUID) should throw ResourceNotFoundException if not found.
        Driver driverToDelete = driverService.read(driverUuid);
        log.debug("Requester [{}]: Found driver ID: {} (UUID: {}) for deletion.",
                requesterId, driverToDelete.getId(), driverToDelete.getUuid());

        // Service handles logic using the internal ID.
        boolean deleted = driverService.delete(driverToDelete.getId());
        if (!deleted) {
            // This case is generally hit if the service's delete(id) method returns false
            // for a reason other than "not found by id" (since read(uuid) should have caught that).
            // e.g., business rule preventing deletion, or already deleted.
            log.warn("Requester [{}]: Driver with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.",
                    requesterId, driverToDelete.getId(), driverToDelete.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Requester [{}]: Successfully soft-deleted driver with ID: {} (UUID: {}).",
                requesterId, driverToDelete.getId(), driverToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a list of all drivers.
     * This endpoint might be public or require authentication depending on security configuration.
     *
     * @return A ResponseEntity containing a list of {@link DriverResponseDTO}s. Returns 204 No Content if no drivers exist.
     */
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all drivers.", requesterId);

        List<Driver> drivers = driverService.getAll();
        if (drivers.isEmpty()) {
            log.info("Requester [{}]: No drivers found.", requesterId);
            return ResponseEntity.noContent().build();
        }
        List<DriverResponseDTO> driverDTOs = DriverMapper.toDtoList(drivers);
        log.info("Requester [{}]: Successfully retrieved {} drivers.", requesterId, driverDTOs.size());
        return ResponseEntity.ok(driverDTOs);
    }
}