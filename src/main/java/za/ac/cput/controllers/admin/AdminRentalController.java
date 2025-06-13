package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.*;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * AdminRentalController.java
 * Controller for administrators to manage Rental entities.
 * Allows admins to create, retrieve, update (limited fields via RentalUpdateDTO),
 * delete, confirm, cancel, and complete rentals.
 * <p>
 * Author: [Original Author Name]
 * Updated by: Peter Buckingham
 * Date: [Original Date]
 * Updated: 2025-05-29
 */
@RestController
@RequestMapping("/api/v1/admin/rentals")
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
@Tag(name = "Admin Rental Management", description = "Endpoints for administrators to manage all aspects of rentals.")
public class AdminRentalController {

    private static final Logger log = LoggerFactory.getLogger(AdminRentalController.class);

    private final IRentalService rentalService;
    private final IUserService userService;
    private final ICarService carService;
    private final IDriverService driverService;
    private final IFileStorageService fileStorageService;

    @Autowired
    public AdminRentalController(IRentalService rentalService, IUserService userService,
                                 ICarService carService, IDriverService driverService, IFileStorageService fileStorageService) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.carService = carService;
        this.driverService = driverService;
        this.fileStorageService = fileStorageService;
        log.info("AdminRentalController initialized.");
    }

    // --- GET all, POST create, GET by UUID methods ---
    // (These remain largely the same, ensure createRentalByAdmin maps expectedReturnedDate from RentalRequestDTO)
