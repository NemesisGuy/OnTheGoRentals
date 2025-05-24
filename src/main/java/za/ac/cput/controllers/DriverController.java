package za.ac.cput.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Driver; // For service layer interaction
import za.ac.cput.domain.dto.request.DriverCreateDTO;
import za.ac.cput.domain.dto.request.DriverUpdateDTO; // Using DriverUpdateDTO
import za.ac.cput.domain.dto.response.DriverResponseDTO;
import za.ac.cput.domain.mapper.DriverMapper;
import za.ac.cput.service.IDriverService; // Use interface
import za.ac.cput.exception.ResourceNotFoundException; // For explicit error handling

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
// @CrossOrigin(...) // Prefer global CORS configuration
public class DriverController {

    private final IDriverService driverService;

    @Autowired
    public DriverController(IDriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverCreateDTO driverCreateDTO) {
        Driver driverToCreate = DriverMapper.toEntity(driverCreateDTO);
        Driver createdDriverEntity = driverService.create(driverToCreate);
        DriverResponseDTO responseDto = DriverMapper.toDto(createdDriverEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{driverUuid}")
    public ResponseEntity<DriverResponseDTO> getDriverByUuid(@PathVariable UUID driverUuid) {
        Driver driverEntity = driverService.read(driverUuid); // Service throws ResourceNotFoundException if not found
        DriverResponseDTO responseDto = DriverMapper.toDto(driverEntity);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{driverUuid}")
    public ResponseEntity<DriverResponseDTO> updateDriver(
            @PathVariable UUID driverUuid,
            @Valid @RequestBody DriverUpdateDTO driverUpdateDTO // Using DriverUpdateDTO
    ) {
        Driver existingDriver = driverService.read(driverUuid); // Fetch existing entity
        // Throws ResourceNotFoundException if not found, handled by @ControllerAdvice or default Spring

        existingDriver = DriverMapper.updateEntityFromDto(driverUpdateDTO, existingDriver); // Apply DTO changes to the entity

        Driver updatedDriverEntity = driverService.update(existingDriver); // Service saves the modified entity

        DriverResponseDTO responseDto = DriverMapper.toDto(updatedDriverEntity);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{driverUuid}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID driverUuid) {
        Driver driver = driverService.read(driverUuid); // Service throws ResourceNotFoundException if not found
        boolean deleted = driverService.delete(driver.getId()); // Service handles logic, throws if not found
        if (!deleted) {
            // This case should ideally be covered if service.softDeleteByUuid throws ResourceNotFoundException
            // For now, if it returns false for "not found and thus not deleted":
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        List<Driver> drivers = driverService.getAll();
        List<DriverResponseDTO> driverDTOs = DriverMapper.toDtoList(drivers);
        if (driverDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(driverDTOs);
    }
}