package za.ac.cput.controllers.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.BookingRequestDTO;
import za.ac.cput.domain.dto.request.BookingUpdateDTO;
import za.ac.cput.domain.dto.response.BookingResponseDTO;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.domain.mapper.BookingMapper;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.*;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * AdminBookingController handles administrative operations for bookings.
 * Admins can create, read, update, delete, confirm, and cancel bookings.
 * Bookings are identified externally by UUIDs, while internal service operations
 * primarily use integer IDs.
 * This controller is responsible for resolving UUIDs to entities before calling
 * service methods that expect entities or their internal IDs.
 */
@RestController
@RequestMapping("/api/v1/admin/bookings")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')") // Class-level security
@Api(value = "Admin Booking Management", tags = "Admin Booking Management")
public class AdminBookingController {

    private static final Logger log = LoggerFactory.getLogger(AdminBookingController.class);

    private final IBookingService bookingService;
    private final IUserService userService;
    private final ICarService carService;
    private final IDriverService driverService;
    private final IFileStorageService fileStorageService;

    /**
     * Constructs an AdminBookingController with necessary service dependencies.
     *
     * @param bookingService The booking service.
     * @param userService    The user service.
     * @param carService     The car service.
     * @param driverService  The driver service.
     */
    @Autowired
    public AdminBookingController(IBookingService bookingService, IUserService userService,
                                  ICarService carService, IDriverService driverService, IFileStorageService fileStorageService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.carService = carService;
        this.driverService = driverService;
        this.fileStorageService = fileStorageService;
        log.info("AdminBookingController initialized.");
    }

