package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.DriverCreateDTO;
import za.ac.cput.domain.dto.request.DriverUpdateDTO;
import za.ac.cput.domain.dto.response.DriverResponseDTO;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.mapper.DriverMapper;
import za.ac.cput.service.IDriverService; // Inject interface
import za.ac.cput.exception.ResourceNotFoundException; // For explicit error handling

import java.util.List;
import java.util.UUID;

/**
 * AdminDriverController.java
 * Controller for Admin to manage Driver entities.
 * Author: Peter Buckingham // Updated by: [Your Name]
 * Date: [Original Date] // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/admin/drivers") // Standardized base path
// @CrossOrigin(...) // Prefer global CORS
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminDriverController {

    private final IDriverService driverService;

    @Autowired
    public AdminDriverController(IDriverService driverService) {
        this.driverService = driverService;
    }

    /**
     * Admin creates a new driver.
     */
    @PostMapping // POST /api/v1/admin/drivers
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverCreateDTO driverCreateDTO) {
        Driver driverToCreate = DriverMapper.toEntity(driverCreateDTO); // Map DTO to entity
        Driver createdDriverEntity = driverService.create(driverToCreate); // Service takes entity
        DriverResponseDTO responseDto = DriverMapper.toDto(createdDriverEntity); // Map result to DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Admin retrieves a specific driver by their UUID.
     */
    @GetMapping("/{driverUuid}") // GET /api/v1/admin/drivers/{uuid_value}
    public ResponseEntity<DriverResponseDTO> getDriverByUuid(@PathVariable UUID driverUuid) {
        Driver driverEntity = driverService.read(driverUuid); // Service returns entity
        // readByUuid should throw ResourceNotFoundException if not found.
        return ResponseEntity.ok(DriverMapper.toDto(driverEntity));
    }

    /**
     * Admin updates an existing driver identified by their UUID.
     */
    @PutMapping("/{driverUuid}") // PUT /api/v1/admin/drivers/{uuid_value}
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable UUID driverUuid,
            @Valid @RequestBody DriverUpdateDTO driverUpdateDTO
    ) {
        Driver existingDriver = driverService.read(driverUuid); // Fetch current entity state
        // readByUuid should throw ResourceNotFoundException if not found.

        // Mapper creates a new entity instance with updates applied, based on the existing one
        Driver driverWithUpdates = DriverMapper.applyUpdateDtoToEntity(driverUpdateDTO, existingDriver);

        // Service's update method receives this new instance which has the same ID.
        Driver persistedUpdatedDriver = driverService.update(driverWithUpdates);

        return ResponseEntity.ok(DriverMapper.toDto(persistedUpdatedDriver));
    }

    /**
     * Admin soft-deletes a driver by their UUID.
     */
    @DeleteMapping("/{driverUuid}") // DELETE /api/v1/admin/drivers/{uuid_value}
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID driverUuid) {
        Driver driver = driverService.read(driverUuid); // Service returns entity, throws ResourceNotFoundException if not found
        boolean deleted = driverService.delete(driverUuid); // Service handles logic
        // If service throws ResourceNotFoundException, a @ControllerAdvice would handle it.
        // If service returns false for "not found":
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin retrieves a list of all drivers.
     * Service method `getAll()` might include soft-deleted ones for admin view.
     */
    @GetMapping // GET /api/v1/admin/drivers (replaces /get-all)
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        List<Driver> drivers = driverService.getAll(); // Service returns List<Driver> entities
        List<DriverResponseDTO> driverDTOs = DriverMapper.toDtoList(drivers);
        if (driverDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(driverDTOs);
    }

    // Your original factory usage is removed as mapping is now done via DriverMapper.
    // Your original integer ID endpoints are removed in favor of UUID based ones for this admin controller.
    // If you still need integer ID access for some specific internal admin function,
    // they would need distinct paths, e.g., /api/v1/admin/drivers/internal/{id}
}