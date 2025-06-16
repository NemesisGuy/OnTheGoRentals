package za.ac.cput.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import za.ac.cput.service.IFileStorageService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * CarController.java
 * Controller for public access to car information.
 * Provides endpoints to list all cars, list available cars, filter by price group,
 * and retrieve details of a specific car by UUID.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/cars")
@Tag(name = "Public Car Information", description = "Endpoints for public users to browse and view car inventory.")
public class CarController {

    private static final Logger log = LoggerFactory.getLogger(CarController.class);
    private final ICarService carService;
    private final IFileStorageService fileStorageService;

    /**
     * Constructs the CarController with the necessary services.
     *
     * @param carService         The service for car data operations.
     * @param fileStorageService The service for generating image URLs.
     */
    @Autowired
    public CarController(ICarService carService, IFileStorageService fileStorageService) {
        this.carService = carService;
        this.fileStorageService = fileStorageService;
        log.info("CarController initialized.");
    }

    /**
     * Retrieves a list of all cars in the system.
     *
     * @return A ResponseEntity containing a list of all car DTOs.
     */
    @Operation(summary = "Get all cars", description = "Retrieves a list of all cars in the system, including unavailable ones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved car list"),
            @ApiResponse(responseCode = "204", description = "No cars found in the system")
    })
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all cars (unfiltered).", requesterId);
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(cars, fileStorageService));
    }

    /**
     * Retrieves a list of all cars currently marked as available for booking.
     *
     * @return A ResponseEntity containing a list of available car DTOs.
     */
    @Operation(summary = "Get available cars", description = "Retrieves a list of all cars currently marked as available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved available cars"),
            @ApiResponse(responseCode = "204", description = "No cars are currently available")
    })
    @GetMapping("/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCars() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all available cars.", requesterId);

        List<Car> availableCars = carService.findAllAvailableAndNonDeleted();
        if (availableCars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(availableCars, fileStorageService));
    }

    /**
     * Retrieves a list of available cars filtered by a specific price group.
     *
     * @param groupString The price group string (e.g., "LUXURY", "economy"). Case-insensitive.
     * @return A ResponseEntity containing a list of matching available car DTOs.
     */
    @Operation(summary = "Get available cars by price group", description = "Retrieves available cars filtered by a specific price group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cars for the price group"),
            @ApiResponse(responseCode = "204", description = "No available cars found for the specified price group"),
            @ApiResponse(responseCode = "400", description = "Invalid price group provided")
    })
    @GetMapping("/available/price-group/{groupString}")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsByPriceGroup(
            @Parameter(description = "Price group string (e.g., 'LUXURY', 'ECONOMY'). Case-insensitive.", required = true) @PathVariable String groupString) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get available cars by price group: '{}'", requesterId, groupString);
        PriceGroup priceGroupEnum = parsePriceGroup(groupString, requesterId);

        List<Car> cars = carService.getAvailableCarsByPrice(priceGroupEnum);
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(cars, fileStorageService));
    }

    /**
     * Retrieves a specific car by its UUID.
     *
     * @param carUuid The UUID of the car to retrieve.
     * @return A ResponseEntity containing the car's DTO.
     */
    @Operation(summary = "Get car by UUID", description = "Retrieves a specific car by its unique identifier (UUID).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the car"),
            @ApiResponse(responseCode = "404", description = "Car with the specified UUID was not found")
    })
    @GetMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> getCarByUuid(
            @Parameter(description = "UUID of the car to retrieve", required = true) @PathVariable UUID carUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get car by UUID: {}", requesterId, carUuid);

        Car car = carService.read(carUuid); // Throws ResourceNotFoundException if not found
        return ResponseEntity.ok(CarMapper.toDto(car, fileStorageService));
    }

    /**
     * Helper method to parse a string into a PriceGroup enum, handling case-insensitivity and errors.
     *
     * @param groupString The string to parse.
     * @param requesterId The identifier of the requester for logging purposes.
     * @return The corresponding PriceGroup enum.
     * @throws BadRequestException if the string does not match any valid price group.
     */
    private PriceGroup parsePriceGroup(String groupString, String requesterId) {
        try {
            return PriceGroup.valueOf(groupString.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.warn("Requester [{}]: Invalid price group string provided: '{}'.", requesterId, groupString);
            throw new BadRequestException("Invalid price group value: '" + groupString + "'.");
        }
    }
}