    /**
     * Retrieves all bookings.
     * This endpoint is intended for admin use and may return bookings in various states.
     *
     * @return A ResponseEntity containing a list of {@link BookingResponseDTO} or no content if none exist.
     */
    @GetMapping
    @ApiOperation(value = "Get all bookings (Admin)", notes = "Retrieves all bookings in the system.")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        log.info("Admin request to get all bookings.");
        List<Booking> bookings = bookingService.getAll();
        if (bookings.isEmpty()) {
            log.info("No bookings found.");
            return ResponseEntity.noContent().build();
        }
        List<BookingResponseDTO> bookingResponseDTOS = BookingMapper.toDtoList(bookings,fileStorageService);
        log.info("Successfully retrieved {} bookings.", bookingResponseDTOS.size());
        return ResponseEntity.ok(bookingResponseDTOS);
    }

    /**
     * Allows an admin to create a new booking.
     * The admin can specify the user, car, driver, and other booking details.
     *
     * @param createDto The {@link BookingRequestDTO} containing booking creation details.
     * @return A ResponseEntity containing the created {@link BookingResponseDTO} and HTTP status CREATED.
     * @throws ResourceNotFoundException if the specified User, Car, or Driver (if provided) is not found.
     */
    @PostMapping
    @ApiOperation(value = "Create a new booking (Admin)", notes = "Allows an administrator to create a new booking, specifying user, car, and driver.")
    public ResponseEntity<BookingResponseDTO> createBookingByAdmin(
            @ApiParam(value = "Booking creation data", required = true) @Valid @RequestBody BookingRequestDTO createDto) {
        log.info("Admin request to create a new booking with DTO: {}", createDto);

        log.debug("Fetching user with UUID: {}", createDto.getUserUuid());
        User userEntity = userService.read(createDto.getUserUuid());
        if (userEntity == null) {
            log.warn("User not found for UUID: {}", createDto.getUserUuid());
            throw new ResourceNotFoundException("User not found with UUID: " + createDto.getUserUuid());
        }
        log.debug("Found user: {}", userEntity.getId());


        log.debug("Fetching car with UUID: {}", createDto.getCarUuid());
        Car carEntity = carService.read(createDto.getCarUuid());
        if (carEntity == null) {
            log.warn("Car not found for UUID: {}", createDto.getCarUuid());
            throw new ResourceNotFoundException("Car not found with UUID: " + createDto.getCarUuid());
        }
        log.debug("Found car: {}", carEntity.getLicensePlate());


        Driver driverEntity = null;
        if (createDto.getDriverUuid() != null) {
            log.debug("Fetching driver with UUID: {}", createDto.getDriverUuid());
            driverEntity = driverService.read(createDto.getDriverUuid());
            if (driverEntity == null) {
                log.warn("Driver not found for UUID: {}", createDto.getDriverUuid());
                throw new ResourceNotFoundException("Driver not found with UUID: " + createDto.getDriverUuid());
            }
            log.debug("Found driver: {}", driverEntity.getId());
        } else {
            log.debug("No driver UUID provided for booking creation.");
        }

        Booking bookingToCreate = BookingMapper.toEntity(createDto, userEntity, carEntity, driverEntity);
        log.debug("Mapped DTO to Booking entity for creation: {}", bookingToCreate);

        Booking createdEntity = bookingService.create(bookingToCreate);
        log.info("Successfully created booking with ID: {} and UUID: {}", createdEntity.getId(), createdEntity.getUuid());
        return new ResponseEntity<>(BookingMapper.toDto(createdEntity , fileStorageService), HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific booking by its UUID for administrative purposes.
     *
     * @param bookingUuid The UUID of the booking to retrieve.
     * @return A ResponseEntity containing the {@link BookingResponseDTO} if found.
     * @throws ResourceNotFoundException if the booking with the given UUID is not found.
     */
    @GetMapping("/{bookingUuid}")
    @ApiOperation(value = "Get booking by UUID (Admin)", notes = "Retrieves a specific booking by its UUID for administrative purposes.")
    public ResponseEntity<BookingResponseDTO> getBookingByUuid_Admin(
            @ApiParam(value = "UUID of the booking to retrieve", required = true) @PathVariable UUID bookingUuid) {
        log.info("Admin request to get booking by UUID: {}", bookingUuid);
        Booking bookingEntity = bookingService.read(bookingUuid);
        // Assuming bookingService.read(UUID) throws ResourceNotFoundException if not found.
        // If not, add: if (bookingEntity == null) throw new ResourceNotFoundException(...);
        log.info("Successfully retrieved booking with ID: {} for UUID: {}", bookingEntity.getId(), bookingUuid);
        return ResponseEntity.ok(BookingMapper.toDto(bookingEntity, fileStorageService));
    }

    /**
     * Allows an admin to update an existing booking.
     *
     * @param bookingUuid The UUID of the booking to update.
     * @param updateDto   The {@link BookingUpdateDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link BookingResponseDTO}.
     * @throws ResourceNotFoundException if the booking or any referenced User, Car, or Driver (if changed) is not found.
     */
    @PutMapping("/{bookingUuid}")
    @ApiOperation(value = "Update a booking (Admin)", notes = "Allows an administrator to update an existing booking.")
    public ResponseEntity<BookingResponseDTO> updateBookingByAdmin(
            @ApiParam(value = "UUID of the booking to update", required = true) @PathVariable UUID bookingUuid,
            @ApiParam(value = "Booking update data", required = true) @Valid @RequestBody BookingUpdateDTO updateDto
    ) {
        log.info("Admin request to update booking with UUID: {}. Update DTO: {}", bookingUuid, updateDto);
        Booking existingBooking = bookingService.read(bookingUuid);
        log.debug("Found existing booking with ID: {} for UUID: {}", existingBooking.getId(), bookingUuid);


        User userEntity = existingBooking.getUser();
        if (updateDto.getUserUuid() != null && !updateDto.getUserUuid().equals(existingBooking.getUser().getUuid())) {
            log.debug("Updating user. Fetching new user with UUID: {}", updateDto.getUserUuid());
            userEntity = userService.read(updateDto.getUserUuid());
            if (userEntity == null) {
                log.warn("User for update not found with UUID: {}", updateDto.getUserUuid());
                throw new ResourceNotFoundException("User for update not found: " + updateDto.getUserUuid());
            }
            log.debug("Found user for update: {}", userEntity.getId());
        }

        Car carEntity = existingBooking.getCar();
        if (updateDto.getCarUuid() != null && !updateDto.getCarUuid().equals(existingBooking.getCar().getUuid())) {
            log.debug("Updating car. Fetching new car with UUID: {}", updateDto.getCarUuid());
            carEntity = carService.read(updateDto.getCarUuid());
            if (carEntity == null) {
                log.warn("Car for update not found with UUID: {}", updateDto.getCarUuid());
                throw new ResourceNotFoundException("Car for update not found: " + updateDto.getCarUuid());
            }
            log.debug("Found car for update: {}", carEntity.getLicensePlate());
        }

        Driver driverEntity = existingBooking.getDriver();
        if (updateDto.getDriverUuid() != null) { // If a driver UUID is provided in the update
            if (existingBooking.getDriver() == null || !updateDto.getDriverUuid().equals(existingBooking.getDriver().getUuid())) {
                log.debug("Updating driver. Fetching new driver with UUID: {}", updateDto.getDriverUuid());
                driverEntity = driverService.read(updateDto.getDriverUuid());
                if (driverEntity == null) { // No need to check updateDto.getDriverUuid() != null again here
                    log.warn("Driver for update not found with UUID: {}", updateDto.getDriverUuid());
                    throw new ResourceNotFoundException("Driver for update not found: " + updateDto.getDriverUuid());
                }
                log.debug("Found driver for update: {}", driverEntity.getId());
            }
        } else if (updateDto.getDriverUuid() == null && existingBooking.getDriver() != null && updateDto.getDriverUuid() != null) {
            // This condition means driverUuid was explicitly provided as null in the request JSON
            // and there was an existing driver.
            log.debug("Driver explicitly set to null for booking update.");
            driverEntity = null;
        }
        // To handle the case where driverUuid is not in the DTO request at all (meaning no change to driver intended),
        // ensure BookingUpdateDTO has a way to distinguish between "not provided" and "explicitly null".
        // For simplicity, the current logic relies on updateDto.getDriverUuid() directly.
        // If you add `isDriverUuidPresentInRequest` to BookingUpdateDTO:
        // if (updateDto.getIsDriverUuidPresentInRequest()) { // Check if driver UUID was part of the request
        //    if (updateDto.getDriverUuid() == null) { // Explicitly set to null
        //        driverEntity = null;
        //        log.debug("Driver explicitly set to null for booking update.");
        //    } else if (existingBooking.getDriver() == null || !updateDto.getDriverUuid().equals(existingBooking.getDriver().getUuid())) {
        //        log.debug("Updating driver. Fetching new driver with UUID: {}", updateDto.getDriverUuid());
        //        driverEntity = driverService.read(updateDto.getDriverUuid());
        //        if (driverEntity == null) {
        //            log.warn("Driver for update not found with UUID: {}", updateDto.getDriverUuid());
        //            throw new ResourceNotFoundException("Driver for update not found: " + updateDto.getDriverUuid());
        //        }
        //        log.debug("Found driver for update: {}", driverEntity.getDriverId());
        //    }
        // }


        Booking bookingWithUpdates = BookingMapper.applyUpdateDtoToEntity(updateDto, existingBooking, userEntity, carEntity, driverEntity);
        log.debug("Applied DTO updates to Booking entity: {}", bookingWithUpdates);

        Booking persistedBooking = bookingService.update(bookingWithUpdates); // Service.update takes entity
        log.info("Successfully updated booking with ID: {} and UUID: {}", persistedBooking.getId(), persistedBooking.getId());

        return ResponseEntity.ok(BookingMapper.toDto(persistedBooking, fileStorageService));
    }

    /**
     * Allows an admin to soft-delete a booking by its UUID.
     * The actual deletion logic (soft or hard) is handled by the service layer.
     *
     * @param bookingUuid The UUID of the booking to delete.
     * @return A ResponseEntity with no content if successful, or not found if the booking doesn't exist.
     * @throws ResourceNotFoundException if the booking service indicates the resource was not found prior to deletion.
     */
    @DeleteMapping("/{bookingUuid}")
    @ApiOperation(value = "Delete a booking (Admin)", notes = "Allows an administrator to soft-delete a booking by its UUID.")
    public ResponseEntity<Void> deleteBookingByAdmin(
            @ApiParam(value = "UUID of the booking to delete", required = true) @PathVariable UUID bookingUuid) {
        log.info("Admin request to delete booking with UUID: {}", bookingUuid);
        Booking existingBooking = bookingService.read(bookingUuid); // Fetch current entity to get its internal ID
        log.debug("Found booking with ID: {} for UUID: {} to be deleted.", existingBooking.getId(), bookingUuid);

        boolean deleted = bookingService.delete(existingBooking.getId()); // Service method uses internal ID
        if (!deleted) {
            // This case might indicate an issue if the service is expected to throw an exception for not found,
            // or if it returns false for other reasons (e.g., business rule preventing deletion).
            // If bookingService.delete(id) throws ResourceNotFoundException for a non-existent ID,
            // this block might not be reached for "not found" scenarios handled by the service.
            log.warn("Booking with ID: {} (UUID: {}) could not be deleted by service, or was already deleted.", existingBooking.getId(), bookingUuid);
            // Consider if service.delete should throw if not found, rather than returning false.
            // If it returns false because it was already "deleted" (soft-delete), then noContent might still be appropriate.
            // For now, adhering to existing structure, returning notFound if service says !deleted.
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully deleted booking with ID: {} (UUID: {}).", existingBooking.getId(), bookingUuid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Allows an admin to confirm a booking.
     *
     * @param bookingUuid The UUID of the booking to confirm.
     * @return A ResponseEntity containing the updated {@link BookingResponseDTO} with CONFIRMED status.
     * @throws ResourceNotFoundException if the booking is not found.
     */
    @PostMapping("/{bookingUuid}/confirm")
    @ApiOperation(value = "Confirm a booking (Admin)", notes = "Allows an administrator to confirm a booking.")
    public ResponseEntity<BookingResponseDTO> confirmBookingByAdmin(
            @ApiParam(value = "UUID of the booking to confirm", required = true) @PathVariable UUID bookingUuid) {
        log.info("Admin request to confirm booking with UUID: {}", bookingUuid);
        Booking existingBooking = bookingService.read(bookingUuid);
        log.debug("Found booking with ID: {} (UUID: {}) for confirmation.", existingBooking.getId(), bookingUuid);

        // Create a new Booking instance with the updated status.
        // This ensures immutability if the Builder pattern creates a new object,
        // or prepares the existing one for update via service layer.
        Booking bookingToUpdate = new Booking.Builder()
                .copy(existingBooking)
                .setStatus(BookingStatus.CONFIRMED) // Use enum's name() for string representation
                .build();

        Booking updatedBooking = bookingService.update(bookingToUpdate); // Service.update takes the entity
        log.info("Successfully confirmed booking with ID: {} (UUID: {}).", updatedBooking.getId(), bookingUuid);
        BookingResponseDTO confirmedDto = BookingMapper.toDto(updatedBooking, fileStorageService);
        return ResponseEntity.ok(confirmedDto);
    }

    /**
     * Allows an admin to cancel a booking.
     *
     * @param bookingUuid The UUID of the booking to cancel.
     * @return A ResponseEntity containing the updated {@link BookingResponseDTO} with CANCELED status.
     * @throws ResourceNotFoundException if the booking is not found.
     */
    @PostMapping("/{bookingUuid}/cancel")
    @ApiOperation(value = "Cancel a booking (Admin)", notes = "Allows an administrator to cancel a booking.")
    public ResponseEntity<BookingResponseDTO> cancelBookingByAdmin(
            @ApiParam(value = "UUID of the booking to cancel", required = true) @PathVariable UUID bookingUuid) {
        log.info("Admin request to cancel booking with UUID: {}", bookingUuid);
        Booking existingBooking = bookingService.read(bookingUuid);
        log.debug("Found booking with ID: {} (UUID: {}) for cancellation.", existingBooking.getId(), bookingUuid);

        Booking bookingToUpdate = new Booking.Builder()
                .copy(existingBooking)
                .setStatus(BookingStatus.ADMIN_CANCELLED) // Use enum's name() for string representation
                .build();

        Booking updatedBooking = bookingService.update(bookingToUpdate); // Service.update takes the entity
        log.info("Successfully canceled booking with ID: {} (UUID: {}).", updatedBooking.getId(), bookingUuid);
        BookingResponseDTO canceledDto = BookingMapper.toDto(updatedBooking, fileStorageService);
        return ResponseEntity.ok(canceledDto);
    }

    /**
     * Retrieves bookings that are confirmed and scheduled for collection today.
     * Intended for staff to prepare for customer pickups.
     *
     * @return ResponseEntity with a list of BookingResponseDTOs due for collection today.
     */
    @GetMapping("/collections-due-today")
    @ApiOperation(value = "Get bookings for collection today (Admin/Staff)", notes = "Retrieves bookings that are confirmed and scheduled for collection today.")
    // @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsForCollectionToday() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Staff/Admin [{}]: Request for bookings due for collection today.", requesterId);
        List<Booking> bookings = bookingService.findBookingsForCollectionToday(); // New service method
        if (bookings.isEmpty()) {
            log.info("Staff/Admin [{}]: No bookings due for collection today.", requesterId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(BookingMapper.toDtoList(bookings, fileStorageService));
    }

}