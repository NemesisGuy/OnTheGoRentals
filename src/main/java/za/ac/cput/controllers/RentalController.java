package za.ac.cput.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.dto.request.RentalRequestDTO;
import za.ac.cput.domain.dto.request.RentalUpdateDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.ResourceNotFoundException; // For consistency
import za.ac.cput.exception.CarNotAvailableException;  // For specific error
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IDriverService;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService;
import za.ac.cput.utils.SecurityUtils; // Import your helper

import java.util.List;
import java.util.UUID;

/**
 * RentalController.java
 * Controller for managing Rental operations.
 * Primarily handles operations for the currently authenticated user, such as creating
 * rentals, viewing their own rentals, and performing actions like confirming or canceling.
 * Includes endpoints that might be used by staff/admins (e.g., completing a rental).
 *
 * Author: Peter Buckingham (220165289)
 * Date: 10 April 2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/rentals")
// @CrossOrigin(...) // Prefer global CORS configuration
public class RentalController {

    private static final Logger log = LoggerFactory.getLogger(RentalController.class);

    private final IRentalService rentalService;
    private final IUserService userService;
    private final ICarService carService;
    private final IDriverService driverService;

    /**
     * Constructs a RentalController with necessary service dependencies.
     *
     * @param rentalService The rental service.
     * @param userService   The user service.
     * @param carService    The car service.
     * @param driverService The driver service (for associating drivers with rentals).
     */
    @Autowired
    public RentalController(IRentalService rentalService, IUserService userService,
                            ICarService carService, IDriverService driverService) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.carService = carService;
        this.driverService = driverService;
        log.info("RentalController initialized.");
    }

    /**
     * Creates a new rental for the currently authenticated user.
     *
     * @param rentalRequestDTO The {@link RentalRequestDTO} containing details for the new rental.
     * @return A ResponseEntity containing the created {@link RentalResponseDTO} and HTTP status 201 Created.
     * @throws ResourceNotFoundException if the specified User, Car, or Driver (if provided) is not found.
     * @throws CarNotAvailableException if the specified car is not available.
     */
    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(@Valid @RequestBody RentalRequestDTO rentalRequestDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new rental with DTO: {}", requesterId, rentalRequestDTO);

        User currentUser = userService.read(requesterId); // Fetches user by email (requesterId)
        if (currentUser == null) {
            log.warn("Requester [{}]: User profile not found for authenticated user. Cannot create rental.", requesterId);
            // This should ideally be caught by Spring Security earlier for authenticated endpoints.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        log.debug("Requester [{}]: Authenticated user found: ID: {}, UUID: {}", requesterId, currentUser.getId(), currentUser.getUuid());

        log.debug("Requester [{}]: Fetching car with UUID: {}", requesterId, rentalRequestDTO.getCarUuid());
        Car carEntity = carService.read(rentalRequestDTO.getCarUuid());
        if (carEntity == null) {
            log.warn("Requester [{}]: Car not found with UUID: {}", requesterId, rentalRequestDTO.getCarUuid());
            throw new ResourceNotFoundException("Car not found with UUID: " + rentalRequestDTO.getCarUuid());
        }
        if (!carEntity.isAvailable()) { // Ensure car availability check
            log.warn("Requester [{}]: Car UUID: {} is not available for rental.", requesterId, carEntity.getUuid());
            throw new CarNotAvailableException("Car with UUID: " + carEntity.getUuid() + " is not available.");
        }
        log.debug("Requester [{}]: Car found for rental: ID: {}, UUID: {}", requesterId, carEntity.getId(), carEntity.getUuid());


        Driver driverEntity = null;
        if (rentalRequestDTO.getDriverUuid() != null) {
            log.debug("Requester [{}]: Fetching driver with UUID: {}", requesterId, rentalRequestDTO.getDriverUuid());
            driverEntity = driverService.read(rentalRequestDTO.getDriverUuid());
            if (driverEntity == null) {
                log.warn("Requester [{}]: Driver not found with UUID: {}", requesterId, rentalRequestDTO.getDriverUuid());
                // Decide if this is a hard stop or if rental can proceed without driver if DTO had a stale UUID
                throw new ResourceNotFoundException("Driver not found with UUID: " + rentalRequestDTO.getDriverUuid());
            }
            log.debug("Requester [{}]: Driver found for rental: ID: {}, UUID: {}", requesterId, driverEntity.getId(), driverEntity.getUuid());
        } else {
            log.debug("Requester [{}]: No driver UUID provided for rental.", requesterId);
        }

        Rental rentalToCreate = RentalMapper.toEntity(rentalRequestDTO, currentUser, carEntity, driverEntity);
        log.debug("Requester [{}]: Mapped DTO to Rental entity for creation: {}", requesterId, rentalToCreate);

        Rental createdRentalEntity = rentalService.create(rentalToCreate);
        log.info("Requester [{}]: Successfully created rental with ID: {} and UUID: {}",
                requesterId, createdRentalEntity.getId(), createdRentalEntity.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(RentalMapper.toDto(createdRentalEntity));
    }

    /**
     * Retrieves a specific rental by its UUID.
     * Access control (e.g., ensuring the requester owns the rental or is an admin/staff)
     * should be handled within this method or by the service layer.
     *
     * @param rentalUuid The UUID of the rental to retrieve.
     * @return A ResponseEntity containing the {@link RentalResponseDTO} if found.
     * @throws ResourceNotFoundException if the rental with the given UUID is not found.
     */
    @GetMapping("/{rentalUuid}")
    public ResponseEntity<RentalResponseDTO> getRentalByUuid(@PathVariable UUID rentalUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get rental by UUID: {}", requesterId, rentalUuid);

        Rental rentalEntity = rentalService.read(rentalUuid);
        // rentalService.read(UUID) should throw ResourceNotFoundException if not found.

        // TODO: Implement robust authorization check.
        // Example: Ensure the current user owns this rental or has admin/staff role.
        if (!requesterId.equals("GUEST") && !rentalEntity.getUser().getEmail().equals(requesterId) /* && !isRequesterAdminOrStaff() */) {
            log.warn("Requester [{}]: Attempted to access rental UUID: {} which they do not own or have permission for.", requesterId, rentalUuid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Or .notFound() to not reveal existence
        }

        log.info("Requester [{}]: Successfully retrieved rental with ID: {} for UUID: {}",
                requesterId, rentalEntity.getId(), rentalEntity.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(rentalEntity));
    }

    /**
     * Retrieves all rentals for the currently authenticated user.
     *
     * @return A ResponseEntity containing a list of {@link RentalResponseDTO}s, or 204 No Content if none exist.
     */
    @GetMapping("/my-rentals")
    public ResponseEntity<List<RentalResponseDTO>> getCurrentUserRentals() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get their rentals.", requesterId);

        if ("GUEST".equals(requesterId)) {
            log.warn("Requester [GUEST]: Attempted to access /my-rentals. Endpoint requires authentication.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userService.read(requesterId);
        if (currentUser == null) {
            log.warn("Requester [{}]: User profile not found. Cannot retrieve rentals.", requesterId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Rental> userRentals = rentalService.getRentalHistoryByUser(currentUser);
        if (userRentals.isEmpty()) {
            log.info("Requester [{}]: No rentals found for this user.", requesterId);
            return ResponseEntity.noContent().build();
        }
        log.info("Requester [{}]: Successfully retrieved {} rentals for this user.", requesterId, userRentals.size());
        return ResponseEntity.ok(RentalMapper.toDtoList(userRentals));
    }

    /**
     * Updates an existing rental.
     * Typically, only certain fields are updatable by the user who owns the rental.
     * Access control is crucial here.
     *
     * @param rentalUuid        The UUID of the rental to update.
     * @param rentalUpdateDTO   The {@link RentalUpdateDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link RentalResponseDTO}.
     * @throws ResourceNotFoundException if the rental or related entities (if changed) are not found.
     */
    @PutMapping("/{rentalUuid}")
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
                    (existingRental.getStatus() == RentalStatus.ACTIVE ||
                            existingRental.getStatus() == RentalStatus.CONFIRMED ||
                            existingRental.getStatus() == RentalStatus.IN_PROGRESS)) {
                Car oldCarToMakeAvailable = Car.builder().copy(existingRental.getCar()).available(true).build();
                carService.update(oldCarToMakeAvailable);
                log.info("Admin [{}]: Rental update - Old car ID {} made available due to car change.", adminId, oldCarToMakeAvailable.getId());
            }
            rentalBuilder.setCar(newCarEntity);
            changed = true;
            log.debug("Admin [{}]: Rental update - New Car (ID: {}) assigned.", adminId, newCarEntity.getId());
        }

        // Update driver
        boolean updateDriverField = rentalUpdateDTO.getDriverUuid() != null ||
                (existingRental.getDriver() != null && rentalUpdateDTO.getDriverUuid() == null );
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
            return ResponseEntity.ok(RentalMapper.toDto(existingRental));
        }

        Rental rentalWithUpdates = rentalBuilder.build();
        log.debug("Admin [{}]: Rental entity built with updates: {}", adminId, rentalWithUpdates);

        Rental persistedRental = rentalService.update(rentalWithUpdates);

        log.info("Admin [{}]: Successfully updated rental ID: {}, UUID: {}", adminId, persistedRental.getId(), persistedRental.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(persistedRental));
    }
    /**
     * Allows the authenticated user (or admin/staff) to confirm a rental.
     *
     * @param rentalUuid The UUID of the rental to confirm.
     * @return A ResponseEntity containing the {@link RentalResponseDTO} of the confirmed rental.
     */
    @PostMapping("/{rentalUuid}/confirm")
    public ResponseEntity<RentalResponseDTO> confirmRental(@PathVariable UUID rentalUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to confirm rental UUID: {}", requesterId, rentalUuid);

        // Service method should handle authorization (e.g., user owns rental or admin)
        // and business logic for confirmation.
        Rental rentalToConfirm = rentalService.read(rentalUuid); // Read first for logging and pre-check
        if (!requesterId.equals("GUEST") && !rentalToConfirm.getUser().getEmail().equals(requesterId) /* && !isRequesterAdminOrStaff() */) {
            log.warn("Requester [{}]: Attempted to confirm rental UUID: {} which they do not own/have permission for.", requesterId, rentalUuid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.debug("Requester [{}]: Found rental ID: {}, UUID: {} for confirmation.",
                requesterId, rentalToConfirm.getId(), rentalToConfirm.getUuid());

        Rental confirmedRental = rentalService.confirmRentalByUuid(rentalUuid);
        log.info("Requester [{}]: Successfully confirmed rental with ID: {} and UUID: {}",
                requesterId, confirmedRental.getId(), confirmedRental.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(confirmedRental));
    }

    /**
     * Allows the authenticated user (or admin/staff) to cancel a rental.
     *
     * @param rentalUuid The UUID of the rental to cancel.
     * @return A ResponseEntity containing the {@link RentalResponseDTO} of the cancelled rental.
     */
    @PostMapping("/{rentalUuid}/cancel")
    public ResponseEntity<RentalResponseDTO> cancelRental(@PathVariable UUID rentalUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to cancel rental UUID: {}", requesterId, rentalUuid);

        Rental rentalToCancel = rentalService.read(rentalUuid); // Read first for logging and pre-check
        if (!requesterId.equals("GUEST") && !rentalToCancel.getUser().getEmail().equals(requesterId) /* && !isRequesterAdminOrStaff() */) {
            log.warn("Requester [{}]: Attempted to cancel rental UUID: {} which they do not own/have permission for.", requesterId, rentalUuid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.debug("Requester [{}]: Found rental ID: {}, UUID: {} for cancellation.",
                requesterId, rentalToCancel.getId(), rentalToCancel.getUuid());

        Rental cancelledRental = rentalService.cancelRentalByUuid(rentalUuid);
        log.info("Requester [{}]: Successfully cancelled rental with ID: {} and UUID: {}",
                requesterId, cancelledRental.getId(), cancelledRental.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(cancelledRental));
    }

    /**
     * Allows admin/staff to mark a rental as completed, optionally applying a fine.
     * Access to this endpoint should be restricted (e.g., via @PreAuthorize).
     *
     * @param rentalUuid The UUID of the rental to complete.
     * @param fineAmount The amount of any fine to be applied (optional, defaults to 0.0).
     * @return A ResponseEntity containing the {@link RentalResponseDTO} of the completed rental.
     */
    @PostMapping("/{rentalUuid}/complete")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')") // Example security
    public ResponseEntity<RentalResponseDTO> completeRental(
            @PathVariable UUID rentalUuid,
            @RequestParam(required = false, defaultValue = "0.0") double fineAmount
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to complete rental UUID: {}, Fine amount: {}", requesterId, rentalUuid, fineAmount);
        // Add authorization check here if not using @PreAuthorize to ensure requester is Admin/Staff

        Rental completedRental = rentalService.completeRentalByUuid(rentalUuid, fineAmount);
        log.info("Requester [{}]: Successfully completed rental with ID: {} and UUID: {}. Fine applied: {}",
                requesterId, completedRental.getId(), completedRental.getUuid(), fineAmount);
        return ResponseEntity.ok(RentalMapper.toDto(completedRental));
    }
}