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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.BookingRequestDTO;
import za.ac.cput.domain.dto.request.BookingUpdateDTO;
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
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Booking Management", description = "Endpoints for user-initiated booking operations.")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final IBookingService bookingService;
    private final ICarService carService;
    private final IUserService userService;
    private final IFileStorageService fileStorageService;

    /**
     * Constructs a BookingController with necessary service dependencies.
     *
     * @param bookingService     The booking service for booking logic.
     * @param carService         The car service for car lookups.
     * @param userService        The user service for user lookups.
     * @param fileStorageService The service for generating file URLs.
     */
    @Autowired
    public BookingController(IBookingService bookingService, ICarService carService, IUserService userService, IFileStorageService fileStorageService) {
        this.bookingService = bookingService;
        this.carService = carService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        log.info("BookingController initialized.");
    }

    /**
     * Allows an authenticated user to create a new booking.
     * The user is identified from the security context, and the car is specified by its UUID in the request.
     *
     * @param bookingRequestDTO The DTO containing details for the new booking.
     * @return A ResponseEntity containing the created booking DTO and HTTP status 201 CREATED.
     * @throws ResourceNotFoundException if the specified car is not found.
     * @throws CarNotAvailableException  if the specified car is not available.
     */
    @Operation(summary = "Create a new booking", description = "Allows an authenticated user to create a new booking for an available car.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Car with specified UUID not found"),
            @ApiResponse(responseCode = "409", description = "Car is not available for the requested dates")
    })
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new booking with DTO: {}", requesterId, bookingRequestDTO);

        User currentUser = userService.read(requesterId);
        Car carToBook = carService.read(bookingRequestDTO.getCarUuid());

        if (!carToBook.isAvailable()) {
            throw new CarNotAvailableException("Car with UUID: " + carToBook.getUuid() + " is not available for booking.");
        }

        Booking bookingToCreate = BookingMapper.toEntity(bookingRequestDTO, currentUser, carToBook);
        Booking createdBookingEntity = bookingService.create(bookingToCreate);

        log.info("Requester [{}]: Successfully created booking with UUID: {}", requesterId, createdBookingEntity.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(BookingMapper.toDto(createdBookingEntity, fileStorageService));
    }

    /**
     * Retrieves a specific booking by its UUID.
     * Access is restricted to the user who owns the booking or an administrator.
     *
     * @param bookingUuid The UUID of the booking to retrieve.
     * @return A ResponseEntity containing the booking DTO if found and accessible.
     */
    @Operation(summary = "Get a booking by UUID", description = "Retrieves a specific booking by its UUID. Users can only view their own bookings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User forbidden from accessing this resource"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{bookingUuid}")
    public ResponseEntity<BookingResponseDTO> getBookingByUuid(
            @Parameter(description = "UUID of the booking to retrieve", required = true) @PathVariable UUID bookingUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get booking by UUID: {}", requesterId, bookingUuid);

        Booking bookingEntity = bookingService.read(bookingUuid);
        if (!bookingEntity.getUser().getEmail().equals(requesterId)) {
            log.warn("Requester [{}]: Forbidden to access booking UUID: {}", requesterId, bookingUuid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Requester [{}]: Successfully retrieved booking UUID: {}", requesterId, bookingEntity.getUuid());
        return ResponseEntity.ok(BookingMapper.toDto(bookingEntity, fileStorageService));
    }

    /**
     * Retrieves all bookings for the currently authenticated user.
     *
     * @return A ResponseEntity containing a list of the user's bookings, or 204 No Content if none exist.
     */
    @Operation(summary = "Get current user's bookings", description = "Retrieves all bookings for the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No bookings found for this user")
    })
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDTO>> getCurrentUserBookings() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get their bookings.", requesterId);

        User currentUser = userService.read(requesterId);
        List<Booking> bookings = bookingService.getUserBookings(currentUser.getId());
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(BookingMapper.toDtoList(bookings, fileStorageService));
    }

    /**
     * Allows an authenticated user to update their existing booking.
     *
     * @param bookingUuid      The UUID of the booking to update.
     * @param bookingUpdateDTO The DTO containing the update data.
     * @return A ResponseEntity containing the updated booking DTO.
     */
    @Operation(summary = "Update an existing booking", description = "Allows a user to update the dates or car for their existing booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking updated successfully", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User forbidden from updating this booking"),
            @ApiResponse(responseCode = "404", description = "Booking or new car not found")
    })
    @PutMapping("/{bookingUuid}")
    public ResponseEntity<BookingResponseDTO> updateBooking(
            @Parameter(description = "UUID of the booking to update", required = true) @PathVariable UUID bookingUuid,
            @Valid @RequestBody BookingUpdateDTO bookingUpdateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update booking UUID: {}", requesterId, bookingUuid);

        Booking existingBooking = bookingService.read(bookingUuid);
        if (!existingBooking.getUser().getEmail().equals(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Car carForUpdate = existingBooking.getCar();
        if (bookingUpdateDTO.getCarUuid() != null && !bookingUpdateDTO.getCarUuid().equals(existingBooking.getCar().getUuid())) {
            carForUpdate = carService.read(bookingUpdateDTO.getCarUuid());
        }

        Booking bookingWithUpdates = BookingMapper.applyUpdateDtoToEntity(bookingUpdateDTO, existingBooking, existingBooking.getUser(), carForUpdate, existingBooking.getDriver());
        Booking updatedBookingEntity = bookingService.update(bookingWithUpdates);

        log.info("Requester [{}]: Successfully updated booking UUID: {}", requesterId, updatedBookingEntity.getUuid());
        return ResponseEntity.ok(BookingMapper.toDto(updatedBookingEntity, fileStorageService));
    }

    /**
     * Confirms a booking for the authenticated user.
     *
     * @param bookingUuid The UUID of the booking to confirm.
     * @return A ResponseEntity containing the confirmed booking DTO.
     */
    @Operation(summary = "Confirm a booking", description = "Allows an authenticated user to confirm their booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking confirmed successfully", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User forbidden from confirming this booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{bookingUuid}/confirm")
    public ResponseEntity<BookingResponseDTO> confirmBooking(
            @Parameter(description = "UUID of the booking to confirm", required = true) @PathVariable UUID bookingUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to confirm booking UUID: {}", requesterId, bookingUuid);

        Booking bookingToConfirm = bookingService.read(bookingUuid);
        if (!bookingToConfirm.getUser().getEmail().equals(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Booking confirmedBookingEntity = bookingService.confirmBooking(bookingToConfirm.getId());
        log.info("Requester [{}]: Successfully confirmed booking UUID: {}", requesterId, confirmedBookingEntity.getUuid());
        return ResponseEntity.ok(BookingMapper.toDto(confirmedBookingEntity, fileStorageService));
    }

    /**
     * Cancels a booking for the authenticated user.
     *
     * @param bookingUuid The UUID of the booking to cancel.
     * @return A ResponseEntity containing the cancelled booking DTO.
     */
    @Operation(summary = "Cancel a booking", description = "Allows an authenticated user to cancel their booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User forbidden from cancelling this booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{bookingUuid}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(
            @Parameter(description = "UUID of the booking to cancel", required = true) @PathVariable UUID bookingUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to cancel booking UUID: {}", requesterId, bookingUuid);

        Booking bookingToCancel = bookingService.read(bookingUuid);
        if (!bookingToCancel.getUser().getEmail().equals(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Booking canceledBookingEntity = bookingService.cancelBooking(bookingToCancel.getId());
        log.info("Requester [{}]: Successfully cancelled booking UUID: {}", requesterId, canceledBookingEntity.getUuid());
        return ResponseEntity.ok(BookingMapper.toDto(canceledBookingEntity, fileStorageService));
    }

    /**
     * Retrieves a list of all cars currently available for booking.
     *
     * @return A ResponseEntity containing a list of available car DTOs.
     */
    @Operation(summary = "Get available cars for booking", description = "A helper endpoint to retrieve a list of all cars currently available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available cars retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No cars are currently available")
    })
    @GetMapping("/available-cars")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsForBooking() {
        log.info("Request to get available cars for booking.");
        List<Car> cars = carService.findAllAvailableAndNonDeleted();
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(cars, fileStorageService));
    }

    /**
     * Retrieves the profile of the currently authenticated user for booking context.
     *
     * @return A ResponseEntity containing the user's profile DTO.
     */
    @Operation(summary = "Get current user profile for booking context", description = "A helper endpoint to retrieve the authenticated user's profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile found", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User profile not found")
    })
    @GetMapping("/user-profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfileForBooking() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get their user profile (for booking context).", requesterId);
        User user = userService.read(requesterId);
        return ResponseEntity.ok(UserMapper.toDto(user, fileStorageService));
    }
}