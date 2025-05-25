package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Booking; // For service layer
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.dto.request.BookingRequestDTO;
import za.ac.cput.domain.dto.request.BookingUpdateDTO;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.domain.security.User;

import za.ac.cput.domain.dto.response.BookingResponseDTO;
import za.ac.cput.domain.mapper.BookingMapper;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IBookingService;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IDriverService;
import za.ac.cput.service.IUserService;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/bookings") // Added /v1 for consistency
// @CrossOrigin(...) // Prefer global CORS
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')") // Class-level security
public class AdminBookingController {

    private final IBookingService bookingService;
    private final IUserService userService; // Needed to fetch User entity for creation/update
    private final ICarService carService;   // Needed to fetch Car entity
    private final IDriverService driverService; // Needed to fetch Driver entity

    @Autowired
    public AdminBookingController(IBookingService bookingService, IUserService userService,
                                  ICarService carService, IDriverService driverService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.carService = carService;
        this.driverService = driverService;
    }

    /**
     * Retrieves all bookings (admin view, could include all statuses/deleted).
     */
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<Booking> bookings = bookingService.getAll(); // Service returns entities
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(BookingMapper.toDtoList(bookings));
    }

    /**
     * Admin creates a new booking, can specify user, car, driver, status etc.
     */
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBookingByAdmin(@Valid @RequestBody BookingRequestDTO createDto) {
        User userEntity = userService.read(createDto.getUserUuid());
        if (userEntity == null) throw new ResourceNotFoundException("User not found with UUID: " + createDto.getUserUuid());

        Car carEntity = carService.read(createDto.getCarUuid()); // findByUuidAndNonDeleted must exist in ICarService
        if (carEntity == null) throw new ResourceNotFoundException("Car not found with UUID: " + createDto.getCarUuid());

        Driver driverEntity = null;
        if (createDto.getDriverUuid() != null) {
            driverEntity = driverService.read(createDto.getDriverUuid()); // readByUuid must exist in IDriverService
            if (driverEntity == null) throw new ResourceNotFoundException("Driver not found with UUID: " + createDto.getDriverUuid());
        }

        Booking bookingToCreate = BookingMapper.toEntity(createDto, userEntity, carEntity, driverEntity);
        Booking createdEntity = bookingService.create(bookingToCreate); // Service.create takes entity
        return new ResponseEntity<>(BookingMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    /**
     * Admin retrieves a specific booking by its UUID.
     */
    @GetMapping("/{bookingUuid}")
    public ResponseEntity<BookingResponseDTO> getBookingByUuid_Admin(@PathVariable UUID bookingUuid) {
        Booking bookingEntity = bookingService.read(bookingUuid); // Service returns entity
        // readByUuidEntity should throw ResourceNotFoundException if not found
        return ResponseEntity.ok(BookingMapper.toDto(bookingEntity));
    }

    /**
     * Admin updates an existing booking.
     */
    @PutMapping("/{bookingUuid}")
    public ResponseEntity<BookingResponseDTO> updateBookingByAdmin(
            @PathVariable UUID bookingUuid,
            @Valid @RequestBody BookingUpdateDTO updateDto
    ) {
        Booking existingBooking = bookingService.read(bookingUuid); // Fetch current entity

        User userEntity = existingBooking.getUser(); // Default to existing
        if (updateDto.getUserUuid() != null && !updateDto.getUserUuid().equals(existingBooking.getUser().getUuid())) {
            userEntity = userService.read(updateDto.getUserUuid());
            if (userEntity == null) throw new ResourceNotFoundException("User for update not found: " + updateDto.getUserUuid());
        }

        Car carEntity = existingBooking.getCar(); // Default to existing
        if (updateDto.getCarUuid() != null && !updateDto.getCarUuid().equals(existingBooking.getCar().getUuid())) {
            carEntity = carService.read(updateDto.getCarUuid());
            if (carEntity == null) throw new ResourceNotFoundException("Car for update not found: " + updateDto.getCarUuid());
        }

        Driver driverEntity = existingBooking.getDriver(); // Default to existing
        if (updateDto.getDriverUuid() != null) {
            if (existingBooking.getDriver() == null || !updateDto.getDriverUuid().equals(existingBooking.getDriver().getUuid())) {
                driverEntity = driverService.read(updateDto.getDriverUuid());
                if (driverEntity == null && updateDto.getDriverUuid() != null) { // Check again if DTO had it but not found
                    throw new ResourceNotFoundException("Driver for update not found: " + updateDto.getDriverUuid());
                }
            }
        } else if (existingBooking.getDriver() != null && updateDto.getDriverUuid() == null && updateDto.getDriverUuid() != null ) { // Explicitly setting driver to null
            driverEntity = null;
        }


        Booking bookingWithUpdates = BookingMapper.applyUpdateDtoToEntity(updateDto, existingBooking, userEntity, carEntity, driverEntity);
        Booking persistedBooking = bookingService.update(bookingWithUpdates); // Service.update takes entity

        return ResponseEntity.ok(BookingMapper.toDto(persistedBooking));
    }

    /**
     * Admin soft-deletes a booking by its UUID.
     */
    @DeleteMapping("/{bookingUuid}")
    public ResponseEntity<Void> deleteBookingByAdmin(@PathVariable UUID bookingUuid) {
        Booking existingBooking = bookingService.read(bookingUuid); // Fetch current entity
        boolean deleted = bookingService.delete(existingBooking.getId()); // Service method for admin
        if (!deleted) {
            // This typically means it wasn't found, service should throw ResourceNotFoundException
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // Endpoints for specific actions like confirm/cancel, if admins can also do this
    // These would mirror the user-facing BookingController but might have different authorization
    @PostMapping("/{bookingUuid}/confirm")
    public ResponseEntity<BookingResponseDTO> confirmBookingByAdmin(@PathVariable UUID bookingUuid) {
        Booking existingBooking = bookingService.read(bookingUuid);
        Booking updatedBooking = new Booking.Builder()
                .copy(existingBooking)
                .setStatus(String.valueOf(BookingStatus.CONFIRMED)) // Assuming Booking has a status enum
                .build();
        existingBooking = updatedBooking;
        existingBooking = bookingService.update(existingBooking);
        BookingResponseDTO confirmedDto = BookingMapper.toDto(existingBooking);
        return ResponseEntity.ok(confirmedDto);
    }

    @PostMapping("/{bookingUuid}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBookingByAdmin(@PathVariable UUID bookingUuid) {
        Booking existingBooking = bookingService.read(bookingUuid);
        Booking updatedBooking = new Booking.Builder()
                .copy(existingBooking)
                .setStatus(String.valueOf(BookingStatus.CANCELED)) // Assuming Booking has a status enum
                .build();

        existingBooking = updatedBooking;
        existingBooking = bookingService.update(existingBooking);
        BookingResponseDTO canceledDto = BookingMapper.toDto(existingBooking);
        return ResponseEntity.ok(canceledDto);
    }
}