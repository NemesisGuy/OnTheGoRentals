package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.dto.request.CarCreateDTO;
import za.ac.cput.domain.dto.request.CarUpdateDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.exception.ResourceNotFoundException; // Assuming this exists
import za.ac.cput.service.ICarService;

import java.util.List;
import java.util.UUID;

/**
 * AdminCarController.java
 * Controller for administrative operations on Car entities.
 * Allows administrators to create, retrieve, update, and delete cars.
 * Cars are identified externally by UUIDs, while internal service operations
 * primarily use integer IDs. This controller is responsible for resolving UUIDs
 * to entities before calling service methods that expect entities or their internal IDs.
 *
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 * Updated: [Your Current Date - e.g., 2024-05-28]
 */
@RestController
@RequestMapping("/api/v1/admin/cars")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')") // Class-level security
public class AdminCarController {

    private static final Logger log = LoggerFactory.getLogger(AdminCarController.class);
    private final ICarService carService;

    /**
     * Constructs an AdminCarController with the necessary Car service.
     *
     * @param carService The service implementation for car operations.
     */
    @Autowired
    public AdminCarController(ICarService carService) {
        this.carService = carService;
        log.info("AdminCarController initialized.");
    }

    /**
     * Retrieves all cars for administrative view.
     * Depending on the service implementation of `getAll()`, this might include
     * cars that have been soft-deleted.
     *
     * @return A ResponseEntity containing a list of {@link CarResponseDTO}s, or no content if none exist.
     */
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCarsForAdmin() {
        log.info("Admin request to get all cars.");
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            log.info("No cars found.");
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carResponseDTOS = CarMapper.toDtoList(cars);
        log.info("Successfully retrieved {} cars.", carResponseDTOS.size());
        return ResponseEntity.ok(carResponseDTOS);
    }

    /**
     * Creates a new car entry.
     *
     * @param carCreateDTO The {@link CarCreateDTO} containing the data for the new car.
     * @return A ResponseEntity containing the created {@link CarResponseDTO} and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarCreateDTO carCreateDTO) {
        log.info("Admin request to create a new car with DTO: {}", carCreateDTO);
        Car carToCreate = CarMapper.toEntity(carCreateDTO);
        log.debug("Mapped DTO to Car entity for creation: {}", carToCreate);

        Car createdCarEntity = carService.create(carToCreate);
        // Assuming 'getUuid()' is the method in Car entity to get its UUID. Adjust if different.
        log.info("Successfully created car with ID: {} and UUID: {}", createdCarEntity.getId(), createdCarEntity.getUuid());
        CarResponseDTO responseDto = CarMapper.toDto(createdCarEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific car by its UUID for administrative view.
     *
     * @param carUuid The UUID of the car to retrieve.
     * @return A ResponseEntity containing the {@link CarResponseDTO} if found.
     * @throws ResourceNotFoundException if the car with the given UUID is not found (handled by service).
     */
    @GetMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> getCarByUuidAdmin(@PathVariable UUID carUuid) {
        log.info("Admin request to get car by UUID: {}", carUuid);
        Car carEntity = carService.read(carUuid);
        // The carService.read(UUID) method is expected to throw ResourceNotFoundException if not found.
        log.info("Successfully retrieved car with ID: {} for UUID: {}", carEntity.getId(), carUuid);
        return ResponseEntity.ok(CarMapper.toDto(carEntity));
    }

    /**
     * Updates an existing car identified by its UUID.
     *
     * @param carUuid      The UUID of the car to update.
     * @param carUpdateDTO The {@link CarUpdateDTO} containing the updated data for the car.
     * @return A ResponseEntity containing the updated {@link CarResponseDTO}.
     * @throws ResourceNotFoundException if the car with the given UUID is not found (handled by service).
     */
    @PutMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> updateCar(
            @PathVariable UUID carUuid,
            @Valid @RequestBody CarUpdateDTO carUpdateDTO
    ) {
        log.info("Admin request to update car with UUID: {}. Update DTO: {}", carUuid, carUpdateDTO);
        Car existingCar = carService.read(carUuid); // Fetch current entity to ensure it exists and get its internal ID.
        log.debug("Found existing car with ID: {} for UUID: {}", existingCar.getId(), carUuid);

        Car carWithUpdates = CarMapper.applyUpdateDtoToEntity(carUpdateDTO, existingCar);
        log.debug("Applied DTO updates to Car entity: {}", carWithUpdates);

        Car persistedUpdatedCar = carService.update(carWithUpdates); // Service.update takes the full entity.
        log.info("Successfully updated car with ID: {} and UUID: {}", persistedUpdatedCar.getId(), persistedUpdatedCar.getUuid());
        return ResponseEntity.ok(CarMapper.toDto(persistedUpdatedCar));
    }

    /**
     * Soft-deletes a car identified by its UUID.
     * The controller first retrieves the car entity by its UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param carUuid The UUID of the car to delete.
     * @return A ResponseEntity with no content if successful, or not found if the car doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the car with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{carUuid}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carUuid) {
        log.info("Admin request to delete car with UUID: {}", carUuid);
        Car carToDelete = carService.read(carUuid); // Fetch entity to get its internal ID
        log.debug("Found car with ID: {} (UUID: {}) for deletion.", carToDelete.getId(), carUuid);

        boolean deleted = carService.delete(carToDelete.getId()); // Service method uses internal ID
        if (!deleted) {
            // This might be reached if service.delete(id) returns false for reasons other than "not found"
            // (e.g., business rule, or already deleted and method doesn't re-confirm).
            // If service.delete(id) is expected to throw if ID not found after initial read, this is a different issue.
            log.warn("Car with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", carToDelete.getId(), carUuid);
            return ResponseEntity.notFound().build(); // Or another appropriate status
        }
        log.info("Successfully soft-deleted car with ID: {} (UUID: {}).", carToDelete.getId(), carUuid);
        return ResponseEntity.noContent().build();
    }
}