package za.ac.cput.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.RentalFromBookingRequestDTO;
import za.ac.cput.domain.dto.request.RentalRequestDTO;
import za.ac.cput.domain.dto.request.RentalUpdateDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.service.*;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * RentalController.java
 * Controller for managing Rental operations.
 * Handles creating rentals directly or from bookings, and viewing/managing rental status.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/rentals")
@Tag(name = "Rental Management", description = "Endpoints for creating and managing car rentals.")
@SecurityRequirement(name = "bearerAuth")
public class RentalController {

    private static final Logger log = LoggerFactory.getLogger(RentalController.class);

    private final IRentalService rentalService;
    private final IUserService userService;
    private final ICarService carService;
    private final IDriverService driverService;
    private final IFileStorageService fileStorageService;
    private final String publicApiUrl;

    /**
     * Constructs a RentalController with necessary service dependencies.
     *
     * @param rentalService      The rental service for rental logic.
     * @param userService        The user service for user lookups.
     * @param carService         The car service for car lookups.
     * @param driverService      The driver service for driver lookups.
     * @param fileStorageService The service for generating image URLs.
     *
     */
    @Autowired
    public RentalController(IRentalService rentalService, IUserService userService,
                            ICarService carService, IDriverService driverService, IFileStorageService fileStorageService, @Value("${app.public-api-url}") String publicApiUrl // <-- Inject the property
    ) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.carService = carService;
        this.driverService = driverService;
        this.fileStorageService = fileStorageService;
        this.publicApiUrl = publicApiUrl; // Initialize the public API URL
        log.info("RentalController initialized.");
    }

    /**
     * Creates a new rental directly (walk-in rental).
     *
     * @param rentalRequestDTO The DTO containing details for the new rental.
     * @return A ResponseEntity containing the created rental DTO.
     */
    @Operation(summary = "Create a new direct rental", description = "Creates a new rental for the currently authenticated user without a prior booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental created successfully", content = @Content(schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User, Car, or Driver not found"),
            @ApiResponse(responseCode = "409", description = "Car is not available")
    })
    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(@Valid @RequestBody RentalRequestDTO rentalRequestDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new direct rental with DTO: {}", requesterId, rentalRequestDTO);

        User currentUser = userService.read(requesterId);
        Car carEntity = carService.read(rentalRequestDTO.getCarUuid());

        if (!carEntity.isAvailable()) {
            throw new CarNotAvailableException("Car with UUID: " + carEntity.getUuid() + " is not available.");
        }

        Driver driverEntity = null;
        if (rentalRequestDTO.getDriverUuid() != null) {
            driverEntity = driverService.read(rentalRequestDTO.getDriverUuid());
        }

        Rental rentalToCreate = RentalMapper.toEntity(rentalRequestDTO, currentUser, carEntity, driverEntity);
        Rental createdRentalEntity = rentalService.create(rentalToCreate);

        return ResponseEntity.status(HttpStatus.CREATED).body(RentalMapper.toDto(createdRentalEntity, fileStorageService, publicApiUrl));
    }

    /**
     * Retrieves a specific rental by its UUID.
     *
     * @param rentalUuid The UUID of the rental to retrieve.
     * @return A ResponseEntity containing the rental DTO.
     */
    @Operation(summary = "Get rental by UUID", description = "Retrieves a specific rental by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental found", content = @Content(schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User forbidden from accessing this rental"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    @GetMapping("/{rentalUuid}")
    public ResponseEntity<RentalResponseDTO> getRentalByUuid(
            @Parameter(description = "UUID of the rental to retrieve", required = true) @PathVariable UUID rentalUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get rental by UUID: {}", requesterId, rentalUuid);

        Rental rentalEntity = rentalService.read(rentalUuid);
        if (!rentalEntity.getUser().getEmail().equals(requesterId) /* && !isRequesterAdminOrStaff() */) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(RentalMapper.toDto(rentalEntity, fileStorageService, publicApiUrl));
    }

    /**
     * Retrieves all rentals for the currently authenticated user.
     *
     * @return A ResponseEntity containing a list of the user's rentals.
     */
    @Operation(summary = "Get current user's rentals", description = "Retrieves all rentals associated with the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved rentals"),
            @ApiResponse(responseCode = "204", description = "No rentals found for this user")
    })
    @GetMapping("/my-rentals")
    public ResponseEntity<List<RentalResponseDTO>> getCurrentUserRentals() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get their rentals.", requesterId);

        User currentUser = userService.read(requesterId);
        List<Rental> userRentals = rentalService.getRentalHistoryByUser(currentUser);

        if (userRentals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(userRentals, fileStorageService, publicApiUrl));
    }

    /**
     * Updates an existing rental. This is typically an administrative action.
     *
     * @param rentalUuid      The UUID of the rental to update.
     * @param rentalUpdateDTO The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated rental DTO.
     */
    @Operation(summary = "Update an existing rental (Admin)", description = "Updates details of an existing rental. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental updated successfully", content = @Content(schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rental, new Car, or new Driver not found")
    })
    public ResponseEntity<RentalResponseDTO> updateRentalByAdmin(
            @PathVariable UUID rentalUuid,
            @Valid @RequestBody RentalUpdateDTO rentalUpdateDTO // Using the simpler RentalUpdateDTO
    ) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to update rental UUID: {} with RentalUpdateDTO: {}", adminId, rentalUuid, rentalUpdateDTO);

        Rental existingRental = rentalService.read(rentalUuid);
        log.debug("Admin [{}]: Found existing rental (ID: {}, UUID: {}) for update. Current status: {}",
                adminId, existingRental.getId(), existingRental.getUuid(), existingRental.getStatus());

        Rental.Builder rentalBuilder = new Rental.Builder().copy(existingRental);
        boolean changed = false;

