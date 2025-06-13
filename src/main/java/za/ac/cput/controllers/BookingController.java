package za.ac.cput.controllers;

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
import za.ac.cput.domain.dto.response.BookingResponseDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.mapper.BookingMapper;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IBookingService;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IFileStorageService;
import za.ac.cput.service.IUserService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * BookingController.java
 * Controller for handling user-initiated booking operations.
 * Allows authenticated users to create, view, update, confirm, and cancel their bookings.
 * Also provides helper endpoints to list available cars and fetch the current user's profile.
 * <p>
 * Author: [Peter Buckingham]
 * Date: [2023-04-20]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/bookings")
// @CrossOrigin(...) // Prefer global CORS configuration
@Api(value = "Booking Management", tags = "Booking Management")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final IBookingService bookingService; // Use interface
    private final ICarService carService;
    private final IUserService userService;
    private final IFileStorageService fileStorageService;

    // private final JwtUtilities jwtUtilities; // Removed as not directly used in these methods

    /**
     * Constructs a BookingController with necessary service dependencies.
     *
     * @param bookingService The booking service.
     * @param carService     The car service.
     * @param userService    The user service.
     */
    @Autowired
    public BookingController(IBookingService bookingService, ICarService carService, IUserService userService , IFileStorageService fileStorageService) {
        this.bookingService = bookingService;
        this.carService = carService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        // this.jwtUtilities = jwtUtilities; // Removed
        log.info("BookingController initialized.");
    }

    /**
     * Allows an authenticated user to create a new booking.
     * The user is identified from the security context. The car is specified by its UUID in the request.
     *
     * @param bookingRequestDTO The {@link BookingRequestDTO} containing details for the new booking.
     * @return A ResponseEntity containing the created {@link BookingResponseDTO} and HTTP status CREATED.
     * @throws ResourceNotFoundException if the specified car is not found.
     * @throws CarNotAvailableException  if the specified car is not available for booking.
     */
    @PostMapping
    @ApiOperation(value = "Create a new booking", notes = "Allows an authenticated user to create a new booking.")
    public ResponseEntity<BookingResponseDTO> createBooking(
            @ApiParam(value = "Booking request data", required = true) @Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new booking with DTO: {}", requesterId, bookingRequestDTO);

        User currentUser = userService.read(requesterId); // Fetches user by email (requesterId)
        if (currentUser == null) {
            // This case should ideally be prevented by Spring Security filters for authenticated endpoints.
            log.warn("Requester [{}]: Could not find user profile for authenticated user. This indicates a potential issue.", requesterId);
            // Returning UNAUTHORIZED here, but Spring Security should handle this earlier.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        log.debug("Requester [{}]: Authenticated user found with ID: {}, UUID: {}", requesterId, currentUser.getId(), currentUser.getUuid());

        log.debug("Requester [{}]: Fetching car with UUID: {}", requesterId, bookingRequestDTO.getCarUuid());
        Car carToBook = carService.read(bookingRequestDTO.getCarUuid());
        if (carToBook == null) {
            log.warn("Requester [{}]: Car not found with UUID: {}", requesterId, bookingRequestDTO.getCarUuid());
            throw new ResourceNotFoundException("Car not found with UUID: " + bookingRequestDTO.getCarUuid());
        }
        if (!carToBook.isAvailable()) {
            log.warn("Requester [{}]: Car with UUID {} is not available for booking.", requesterId, carToBook.getUuid());
            throw new CarNotAvailableException("Car with UUID: " + carToBook.getUuid() + " is not available for booking.");
        }
        log.debug("Requester [{}]: Car found for booking: ID: {}, UUID: {}", requesterId, carToBook.getId(), carToBook.getUuid());

        Booking bookingToCreate = BookingMapper.toEntity(bookingRequestDTO, currentUser, carToBook);
        // The service method bookingService.createBooking(Booking) is expected to set initial status,
        // generated UUID, and potentially issuedDate.
        log.debug("Requester [{}]: Mapped DTO to Booking entity for creation: {}", requesterId, bookingToCreate);

        Booking createdBookingEntity = bookingService.create(bookingToCreate); // Use create method from IBookingService interface
        log.info("Requester [{}]: Successfully created booking with ID: {} and UUID: {}",
                requesterId, createdBookingEntity.getId(), createdBookingEntity.getUuid());

        // The car availability update should ideally be handled within the bookingService.create method
        // in a transactional way to ensure data consistency.
        // e.g., carToBook.setAvailable(false); carService.update(carToBook.getId(), carToBook);

        return ResponseEntity.status(HttpStatus.CREATED).body(BookingMapper.toDto(createdBookingEntity, fileStorageService));
    }

    /**
     * Retrieves a specific booking by its UUID.
     * Access control (e.g., ensuring the requester owns the booking or is an admin)
     * should be handled by the service layer or Spring Security.
     *
     * @param bookingUuid The UUID of the booking to retrieve.
     * @return A ResponseEntity containing the {@link BookingResponseDTO} if found, or 404 Not Found.
     */
    @GetMapping("/{bookingUuid}")
    @ApiOperation(value = "Get a booking by UUID", notes = "Retrieves a specific booking by its UUID.")
    public ResponseEntity<BookingResponseDTO> getBookingByUuid(
            @ApiParam(value = "UUID of the booking to retrieve", required = true) @PathVariable UUID bookingUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get booking by UUID: {}", requesterId, bookingUuid);

        // Assuming service has readByUuid which might throw ResourceNotFoundException
        // or a method that also checks ownership for non-admin users.
        Booking bookingEntity = bookingService.read(bookingUuid); // Changed to use read(UUID) from IBookingService
        if (bookingEntity == null) { // This check is redundant if service.read(uuid) throws.
            log.warn("Requester [{}]: Booking not found with UUID: {}", requesterId, bookingUuid);
            return ResponseEntity.notFound().build();
        }

        // Additional check for non-admin users to ensure they own the booking:
        if (!requesterId.equals("GUEST") && !bookingEntity.getUser().getEmail().equals(requesterId) /* && !isRequesterAdmin() */) {
            log.warn("Requester [{}]: Attempted to access booking UUID: {} which they do not own.", requesterId, bookingUuid);
            // Depending on policy, return 403 Forbidden or 404 Not Found to not reveal existence.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Or .notFound()
        }

        log.info("Requester [{}]: Successfully retrieved booking with ID: {} for UUID: {}",
                requesterId, bookingEntity.getId(), bookingEntity.getUuid());
        return ResponseEntity.ok(BookingMapper.toDto(bookingEntity, fileStorageService));
    }

    /**
     * Retrieves all bookings for the currently authenticated user.
     *
     * @return A ResponseEntity containing a list of {@link BookingResponseDTO}s, or 204 No Content if none exist.
     */
    @GetMapping("/my-bookings")
    @ApiOperation(value = "Get current user's bookings", notes = "Retrieves all bookings for the currently authenticated user.")
    public ResponseEntity<List<BookingResponseDTO>> getCurrentUserBookings() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get their bookings.", requesterId);

        if ("GUEST".equals(requesterId)) {
            log.warn("Requester [GUEST]: Attempted to access /my-bookings. Endpoint requires authentication.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userService.read(requesterId);
        if (currentUser == null) {
            log.warn("Requester [{}]: User profile not found. Cannot retrieve bookings.", requesterId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Or internal server error if this state is unexpected
        }

        List<Booking> bookings = bookingService.getUserBookings(currentUser.getId());
        if (bookings.isEmpty()) {
            log.info("Requester [{}]: No bookings found for this user.", requesterId);
            return ResponseEntity.noContent().build();
        }
        log.info("Requester [{}]: Successfully retrieved {} bookings for this user.", requesterId, bookings.size());
        return ResponseEntity.ok(BookingMapper.toDtoList(bookings, fileStorageService));
    }

    /**
     * Allows an authenticated user to update their existing booking.
     * The booking is identified by its UUID.
     *
     * @param bookingUuid       The UUID of the booking to update.
     * @param bookingRequestDTO The {@link BookingRequestDTO} (or a specific BookingUpdateDTO) containing update data.
     * @return A ResponseEntity containing the updated {@link BookingResponseDTO}.
     * @throws ResourceNotFoundException if the booking or a new car (if specified) is not found.
     * @throws CarNotAvailableException  if a new car specified for the update is not available.
     */
    @PutMapping("/{bookingUuid}")
    @ApiOperation(value = "Update an existing booking", notes = "Allows an authenticated user to update their existing booking.")
    public ResponseEntity<BookingResponseDTO> updateBooking(
            @ApiParam(value = "UUID of the booking to update", required = true) @PathVariable UUID bookingUuid,
            @ApiParam(value = "Booking update data", required = true) @Valid @RequestBody BookingRequestDTO bookingRequestDTO // Consider a specific BookingUpdateDTO
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update booking UUID: {} with DTO: {}", requesterId, bookingUuid, bookingRequestDTO);

        Booking existingBooking = bookingService.read(bookingUuid); // Fetch by UUID
        if (existingBooking == null) {
            log.warn("Requester [{}]: Booking not found for update with UUID: {}", requesterId, bookingUuid);
            return ResponseEntity.notFound().build();
        }

        // Authorization: Ensure the requester owns this booking
        if (!requesterId.equals("GUEST") && !existingBooking.getUser().getEmail().equals(requesterId)) {
            log.warn("Requester [{}]: Attempted to update booking UUID: {} which they do not own.", requesterId, bookingUuid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.debug("Requester [{}]: Found existing booking ID: {}, UUID: {} for update.",
                requesterId, existingBooking.getId(), existingBooking.getUuid());

        Car carForUpdate = existingBooking.getCar();
        boolean carChanged = false;
        if (bookingRequestDTO.getCarUuid() != null && !bookingRequestDTO.getCarUuid().equals(existingBooking.getCar().getUuid())) {
            log.debug("Requester [{}]: Car change requested. Old car UUID: {}, New car UUID: {}",
                    requesterId, existingBooking.getCar().getUuid(), bookingRequestDTO.getCarUuid());
            carForUpdate = carService.read(bookingRequestDTO.getCarUuid());
            if (carForUpdate == null) {
                log.warn("Requester [{}]: New car for update not found with UUID: {}", requesterId, bookingRequestDTO.getCarUuid());
                throw new ResourceNotFoundException("New car for update not found with UUID: " + bookingRequestDTO.getCarUuid());
            }
            if (!carForUpdate.isAvailable()) { // Don't check availability if it's the same car being "re-selected"
                log.warn("Requester [{}]: Selected new car UUID: {} is not available.", requesterId, carForUpdate.getUuid());
                throw new CarNotAvailableException("Selected new car is not available.");
            }
            carChanged = true;
            log.debug("Requester [{}]: New car for update found: ID: {}, UUID: {}", requesterId, carForUpdate.getId(), carForUpdate.getUuid());
        }

        // Use Builder pattern for updating the booking entity
        Booking.Builder builder = new Booking.Builder().copy(existingBooking);
        boolean entityChanged = false;

        if (bookingRequestDTO.getBookingStartDate() != null && !bookingRequestDTO.getBookingStartDate().equals(existingBooking.getStartDate())) {
            builder.setStartDate(bookingRequestDTO.getBookingStartDate());
            entityChanged = true;
            log.debug("Requester [{}]: Updating booking start date to: {}", requesterId, bookingRequestDTO.getBookingStartDate());
        }
        if (bookingRequestDTO.getBookingEndDate() != null && !bookingRequestDTO.getBookingEndDate().equals(existingBooking.getEndDate())) {
            builder.setEndDate(bookingRequestDTO.getBookingEndDate());
            entityChanged = true;
            log.debug("Requester [{}]: Updating booking end date to: {}", requesterId, bookingRequestDTO.getBookingEndDate());
        }
        if (carChanged) {
            builder.setCar(carForUpdate);
            entityChanged = true;
        }
        // Add other updatable fields from DTO here...
        // e.g., builder.setStatus(bookingRequestDTO.getStatus()); if status can be updated by user.

        if (!entityChanged) {
            log.info("Requester [{}]: No changes detected for booking UUID: {}. Returning existing booking.", requesterId, bookingUuid);
            return ResponseEntity.ok(BookingMapper.toDto(existingBooking, fileStorageService));
        }

        Booking bookingWithUpdates = builder.build();
        log.debug("Requester [{}]: Booking entity prepared for update: {}", requesterId, bookingWithUpdates);

        Booking updatedBookingEntity = bookingService.update(bookingWithUpdates); // Service takes the full entity
        log.info("Requester [{}]: Successfully updated booking with ID: {} and UUID: {}",
                requesterId, updatedBookingEntity.getId(), updatedBookingEntity.getUuid());
        return ResponseEntity.ok(BookingMapper.toDto(updatedBookingEntity , fileStorageService));
    }

    /**
     * Allows an authenticated user to confirm their booking.
     *
     * @param bookingUuid The UUID of the booking to confirm.
     * @return A ResponseEntity containing the {@link BookingResponseDTO} of the confirmed booking.
     */
    @PostMapping("/{bookingUuid}/confirm")
    @ApiOperation(value = "Confirm a booking", notes = "Allows an authenticated user to confirm their booking.")
    public ResponseEntity<BookingResponseDTO> confirmBooking(
            @ApiParam(value = "UUID of the booking to confirm", required = true) @PathVariable UUID bookingUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to confirm booking UUID: {}", requesterId, bookingUuid);

        Booking bookingToConfirm = bookingService.read(bookingUuid);
        if (bookingToConfirm == null) {
            log.warn("Requester [{}]: Booking not found for confirmation with UUID: {}", requesterId, bookingUuid);
            return ResponseEntity.notFound().build();
        }

        // Authorization: Ensure the requester owns this booking
        if (!requesterId.equals("GUEST") && !bookingToConfirm.getUser().getEmail().equals(requesterId)) {
            log.warn("Requester [{}]: Attempted to confirm booking UUID: {} which they do not own.", requesterId, bookingUuid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.debug("Requester [{}]: Found booking ID: {}, UUID: {} for confirmation.",
                requesterId, bookingToConfirm.getId(), bookingToConfirm.getUuid());

        Booking confirmedBookingEntity = bookingService.confirmBooking(bookingToConfirm.getId());
        log.info("Requester [{}]: Successfully confirmed booking with ID: {} and UUID: {}",
                requesterId, confirmedBookingEntity.getId(), confirmedBookingEntity.getUuid());
        return ResponseEntity.ok(BookingMapper.toDto(confirmedBookingEntity , fileStorageService));
    }

    /**
     * Allows an authenticated user to cancel their booking.
     *
     * @param bookingUuid The UUID of the booking to cancel.
     * @return A ResponseEntity containing the {@link BookingResponseDTO} of the cancelled booking.
     */
    @PostMapping("/{bookingUuid}/cancel")
    @ApiOperation(value = "Cancel a booking", notes = "Allows an authenticated user to cancel their booking.")
    public ResponseEntity<BookingResponseDTO> cancelBooking(
            @ApiParam(value = "UUID of the booking to cancel", required = true) @PathVariable UUID bookingUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to cancel booking UUID: {}", requesterId, bookingUuid);

        Booking bookingToCancel = bookingService.read(bookingUuid);
        if (bookingToCancel == null) {
            log.warn("Requester [{}]: Booking not found for cancellation with UUID: {}", requesterId, bookingUuid);
            return ResponseEntity.notFound().build();
        }

        // Authorization: Ensure the requester owns this booking
        if (!requesterId.equals("GUEST") && !bookingToCancel.getUser().getEmail().equals(requesterId)) {
            log.warn("Requester [{}]: Attempted to cancel booking UUID: {} which they do not own.", requesterId, bookingUuid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.debug("Requester [{}]: Found booking ID: {}, UUID: {} for cancellation.",
                requesterId, bookingToCancel.getId(), bookingToCancel.getUuid());

        Booking canceledBookingEntity = bookingService.cancelBooking(bookingToCancel.getId());
        log.info("Requester [{}]: Successfully cancelled booking with ID: {} and UUID: {}",
                requesterId, canceledBookingEntity.getId(), canceledBookingEntity.getUuid());
        return ResponseEntity.ok(BookingMapper.toDto(canceledBookingEntity , fileStorageService));
    }


    // --- Helper Endpoints (Public or Authenticated as per design) ---

    /**
     * Retrieves a list of all cars currently available for booking.
     * This endpoint is typically public or accessible to authenticated users.
     *
     * @return A ResponseEntity containing a list of {@link CarResponseDTO}s, or 204 No Content if no cars are available.
     */
    @GetMapping("/available-cars")
    @ApiOperation(value = "Get available cars for booking", notes = "Retrieves a list of all cars currently available for booking.")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsForBooking() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get available cars for booking.", requesterId);

        List<Car> cars = carService.findAllAvailableAndNonDeleted();
        if (cars.isEmpty()) {
            log.info("Requester [{}]: No cars currently available for booking.", requesterId);
            return ResponseEntity.noContent().build();
        }
        log.info("Requester [{}]: Successfully retrieved {} available cars.", requesterId, cars.size());
        return ResponseEntity.ok(CarMapper.toDtoList(cars));
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     * Useful for client-side applications to pre-fill user information or display user context.
     *
     * @return A ResponseEntity containing the {@link UserResponseDTO} of the authenticated user.
     * @throws ResourceNotFoundException if the user's profile cannot be found.
     */
    @GetMapping("/user-profile")
    @ApiOperation(value = "Get current user profile", notes = "Retrieves the profile of the currently authenticated user.")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfileForBooking() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get their user profile (for booking context).", requesterId);

        if ("GUEST".equals(requesterId)) {
            log.warn("Requester [GUEST]: Attempted to access /user-profile. Endpoint requires authentication.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.read(requesterId);
        if (user == null) {
            log.warn("Requester [{}]: User profile not found.", requesterId);
            throw new ResourceNotFoundException("User profile not found for: " + requesterId);
        }
        log.info("Requester [{}]: Successfully retrieved user profile with ID: {} and UUID: {}",
                requesterId, user.getId(), user.getUuid());
        return ResponseEntity.ok(UserMapper.toDto(user , fileStorageService));
    }
}