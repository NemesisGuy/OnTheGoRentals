package za.ac.cput.controllers; // Assuming this is your public CarController

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.enums.PriceGroup; // Your PriceGroup enum
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.exception.BadRequestException; // Custom exception for bad input
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.ICarService;
import za.ac.cput.utils.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale; // For toUpperCase with locale
import java.util.UUID;

/**
 * CarController.java
 * Controller for public access to car information.
 * Provides endpoints to list all cars, filter by price group,
 * list available cars, and retrieve details of a specific car by UUID.
 *
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private static final Logger log = LoggerFactory.getLogger(CarController.class);
    private final ICarService carService;

    @Autowired
    public CarController(ICarService carService) {
        this.carService = carService;
        log.info("CarController initialized.");
    }

    /**
     * Retrieves a list of all cars currently marked as available for rental.
     * Handles a special path variable "all" to signify no price group filtering.
     *
     * @return A ResponseEntity containing a list of available {@link CarResponseDTO}s.
     */
    @GetMapping("/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCars() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all available cars.", requesterId);

        List<Car> availableCars = carService.findAllAvailableAndNonDeleted();

        if (availableCars.isEmpty()) {
            log.info("Requester [{}]: No cars are currently available.", requesterId);
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(availableCars);
        log.info("Requester [{}]: Successfully retrieved {} available cars.", requesterId, carDTOs.size());
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Retrieves a list of available cars filtered by a specific price group string.
     * The price group string is converted to the PriceGroup enum case-insensitively.
     *
     * @param groupString The price group string from the URL path (e.g., "luxury", "ECONOMY", "special").
     * @return A ResponseEntity containing a list of available {@link CarResponseDTO}s matching the price group.
     * @throws BadRequestException if the provided groupString is not a valid PriceGroup.
     */
    @GetMapping("/available/price-group/{groupString}")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsByPriceGroup(@PathVariable String groupString) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get available cars by price group string: '{}'", requesterId, groupString);

        PriceGroup priceGroupEnum;
        try {
            // Convert string to enum, case-insensitively
            priceGroupEnum = PriceGroup.valueOf(groupString.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.warn("Requester [{}]: Invalid price group string provided: '{}'. Valid values are: {}",
                    requesterId, groupString, java.util.Arrays.toString(PriceGroup.values()));
            throw new BadRequestException("Invalid price group value: '" + groupString + "'. Please use a valid price group.");
        }

        List<Car> cars = carService.getAvailableCarsByPrice(priceGroupEnum); // Assumes service method expects Enum
        if (cars.isEmpty()) {
            log.info("Requester [{}]: No available cars found for price group: {}", requesterId, priceGroupEnum);
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);
        log.info("Requester [{}]: Successfully retrieved {} available cars for price group: {}", requesterId, carDTOs.size(), priceGroupEnum);
        return ResponseEntity.ok(carDTOs);
    }

    // --- Other CarController methods like getAllCars, getCarByUuid etc. ---
    // These would remain as they were if they don't involve PriceGroup path variable conversion.

    /**
     * Retrieves a list of all cars, optionally filtered by a specific price group.
     * If no group is specified or "all" is provided, all cars are returned.
     *
     * @param groupString Optional price group string from the URL path.
     * @return A ResponseEntity containing a list of {@link CarResponseDTO}s.
     */
    @GetMapping(value = {"/price-group", "/price-group/{groupString}"}) // Handles both with and without group
    public ResponseEntity<List<CarResponseDTO>> getAllCarsByPriceGroupOptional(
            @PathVariable(required = false) String groupString) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        List<Car> cars;

        if (groupString == null || groupString.trim().isEmpty() || "all".equalsIgnoreCase(groupString.trim())) {
            log.info("Requester [{}]: Request to get all cars (no price group filter or 'all' specified).", requesterId);
            cars = carService.getAll(); // Fetches all non-deleted cars
        } else {
            log.info("Requester [{}]: Request to get cars by price group string: '{}'", requesterId, groupString);
            PriceGroup priceGroupEnum;
            try {
                priceGroupEnum = PriceGroup.valueOf(groupString.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                log.warn("Requester [{}]: Invalid price group string provided: '{}'. Valid values are: {}",
                        requesterId, groupString, java.util.Arrays.toString(PriceGroup.values()));
                throw new BadRequestException("Invalid price group value: '" + groupString + "'.");
            }
            cars = carService.getCarsByPriceGroup(priceGroupEnum);
        }

        if (cars.isEmpty()) {
            log.info("Requester [{}]: No cars found for the specified criteria (Price Group String: {}).",
                    requesterId, groupString != null ? groupString : "None");
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);
        log.info("Requester [{}]: Successfully retrieved {} cars (Price Group String: {}).",
                requesterId, carDTOs.size(), groupString != null ? groupString : "All");
        return ResponseEntity.ok(carDTOs);
    }


    @GetMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> getCarByUuid(@PathVariable UUID carUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get car by UUID: {}", requesterId, carUuid);
        Car car = carService.read(carUuid);
        if (car == null) {
            log.warn("Requester [{}]: Car not found with UUID: {}", requesterId, carUuid);
            throw new ResourceNotFoundException("Car not found with UUID: " + carUuid);
        }
        log.info("Requester [{}]: Successfully retrieved car ID: {} for UUID: {}",
                requesterId, car.getId(), car.getUuid());
        return ResponseEntity.ok(CarMapper.toDto(car));
    }

    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all cars (unfiltered).", requesterId);
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            log.info("Requester [{}]: No cars found in the system.", requesterId);
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);
        log.info("Requester [{}]: Successfully retrieved {} cars (unfiltered).", requesterId, carDTOs.size());
        return ResponseEntity.ok(carDTOs);
    }
}