// Update car if new carUuid is provided and different
        if (rentalUpdateDTO.getCarUuid() != null &&
                (existingRental.getCar() == null || !rentalUpdateDTO.getCarUuid().equals(existingRental.getCar().getUuid()))) { // Handle if existing car is null
            log.debug("Admin [{}]: Rental update - Car change requested. Current Car UUID: {}, New Car UUID: {}",
                    adminId, existingRental.getCar() != null ? existingRental.getCar().getUuid() : "null", rentalUpdateDTO.getCarUuid());
            Car newCarEntity = carService.read(rentalUpdateDTO.getCarUuid());
            if (!newCarEntity.isAvailable()) {
                log.warn("Admin [{}]: Rental update - New Car UUID: {} is not available.", adminId, rentalUpdateDTO.getCarUuid());
                throw new CarNotAvailableException("The newly selected car (UUID: " + rentalUpdateDTO.getCarUuid() + ") is not available.");
            }
            if (existingRental.getCar() != null && !existingRental.getCar().isAvailable() &&
                    (existingRental.getStatus() == RentalStatus.ACTIVE)) {
                Car oldCarToMakeAvailable = new Car.Builder().copy(existingRental.getCar()).setAvailable(true).build();
                carService.update(oldCarToMakeAvailable);
                log.info("Admin [{}]: Rental update - Old car ID {} made available due to car change.", adminId, oldCarToMakeAvailable.getId());
            }
            rentalBuilder.setCar(newCarEntity);
            changed = true;
            log.debug("Admin [{}]: Rental update - New Car (ID: {}) assigned.", adminId, newCarEntity.getId());
        }

// Update driver
        boolean updateDriverField = rentalUpdateDTO.getDriverUuid() != null ||
                (existingRental.getDriver() != null && rentalUpdateDTO.getDriverUuid() == null);
// ^^^ Added check for explicit null from DTO to remove driver.
// This requires isDriverUuidPresentInJson() method in RentalUpdateDTO or using Optional.
// Simplified logic for now:
        if (rentalUpdateDTO.getDriverUuid() != null) { // If DTO provides a non-null driver UUID
            if (existingRental.getDriver() == null || !rentalUpdateDTO.getDriverUuid().equals(existingRental.getDriver().getUuid())) {
                log.debug("Admin [{}]: Rental update - Driver change/assignment. New Driver UUID: {}", adminId, rentalUpdateDTO.getDriverUuid());
                Driver newDriverEntity = driverService.read(rentalUpdateDTO.getDriverUuid());
                rentalBuilder.setDriver(newDriverEntity);
                changed = true;
                log.debug("Admin [{}]: Rental update - New Driver (ID: {}) assigned.", adminId, newDriverEntity.getId());
            }
        } else { // updateDto.getDriverUuid() is null
// If client sends "driverUuid": null, this means remove.
// If client omits driverUuid key, updateDto.getDriverUuid() is null - check if existing had one.
            if (existingRental.getDriver() != null) { // Check if there was a driver to remove
// To reliably distinguish "not provided" from "explicitly null", DTO needs Optional<UUID> or boolean flag.
// Assuming if DTO.driverUuid is null here, and there was an existing driver, the intent is removal.
// This depends on Jackson's config: does it set field to null if key absent, or only if value is null?
// Let's assume Jackson sets it to null if value is null, or if key is absent and it's an object type.
                log.debug("Admin [{}]: Rental update - Driver removal possibly intended (driverUuid is null in DTO). Existing driver ID: {}", adminId, existingRental.getDriver().getId());
                rentalBuilder.setDriver(null);
                changed = true;
            }
        }

