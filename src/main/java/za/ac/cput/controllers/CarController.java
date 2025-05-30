package za.ac.cput.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.exception.ResourceNotFoundException; // Good for consistency
import za.ac.cput.service.ICarService; // Import interface
// import za.ac.cput.service.IRentalService; // Not directly used in these methods
import za.ac.cput.utils.SecurityUtils; // Import your helper

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CarController.java
 * Controller for public access to car information.
 * Provides endpoints to list all cars, filter by price group,
 * list available cars, and retrieve details of a specific car by UUID.
 *
 * Author: [Original Author Name - Please specify if known]
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/cars") // Standard public API path
public class CarController {

    private static final Logger log = LoggerFactory.getLogger(CarController.class);

    private final ICarService carService; // Use interface
    // private final IRentalService rentalService; // Not directly used in these methods

    /**
     * Constructs a CarController with the necessary Car service.
     *
     * @param carService    The car service for car data operations.
     * //@param rentalService The rental service (if needed for future car-related logic).
     */
    @Autowired
    public CarController(ICarService carService /*, IRentalService rentalService */) {
        this.carService = carService;
        // this.rentalService = rentalService; // Removed as not used
        log.info("CarController initialized.");
    }

    /**
     * Retrieves a list of all cars, regardless of their availability or status.
     * This endpoint is publicly accessible.
     *
     * @return A ResponseEntity containing a list of {@link CarResponseDTO}s. Returns 204 No Content if no cars exist.
     */
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all cars.", requesterId);

        List<Car> cars = carService.getAll(); // Assumes getAll() returns non-deleted cars by default or all for public view
        if (cars.isEmpty()) {
            log.info("Requester [{}]: No cars found in the system.", requesterId);
            return ResponseEntity.noContent().build();
        }
        // Efficient mapping using CarMapper.toDtoList if available, otherwise stream().map() is fine.
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);
        log.info("Requester [{}]: Successfully retrieved {} cars.", requesterId, carDTOs.size());
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Retrieves a list of cars filtered by a specific price group.
     * This endpoint is publicly accessible.
     *
     * @param group The {@link PriceGroup} to filter by.
     * @return A ResponseEntity containing a list of {@link CarResponseDTO}s matching the price group. Returns 204 No Content if no cars match.
     */
    @GetMapping("/price-group/{group}")
    public ResponseEntity<List<CarResponseDTO>> getCarsByPriceGroup(@PathVariable PriceGroup group) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get cars by price group: {}", requesterId, group);

        List<Car> cars = carService.findAllAvailableByPriceGroup(group);
        if (cars.isEmpty()) {
            log.info("Requester [{}]: No cars found for price group: {}", requesterId, group);
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);
        log.info("Requester [{}]: Successfully retrieved {} cars for price group: {}", requesterId, carDTOs.size(), group);
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Retrieves a list of cars that are currently marked as available and not soft-deleted.
     * This endpoint is publicly accessible.
     *
     * @return A ResponseEntity containing a list of available {@link CarResponseDTO}s. Returns 204 No Content if no cars are available.
     */
    @GetMapping("/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCars() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all available cars.", requesterId);

        // The service layer should ideally have a method like `findAllAvailableAndNonDeleted()`
        // List<Car> availableCars = carService.getAll().stream()
        //         .filter(car -> car.isAvailable() && !car.isDeleted()) // This filtering logic is better in the service/repository
        //         .collect(Collectors.toList());
        List<Car> availableCars = carService.findAllAvailableAndNonDeleted(); // Assuming this method exists

        if (availableCars.isEmpty()) {
            log.info("Requester [{}]: No cars are currently available.", requesterId);
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(availableCars);
        log.info("Requester [{}]: Successfully retrieved {} available cars.", requesterId, carDTOs.size());
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Retrieves a list of available cars filtered by a specific price group.
     * This endpoint is publicly accessible.
     *
     * @param group The {@link PriceGroup} to filter by.
     * @return A ResponseEntity containing a list of available {@link CarResponseDTO}s matching the price group. Returns 204 No Content if none match.
     */
    @GetMapping("/available/price-group/{group}")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsByPriceGroup(@PathVariable PriceGroup group) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get available cars by price group: {}", requesterId, group);

        // Assuming carService.getAvailableCarsByPrice(group) correctly fetches available & non-deleted cars for the group
        List<Car> cars = carService.findAllAvailableByPriceGroup(group);
        if (cars.isEmpty()) {
            log.info("Requester [{}]: No available cars found for price group: {}", requesterId, group);
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);
        log.info("Requester [{}]: Successfully retrieved {} available cars for price group: {}", requesterId, carDTOs.size(), group);
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Retrieves a specific car by its UUID.
     * This endpoint is publicly accessible.
     *
     * @param carUuid The UUID of the car to retrieve. (Changed from carId to carUuid for clarity)
     * @return A ResponseEntity containing the {@link CarResponseDTO} if found, or 404 Not Found.
     */
    @GetMapping("/{carUuid}") // Path variable name matches parameter name
    public ResponseEntity<CarResponseDTO> getCarByUuid(@PathVariable UUID carUuid) { // Parameter name changed to carUuid
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get car by UUID: {}", requesterId, carUuid);

        // Assuming carService.read(uuid) or carService.readByUuid(uuid) exists
        // and service will throw ResourceNotFoundException if not found.
        Car car = carService.read(carUuid); // Changed from readByUuid to align with other services' read(UUID) pattern
        // and assume it throws ResourceNotFoundException
        if (car == null) { // This check is redundant if service.read(uuid) throws.
            log.warn("Requester [{}]: Car not found with UUID: {}", requesterId, carUuid);
            // throw new ResourceNotFoundException("Car not found with UUID: " + carUuid); // Prefer service to throw
            return ResponseEntity.notFound().build();
        }

        log.info("Requester [{}]: Successfully retrieved car with ID: {} for UUID: {}",
                requesterId, car.getId(), car.getUuid());
        return ResponseEntity.ok(CarMapper.toDto(car));
    }
}