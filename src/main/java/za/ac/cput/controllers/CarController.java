package za.ac.cput.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.service.ICarService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * CarController.java
 * Controller for public access to car information.
 * Provides endpoints to list all cars, filter by price group,
 * list available cars, and retrieve details of a specific car by UUID.
 * DTO conversion, including image URL generation, is handled by the {@link CarMapper}.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private static final Logger log = LoggerFactory.getLogger(CarController.class);
    private final ICarService carService;

    /**
     * Constructs the CarController with the necessary car service.
     *
     * @param carService The service for car data operations.
     */
    @Autowired
    public CarController(ICarService carService) {
        this.carService = carService;
        log.info("CarController initialized.");
    }

    /**
     * NEW: A dedicated, unambiguous endpoint for fetching available cars.
     * The path "/list/available" cannot be confused with "/{uuid}".
     */
    @GetMapping("/list/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsAdmin() {
        log.info("Admin request to get all available cars.");
        List<Car> availableCars = carService.findAllAvailableAndNonDeleted();
        if (availableCars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(availableCars));
    }


    /**
     * Retrieves a list of all cars currently marked as available.
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

        // Use the mapper to convert the list of entities to DTOs
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(availableCars);

        log.info("Requester [{}]: Successfully retrieved {} available cars.", requesterId, carDTOs.size());
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Retrieves a list of available cars filtered by a specific price group string.
     *
     * @param groupString The price group string (e.g., "luxury", "ECONOMY").
     * @return A ResponseEntity containing a list of available {@link CarResponseDTO}s.
     */
    @GetMapping("/available/price-group/{groupString}")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsByPriceGroup(@PathVariable String groupString) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get available cars by price group: '{}'", requesterId, groupString);
        PriceGroup priceGroupEnum = parsePriceGroup(groupString, requesterId);

        List<Car> cars = carService.getAvailableCarsByPrice(priceGroupEnum);
        if (cars.isEmpty()) {
            log.info("Requester [{}]: No available cars found for price group: {}", requesterId, priceGroupEnum);
            return ResponseEntity.noContent().build();
        }

        // Use the mapper to convert the list of entities to DTOs
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);

        log.info("Requester [{}]: Successfully retrieved {} available cars for price group: {}", requesterId, carDTOs.size(), priceGroupEnum);
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Retrieves a list of all cars, optionally filtered by price group.
     *
     * @param groupString Optional price group string.
     * @return A ResponseEntity containing a list of {@link CarResponseDTO}s.
     */
    @GetMapping(value = {"/price-group", "/price-group/{groupString}"})
    public ResponseEntity<List<CarResponseDTO>> getAllCarsByPriceGroupOptional(
            @PathVariable(required = false) String groupString) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        List<Car> cars;

        if (groupString == null || groupString.trim().isEmpty() || "all".equalsIgnoreCase(groupString.trim())) {
            log.info("Requester [{}]: Request to get all cars (no filter).", requesterId);
            cars = carService.getAll();
        } else {
            log.info("Requester [{}]: Request to get cars by price group: '{}'", requesterId, groupString);
            PriceGroup priceGroupEnum = parsePriceGroup(groupString, requesterId);
            cars = carService.getCarsByPriceGroup(priceGroupEnum);
        }

        if (cars.isEmpty()) {
            log.info("Requester [{}]: No cars found for the specified criteria.", requesterId);
            return ResponseEntity.noContent().build();
        }

        // Use the mapper to convert the list of entities to DTOs
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);

        log.info("Requester [{}]: Successfully retrieved {} cars.", requesterId, carDTOs.size());
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Retrieves a specific car by its UUID.
     *
     * @param carUuid The UUID of the car to retrieve.
     * @return A ResponseEntity containing the {@link CarResponseDTO}.
     */
    @GetMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> getCarByUuid(@PathVariable UUID carUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get car by UUID: {}", requesterId, carUuid);

        // The service throws ResourceNotFoundException if not found, which is handled globally.
        Car car = carService.read(carUuid);

        log.info("Requester [{}]: Successfully retrieved car ID: {}", requesterId, car.getId());

        // Use the mapper to convert the single entity to a DTO
        return ResponseEntity.ok(CarMapper.toDto(car));
    }

    /**
     * Retrieves a list of all cars in the system.
     *
     * @return A ResponseEntity containing a list of all {@link CarResponseDTO}s.
     */
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all cars (unfiltered).", requesterId);
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            log.info("Requester [{}]: No cars found in the system.", requesterId);
            return ResponseEntity.noContent().build();
        }

        // Use the mapper to convert the list of entities to DTOs
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars);

        log.info("Requester [{}]: Successfully retrieved {} cars.", requesterId, carDTOs.size());
        return ResponseEntity.ok(carDTOs);
    }

    /**
     * Helper method to parse a string into a PriceGroup enum.
     *
     * @param groupString The string to parse.
     * @param requesterId The identifier of the requester for logging.
     * @return The corresponding {@link PriceGroup} enum.
     * @throws BadRequestException if the string is not a valid price group.
     */
    private PriceGroup parsePriceGroup(String groupString, String requesterId) {
        try {
            return PriceGroup.valueOf(groupString.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.warn("Requester [{}]: Invalid price group string provided: '{}'.", requesterId, groupString);
            throw new BadRequestException("Invalid price group value: '" + groupString + "'.");
        }
    }

    // The buildDtoWithImageUrl helper method has been removed.
}