// Update expected return date if provided and different
        if (rentalUpdateDTO.getExpectedReturnDate() != null &&
                !rentalUpdateDTO.getExpectedReturnDate().equals(existingRental.getExpectedReturnDate())) {
            log.debug("Admin [{}]: Rental update - ExpectedReturnedDate change from {} to {}",
                    adminId, existingRental.getExpectedReturnDate(), rentalUpdateDTO.getExpectedReturnDate());
            rentalBuilder.setExpectedReturnDate(rentalUpdateDTO.getExpectedReturnDate());
            changed = true;
        }

// ** ADDED STATUS UPDATE LOGIC **
        if (rentalUpdateDTO.getStatus() != null) {
            try {
                RentalStatus newStatusEnum = RentalStatus.valueOf(rentalUpdateDTO.getStatus().trim().toUpperCase());
                if (existingRental.getStatus() != newStatusEnum) {
                    log.debug("Admin [{}]: Rental update - Status change from {} to {}",
                            adminId, existingRental.getStatus(), newStatusEnum);
                    rentalBuilder.setStatus(newStatusEnum);
                    changed = true;
                }
            } catch (IllegalArgumentException e) {
                log.warn("Admin [{}]: Invalid status string '{}' provided in update DTO for rental UUID: {}. Status not changed.",
                        adminId, rentalUpdateDTO.getStatus(), rentalUuid);
// Optionally throw a BadRequestException here or let it proceed without status change.
            }
        }

        if (!changed) {
            log.info("Admin [{}]: No updatable fields provided in RentalUpdateDTO or values are the same for rental UUID: {}. No update performed.", adminId, rentalUuid);
            return ResponseEntity.ok(RentalMapper.toDto(existingRental, fileStorageService, publicApiUrl));
        }

        Rental rentalWithUpdates = rentalBuilder.build();
        log.debug("Admin [{}]: Rental entity built with updates: {}", adminId, rentalWithUpdates);

        Rental persistedRental = rentalService.update(rentalWithUpdates);

        log.info("Admin [{}]: Successfully updated rental ID: {}, UUID: {}", adminId, persistedRental.getId(), persistedRental.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(persistedRental, fileStorageService, publicApiUrl));
    }

    /**
     * Marks a rental as completed. This is typically an administrative action.
     *
     * @param rentalUuid The UUID of the rental to complete.
     * @param fineAmount The amount of any fine to apply (optional).
     * @return A ResponseEntity containing the completed rental DTO.
     */
    @Operation(summary = "Complete a rental (Admin)", description = "Marks a rental as completed and returns the car to an available state. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental completed successfully", content = @Content(schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    @PostMapping("/{rentalUuid}/complete")
    public ResponseEntity<RentalResponseDTO> completeRental(
            @Parameter(description = "UUID of the rental to complete", required = true) @PathVariable UUID rentalUuid,
            @Parameter(description = "Amount of any fine to be applied (optional, defaults to 0.0)") @RequestParam(required = false, defaultValue = "0.0") double fineAmount) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to complete rental UUID: {}, Fine amount: {}", requesterId, rentalUuid, fineAmount);

        Rental completedRental = rentalService.completeRentalByUuid(rentalUuid, fineAmount);
        return ResponseEntity.ok(RentalMapper.toDto(completedRental, fileStorageService, publicApiUrl));
    }

    /**
     * Creates a new rental from a previously confirmed booking. This is a key transactional endpoint.
     *
     * @param bookingUuid The UUID of the confirmed booking to convert.
     * @param dto         The DTO containing any additional details required at pickup.
     * @return A ResponseEntity containing the newly created rental DTO.
     */
    @Operation(summary = "Create rental from booking", description = "Converts a confirmed booking into an active rental.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental created successfully from booking", content = @Content(schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Booking, User, or Driver not found"),
            @ApiResponse(responseCode = "409", description = "Booking is not in a 'CONFIRMED' state")
    })
    @PostMapping("/from-booking/{bookingUuid}")
    public ResponseEntity<RentalResponseDTO> createRentalFromBooking(
            @Parameter(description = "UUID of the confirmed Booking to convert", required = true) @PathVariable UUID bookingUuid,
            @Valid @RequestBody RentalFromBookingRequestDTO dto) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to create rental from Booking UUID: {}", requesterId, bookingUuid);

        Rental createdRental = rentalService.createRentalFromBooking(
                bookingUuid,
                dto.getIssuerId(),
                dto.getDriverUuid(),
                dto.getActualPickupTime()
        );
        return new ResponseEntity<>(RentalMapper.toDto(createdRental, fileStorageService, publicApiUrl ), HttpStatus.CREATED);
    }
}