// In AdminRentalController.java (or StaffOperationsController)

    /**
     * Retrieves rentals that are due for return today.
     * Intended for staff/admin use to manage daily operations.
     *
     * @return ResponseEntity with a list of RentalResponseDTOs due for return today, or no content if none.
     */
    @GetMapping("/returns-due-today")
    @Operation(summary = "Get rentals due for return today", description = "Retrieves active rentals scheduled to be returned today.")
    // @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsDueForReturnToday() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Staff/Admin [{}]: Request for rentals due for return today.", requesterId);
        List<Rental> rentals = rentalService.findRentalsDueToday(); // We created this service method
        if (rentals.isEmpty()) {
            log.info("Staff/Admin [{}]: No rentals due for return today.", requesterId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(rentals, fileStorageService));
    }

    /**
     * Retrieves rentals that are currently overdue.
     * Intended for staff/admin use to follow up on late returns.
     *
     * @return ResponseEntity with a list of overdue RentalResponseDTOs, or no content if none.
     */
    @GetMapping("/overdue-rentals")
    @Operation(summary = "Get overdue rentals", description = "Retrieves rentals that have passed their expected return date and are not yet returned.")
    // @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<RentalResponseDTO>> getOverdueRentals() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Staff/Admin [{}]: Request for overdue rentals.", requesterId);
        List<Rental> rentals = rentalService.findOverdueRentals(); // We created this
        if (rentals.isEmpty()) {
            log.info("Staff/Admin [{}]: No overdue rentals.", requesterId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(rentals , fileStorageService));
    }

    /**
     * Retrieves all currently active rentals.
     *
     * @return ResponseEntity with a list of active RentalResponseDTOs, or no content if none.
     */
    @GetMapping("/active")
    @Operation(summary = "Get all active rentals", description = "Retrieves all rentals that are currently in an ACTIVE status.")
    public ResponseEntity<List<RentalResponseDTO>> getActiveRentals() {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to get all active rentals.", adminId);
        List<Rental> activeRentals = rentalService.findActiveRentals();
        if (activeRentals.isEmpty()) {
            log.info("Admin [{}]: No active rentals found.", adminId);
            return ResponseEntity.noContent().build();
        }
        List<RentalResponseDTO> dtoList = RentalMapper.toDtoList(activeRentals , fileStorageService);
        log.info("Admin [{}]: Successfully retrieved {} active rentals.", adminId, dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Creates a new rental by an administrator.
     * Admin can specify user, car, driver, and other rental details.
     *
     * @param createDto The DTO containing data for the new rental.
     * @return A ResponseEntity containing the created RentalResponseDTO and HTTP status CREATED.
     */
    @PostMapping
    @Operation(summary = "Create a rental (Admin)", description = "Allows an administrator to create a new rental, specifying user, car, and driver.")
    public ResponseEntity<RentalResponseDTO> createRentalByAdmin(
            @Parameter(description = "Data for the new rental", required = true) @Valid @RequestBody RentalRequestDTO createDto) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to create a new rental with DTO: {}", adminId, createDto);

        User userEntity = userService.read(createDto.getUserUuid());
        Car carEntity = carService.read(createDto.getCarUuid());
        Driver driverEntity = null;
        if (createDto.getDriverUuid() != null) {
            driverEntity = driverService.read(createDto.getDriverUuid());
        }

        // RentalMapper.toEntity should correctly handle createDto.getExpectedReturnedDate()
        Rental rentalToCreate = RentalMapper.toEntity(createDto, userEntity, carEntity, driverEntity);

        Rental createdEntity = rentalService.create(rentalToCreate);
        log.info("Admin [{}]: Successfully created rental with ID: {} and UUID: {}", adminId, createdEntity.getId(), createdEntity.getUuid());
        return new ResponseEntity<>(RentalMapper.toDto(createdEntity , fileStorageService), HttpStatus.CREATED);
    }

    /**
     * Retrieves all rentals for administrative view.
     *
     * @return ResponseEntity with a list of all RentalResponseDTOs, or no content if none.
     */
    @GetMapping
    @Operation(summary = "Get all rentals (Admin)", description = "Retrieves a list of all rentals in the system.")
    public ResponseEntity<List<RentalResponseDTO>> getAllRentalsForAdmin() {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to get all rentals.", adminId);
        List<Rental> rentals = rentalService.getAll();
        if (rentals.isEmpty()) {
            log.info("Admin [{}]: No rentals found.", adminId);
            return ResponseEntity.noContent().build();
        }
        List<RentalResponseDTO> dtoList = RentalMapper.toDtoList(rentals , fileStorageService);
        log.info("Admin [{}]: Successfully retrieved {} rentals.", adminId, dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Retrieves a specific rental by its UUID for administrative purposes.
     *
     * @param rentalUuid The UUID of the rental to retrieve.
     * @return A ResponseEntity containing the RentalResponseDTO if found.
     */
    @GetMapping("/{rentalUuid}")
    @Operation(summary = "Get rental by UUID (Admin)", description = "Retrieves a specific rental by its UUID.")
    public ResponseEntity<RentalResponseDTO> getRentalByUuidAdmin(
            @Parameter(description = "UUID of the rental to retrieve", required = true) @PathVariable UUID rentalUuid) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to get rental by UUID: {}", adminId, rentalUuid);
        Rental rentalEntity = rentalService.read(rentalUuid);
        log.info("Admin [{}]: Successfully retrieved rental with ID: {} for UUID: {}", adminId, rentalEntity.getId(), rentalEntity.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(rentalEntity , fileStorageService));
    }

    /**
     * Allows an admin to update specific fields of an existing rental using {@link RentalUpdateDTO}.
     * Updatable fields include expected return date, and reassigning car or driver.
     * Other changes (status, actual return, fines) are handled by specific action endpoints.
     *
     * @param rentalUuid The UUID of the rental to update.
     * @param updateDto  The {@link RentalUpdateDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link RentalResponseDTO}.
     * @throws ResourceNotFoundException if the rental or any newly referenced Car or Driver is not found.
     * @throws CarNotAvailableException  if a newly assigned car is not available.
     */
    @PutMapping("/{rentalUuid}")
    @Operation(summary = "Update a rental (Admin)",
               description = "Allows an administrator to update specific fields of an existing rental, such as dates, car, driver, or status.")
    public ResponseEntity<RentalResponseDTO> updateRentalByAdmin(
            @Parameter(description = "UUID of the rental to update", required = true) @PathVariable UUID rentalUuid,
            @Parameter(description = "Data for updating the rental", required = true) @Valid @RequestBody RentalUpdateDTO updateDto // <--- CHANGE DTO TYPE HERE
    ) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to update rental UUID: {} with AdminRentalUpdateDTO: {}", adminId, rentalUuid, updateDto);

        Rental existingRental = rentalService.read(rentalUuid);
        log.debug("Admin [{}]: Found existing rental (ID: {}, UUID: {}) for update. Current data: {}",
                adminId, existingRental.getId(), existingRental.getUuid(), existingRental);

        Rental.Builder rentalBuilder = new Rental.Builder().copy(existingRental);
        boolean changed = false;

        // User (typically not changed, but if DTO allows)
        if (updateDto.getUserUuid() != null && !updateDto.getUserUuid().equals(existingRental.getUser().getUuid())) {
            User newUser = userService.read(updateDto.getUserUuid());
            rentalBuilder.setUser(newUser);
            changed = true;
            log.debug("Admin [{}]: Rental User updated to UUID: {}", adminId, updateDto.getUserUuid());
        }

        // Car
        if (updateDto.getCarUuid() != null &&
                (existingRental.getCar() == null || !updateDto.getCarUuid().equals(existingRental.getCar().getUuid()))) {
            log.debug("Admin [{}]: Rental Car change requested. New Car UUID: {}", adminId, updateDto.getCarUuid());
            Car newCarEntity = carService.read(updateDto.getCarUuid());
            if (!newCarEntity.isAvailable() && (existingRental.getCar() == null || !newCarEntity.getUuid().equals(existingRental.getCar().getUuid()))) {
                throw new CarNotAvailableException("Newly selected car (UUID: " + updateDto.getCarUuid() + ") is not available.");
            }
            // Old car availability logic
            if (existingRental.getCar() != null && !existingRental.getCar().isAvailable() && (existingRental.getStatus() == za.ac.cput.domain.enums.RentalStatus.ACTIVE || existingRental.getStatus() == RentalStatus.ACTIVE || existingRental.getStatus() == RentalStatus.ACTIVE)) {
                Car oldCar = new Car.Builder().copy(existingRental.getCar()).setAvailable(true).build();
                carService.update(oldCar);
                log.info("Admin [{}]: Rental update - Old car ID {} made available.", adminId, oldCar.getId());
            }
            rentalBuilder.setCar(newCarEntity);
            changed = true;
        }

        // Driver
        if (updateDto.getDriverUuid() != null) {
            if (existingRental.getDriver() == null || !updateDto.getDriverUuid().equals(existingRental.getDriver().getUuid())) {
                Driver newDriverEntity = driverService.read(updateDto.getDriverUuid());
                rentalBuilder.setDriver(newDriverEntity);
                changed = true;
                log.debug("Admin [{}]: Rental Driver updated/assigned: {}", adminId, updateDto.getDriverUuid());
            }
        } else if (existingRental.getDriver() != null) { // DTO has driverUuid as null, and there was an existing driver
            rentalBuilder.setDriver(null);
            changed = true;
            log.debug("Admin [{}]: Rental Driver removed.", adminId);
        }

        // Issuer ID
        if (updateDto.getIssuer() != null && !updateDto.getIssuer().equals(existingRental.getIssuer())) {
            rentalBuilder.setIssuer(updateDto.getIssuer());
            changed = true;
            log.debug("Admin [{}]: Rental IssuerId updated to: {}", adminId, updateDto.getIssuer());
        }

        // Receiver ID
        if (updateDto.getReceiver() != null && !updateDto.getReceiver().equals(existingRental.getReceiver())) {
            rentalBuilder.setReceiver(updateDto.getReceiver());
            changed = true;
            log.debug("Admin [{}]: Rental ReceiverId updated to: {}", adminId, updateDto.getReceiver());
        }

        // Fine Amount (careful with double comparison for equality)
        if (updateDto.getFine() != null &&
                (existingRental.getFine() == 0 || Math.abs(updateDto.getFine() - existingRental.getFine()) > 0.001)) { // Compare doubles with tolerance
            rentalBuilder.setFine(updateDto.getFine().intValue()); // Assuming entity fine is int
            changed = true;
            log.debug("Admin [{}]: Rental Fine updated to: {}", adminId, updateDto.getFine());
        }

        // Issued Date
        if (updateDto.getIssuedDate() != null && !updateDto.getIssuedDate().equals(existingRental.getIssuedDate())) {
            rentalBuilder.setIssuedDate(updateDto.getIssuedDate());
            changed = true;
            log.debug("Admin [{}]: Rental IssuedDate updated to: {}", adminId, updateDto.getIssuedDate());
        }

        // Expected Return Date
        if (updateDto.getExpectedReturnDate() != null && !updateDto.getExpectedReturnDate().equals(existingRental.getExpectedReturnDate())) {
            rentalBuilder.setExpectedReturnDate(updateDto.getExpectedReturnDate());
            changed = true;
            log.debug("Admin [{}]: Rental ExpectedReturnedDate updated to: {}", adminId, updateDto.getExpectedReturnDate());
        }

        // Actual Return Date
        if (updateDto.getReturnedDate() != null && !updateDto.getReturnedDate().equals(existingRental.getReturnedDate())) {
            rentalBuilder.setReturnedDate(updateDto.getReturnedDate()); // Map to entity's 'returnedDate'
            changed = true;
            log.debug("Admin [{}]: Rental ActualReturnedDate (entity.returnedDate) updated to: {}", adminId, updateDto.getReturnedDate());
        }

        // Status
        if (updateDto.getStatus() != null) {
            try {
                RentalStatus newStatusEnum = RentalStatus.valueOf(updateDto.getStatus().trim().toUpperCase());
                if (existingRental.getStatus() != newStatusEnum) {
                    rentalBuilder.setStatus(newStatusEnum);
                    changed = true;
                    log.debug("Admin [{}]: Rental Status updated to: {}", adminId, newStatusEnum);
                }
            } catch (IllegalArgumentException e) {
                log.warn("Admin [{}]: Invalid status string '{}' in DTO. Status not changed.", adminId, updateDto.getStatus());
            }
        }

        if (!changed) {
            log.info("Admin [{}]: No updatable fields provided in DTO or values are the same for rental UUID: {}. No update performed.", adminId, rentalUuid);
            return ResponseEntity.ok(RentalMapper.toDto(existingRental , fileStorageService));
        }

        Rental rentalWithUpdates = rentalBuilder.build();
        Rental persistedRental = rentalService.update(rentalWithUpdates);
        // Car availability logic on update should be in rentalService.update

        log.info("Admin [{}]: Successfully updated rental ID: {}, UUID: {}", adminId, persistedRental.getId(), persistedRental.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(persistedRental , fileStorageService));
    }

    // ... DELETE and other action methods (confirm, cancel, complete) ...

    /**
     * Deletes a rental by its UUID (Admin action).
     *
     * @param rentalUuid The UUID of the rental to delete.
     * @return ResponseEntity with no content if successful, or not found.
     */
    @DeleteMapping("/{rentalUuid}")
    @Operation(summary = "Delete a rental (Admin)", description = "Allows an administrator to soft-delete a rental by its UUID.")
    public ResponseEntity<Void> deleteRentalByAdmin(
            @Parameter(description = "UUID of the rental to delete", required = true) @PathVariable UUID rentalUuid) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to delete rental with UUID: {}", adminId, rentalUuid);
        Rental existingRental = rentalService.read(rentalUuid);
        log.debug("Admin [{}]: Found rental with ID: {} (UUID: {}) for deletion.", adminId, existingRental.getId(), existingRental.getUuid());

        boolean deleted = rentalService.delete(existingRental.getId());
        if (!deleted) {
            log.warn("Admin [{}]: Rental with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", adminId, existingRental.getId(), existingRental.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Admin [{}]: Successfully soft-deleted rental with ID: {} (UUID: {}).", adminId, existingRental.getId(), existingRental.getUuid());
        return ResponseEntity.noContent().build();
    }

    /**
     * Confirms a rental (Admin action).
     *
     * @param rentalUuid The UUID of the rental to confirm.
     * @return ResponseEntity with the confirmed RentalResponseDTO.
     */
    @PostMapping("/{rentalUuid}/confirm")
    @Operation(summary = "Confirm a rental (Admin)", description = "Allows an administrator to confirm a rental.")
    public ResponseEntity<RentalResponseDTO> confirmRentalByAdmin(
            @Parameter(description = "UUID of the rental to confirm", required = true) @PathVariable UUID rentalUuid) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to confirm rental with UUID: {}", adminId, rentalUuid);
        Rental confirmedRental = rentalService.confirmRentalByUuid(rentalUuid);
        log.info("Admin [{}]: Successfully confirmed rental with ID: {} and UUID: {}", adminId, confirmedRental.getId(), confirmedRental.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(confirmedRental , fileStorageService));
    }

    /**
     * Cancels a rental (Admin action).
     *
     * @param rentalUuid The UUID of the rental to cancel.
     * @return ResponseEntity with the cancelled RentalResponseDTO.
     */
    @PostMapping("/{rentalUuid}/cancel")
    @Operation(summary = "Cancel a rental (Admin)", description = "Allows an administrator to cancel a rental.")
    public ResponseEntity<RentalResponseDTO> cancelRentalByAdmin(
            @Parameter(description = "UUID of the rental to cancel", required = true) @PathVariable UUID rentalUuid) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to cancel rental with UUID: {}", adminId, rentalUuid);
        Rental cancelledRental = rentalService.cancelRentalByUuid(rentalUuid);
        log.info("Admin [{}]: Successfully cancelled rental with ID: {} and UUID: {}", adminId, cancelledRental.getId(), cancelledRental.getUuid());
        return ResponseEntity.ok(RentalMapper.toDto(cancelledRental , fileStorageService));
    }

    /**
     * Completes a rental, optionally applying a fine (Admin action).
     *
     * @param rentalUuid The UUID of the rental to complete.
     * @param fineAmount Optional fine amount to apply.
     * @return ResponseEntity with the completed RentalResponseDTO.
     */
    @PostMapping("/{rentalUuid}/complete")
    @Operation(summary = "Complete a rental (Admin)", description = "Allows an administrator to mark a rental as completed, optionally applying a fine.")
    public ResponseEntity<RentalResponseDTO> completeRentalByAdmin(
            @Parameter(description = "UUID of the rental to complete", required = true) @PathVariable UUID rentalUuid,
            @Parameter(description = "Fine amount to apply (optional, defaults to 0.0)") @RequestParam(required = false, defaultValue = "0.0") double fineAmount
    ) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to complete rental with UUID: {}, Fine amount: {}", adminId, rentalUuid, fineAmount);
        Rental completedRental = rentalService.completeRentalByUuid(rentalUuid, fineAmount);
        log.info("Admin [{}]: Successfully completed rental with ID: {} and UUID: {}. Fine applied: {}", adminId, completedRental.getId(), completedRental.getUuid(), fineAmount);
        return ResponseEntity.ok(RentalMapper.toDto(completedRental , fileStorageService));
    }


}