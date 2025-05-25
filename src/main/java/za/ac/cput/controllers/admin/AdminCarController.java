package za.ac.cput.controllers.admin;

/**
 * AdminCarController.java
 * Controller for the Car entity (admin only)
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023 // Updated: [Your Current Date]
 */


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Car; // For service layer interaction
import za.ac.cput.domain.dto.request.CarCreateDTO;
import za.ac.cput.domain.dto.request.CarUpdateDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.service.ICarService; // Inject interface

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cars") // Standardized base path with version
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')") // Class-level security
public class AdminCarController {

    private final ICarService carService;

    @Autowired
    public AdminCarController(ICarService carService) {
        this.carService = carService;
    }

    /**
     * Retrieves all cars for administrative view.
     * This might include soft-deleted cars if the service method `getAll()` is configured to do so.
     */
    @GetMapping // GET /api/v1/admin/cars (replaces /all)
    public ResponseEntity<List<CarResponseDTO>> getAllCarsForAdmin() {
        List<Car> cars = carService.getAll(); // Service returns entities
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(cars));
    }

    /**
     * Creates a new car.
     */
    @PostMapping // POST /api/v1/admin/cars (replaces /create)
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarCreateDTO carCreateDTO) {
        Car carToCreate = CarMapper.toEntity(carCreateDTO); // Map DTO to entity
        Car createdCarEntity = carService.create(carToCreate); // Service takes entity
        CarResponseDTO responseDto = CarMapper.toDto(createdCarEntity); // Map result to DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific car by its UUID for administrative view.
     */
    @GetMapping("/{carUuid}") // GET /api/v1/admin/cars/{uuid_value} (replaces /read/{carId})
    public ResponseEntity<CarResponseDTO> getCarByUuidAdmin(@PathVariable UUID carUuid) {
        // Service's readByUuid might fetch including soft-deleted for admin.
        // If not, use a specific adminReadByUuid or similar.
        Car carEntity = carService.read(carUuid); // Service returns entity
        // readByUuid should throw ResourceNotFoundException if not found.
        return ResponseEntity.ok(CarMapper.toDto(carEntity));
    }

    /**
     * Updates an existing car identified by its UUID.
     */
    @PutMapping("/{carUuid}") // PUT /api/v1/admin/cars/{uuid_value} (replaces /update/{carId})
    public ResponseEntity<CarResponseDTO> updateCar(
            @PathVariable UUID carUuid,
            @Valid @RequestBody CarUpdateDTO carUpdateDTO
    ) {
        Car existingCar = carService.read(carUuid); // Fetch current entity state
        // readByUuid should throw ResourceNotFoundException if not found.

        // Mapper creates a new entity instance with updates applied
        Car carWithUpdates = CarMapper.applyUpdateDtoToEntity(carUpdateDTO, existingCar);

        // Service's update method receives this new instance with the same ID.
        // JPA will treat save() on this as an update to the existing record.
        Car persistedUpdatedCar = carService.update(carWithUpdates);

        return ResponseEntity.ok(CarMapper.toDto(persistedUpdatedCar));
    }

    /**
     * Soft-deletes a car by its UUID.
     */
    @DeleteMapping("/{carUuid}") // DELETE /api/v1/admin/cars/{uuid_value} (replaces /delete/{carId})
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carUuid) {
        Car car  = carService.read(carUuid); // Fetch current entity state
        boolean deleted = carService.delete(car.getId()); // Service handles logic
        // softDeleteByUuid should throw ResourceNotFoundException if not found for more consistent error handling,
        // or controller checks the boolean return value.
        if (!deleted) {
            return ResponseEntity.notFound().build(); // If service returns false for not found
        }
        return ResponseEntity.noContent().build();
    }
}