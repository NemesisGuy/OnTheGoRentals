package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.mapper.BookingMapper;
import za.ac.cput.service.*;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * AdminBookingController.java
 * Handles administrative operations for bookings, allowing admins to perform full CRUD actions,
 * change booking statuses, and view all bookings in the system.
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/bookings")
@Tag(name = "Admin: Booking Management", description = "Endpoints for administrators to manage all user bookings.")
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
     * @param bookingService     The booking service.
     * @param userService        The user service.
     * @param carService         The car service.
     * @param driverService      The driver service.
     * @param fileStorageService The service for generating image URLs.
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
     * Retrieves all bookings in the system.
     *
     * @return A ResponseEntity containing a list of all booking DTOs.
     */
    @Operation(summary = "Get all bookings", description = "Retrieves a list of all bookings in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of bookings"),
            @ApiResponse(responseCode = "204", description = "No bookings found in the system")
    })
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        log.info("Admin request to get all bookings.");
        List<Booking> bookings = bookingService.getAll();
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(BookingMapper.toDtoList(bookings, fileStorageService));
    }

    /**
     * Allows an admin to create a new booking on behalf of a user.
     *
     * @param createDto The DTO containing the details for the new booking.
     * @return A ResponseEntity containing the created booking DTO.
     */
    @Operation(summary = "Create a new booking", description = "Allows an administrator to create a new booking, specifying the user, car, and driver.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Specified User, Car, or Driver not found")
    })
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBookingByAdmin(@Valid @RequestBody BookingRequestDTO createDto) {
        log.info("Admin request to create a new booking with DTO: {}", createDto);
        User userEntity = userService.read(createDto.getUserUuid());
        Car carEntity = carService.read(createDto.getCarUuid());

        Driver driverEntity = null;
        if (createDto.getDriverUuid() != null) {
            driverEntity = driverService.read(createDto.getDriverUuid());
        }

        Booking bookingToCreate = BookingMapper.toEntity(createDto, userEntity, carEntity, driverEntity);
        Booking createdEntity = bookingService.create(bookingToCreate);
        return new ResponseEntity<>(BookingMapper.toDto(createdEntity, fileStorageService), HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific booking by its UUID.
     *
     * @param bookingUuid The UUID of the booking to retrieve.
     * @return A ResponseEntity containing the booking DTO.
     */
    @Operation(summary = "Get booking by UUID", description = "Retrieves a specific booking by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{bookingUuid}")
    public ResponseEntity<BookingResponseDTO> getBookingByUuid_Admin(
            @Parameter(description = "UUID of the booking to retrieve", required = true) @PathVariable UUID bookingUuid) {
        log.info("Admin request to get booking by UUID: {}", bookingUuid);
        Booking bookingEntity = bookingService.read(bookingUuid);
        return ResponseEntity.ok(BookingMapper.toDto(bookingEntity, fileStorageService));
    }

    /**
     * Allows an admin to update an existing booking.
     *
     * @param bookingUuid The UUID of the booking to update.
     * @param updateDto   The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated booking DTO.
     */
    @Operation(summary = "Update a booking", description = "Allows an administrator to update an existing booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking updated successfully", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Booking or a related entity for update not found")
    })
    @PutMapping("/{bookingUuid}")
    public ResponseEntity<BookingResponseDTO> updateBookingByAdmin(
            @Parameter(description = "UUID of the booking to update", required = true) @PathVariable UUID bookingUuid,
            @Valid @RequestBody BookingUpdateDTO updateDto) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to update booking with UUID: {}. Update DTO: {}", requesterId, bookingUuid, updateDto);

        // 1. Fetch the existing, full Booking entity from the database.
        Booking existingBooking = bookingService.read(bookingUuid);

        // 2. Fetch related entities IF their UUIDs are provided in the update DTO.
        //    If a UUID is not provided in the DTO, we keep the existing entity.
        User userEntity = existingBooking.getUser();
        if (updateDto.getUserUuid() != null && !updateDto.getUserUuid().equals(existingBooking.getUser().getUuid())) {
            userEntity = userService.read(updateDto.getUserUuid());
        }

        Car carEntity = existingBooking.getCar();
        if (updateDto.getCarUuid() != null && !updateDto.getCarUuid().equals(existingBooking.getCar().getUuid())) {
            carEntity = carService.read(updateDto.getCarUuid());
        }

        Driver driverEntity = existingBooking.getDriver();
        if (updateDto.getDriverUuid() != null) {
            if (existingBooking.getDriver() == null || !updateDto.getDriverUuid().equals(existingBooking.getDriver().getUuid())) {
                driverEntity = driverService.read(updateDto.getDriverUuid());
            }
        }
        // Note: Logic to explicitly set a driver to null would require the DTO to differentiate between "not present" and "null".

        // 3. Use the mapper to apply changes from the DTO to a new Booking entity instance.
        //    This happens entirely within the controller layer.
        Booking bookingWithUpdates = BookingMapper.applyUpdateDtoToEntity(updateDto, existingBooking, userEntity, carEntity, driverEntity);

        // 4. Pass the pure, updated domain entity to the service layer.
        Booking persistedBooking = bookingService.update(bookingWithUpdates);

        log.info("Admin [{}]: Successfully updated booking with UUID: {}", requesterId, persistedBooking.getUuid());

        // 5. Map the final entity back to a DTO for the response.
        return ResponseEntity.ok(BookingMapper.toDto(persistedBooking, fileStorageService));
    }

    /**
     * Allows an admin to soft-delete a booking by its UUID.
     *
     * @param bookingUuid The UUID of the booking to delete.
     * @return A ResponseEntity with status 204 No Content.
     */
    @Operation(summary = "Delete a booking", description = "Allows an administrator to soft-delete a booking by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/{bookingUuid}")
    public ResponseEntity<Void> deleteBookingByAdmin(
            @Parameter(description = "UUID of the booking to delete", required = true) @PathVariable UUID bookingUuid) {
        log.info("Admin request to delete booking with UUID: {}", bookingUuid);
        Booking existingBooking = bookingService.read(bookingUuid);
        bookingService.delete(existingBooking.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves bookings that are confirmed and scheduled for collection today.
     *
     * @return A ResponseEntity with a list of booking DTOs due for collection.
     */
    @Operation(summary = "Get bookings for collection today", description = "Retrieves bookings that are confirmed and scheduled for collection today.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of bookings for collection"),
            @ApiResponse(responseCode = "204", description = "No bookings are due for collection today")
    })
    @GetMapping("/collections-due-today")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsForCollectionToday() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Staff/Admin [{}]: Request for bookings due for collection today.", requesterId);
        List<Booking> bookings = bookingService.findBookingsForCollectionToday();
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(BookingMapper.toDtoList(bookings, fileStorageService));
    }
}