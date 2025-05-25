package za.ac.cput.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.dto.request.BookingRequestDTO;
import za.ac.cput.domain.dto.response.BookingResponseDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.mapper.BookingMapper;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.security.User;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IUserService;
import za.ac.cput.service.impl.IBookingServiceImpl;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
// @CrossOrigin(...) // Prefer global CORS
public class BookingController {

    private final IBookingServiceImpl bookingService; // Using Impl directly
    private final ICarService carService;
    private final IUserService userService;
    private final JwtUtilities jwtUtilities;

    @Autowired
    public BookingController(IBookingServiceImpl bookingService, ICarService carService, IUserService userService, JwtUtilities jwtUtilities) {
        this.bookingService = bookingService;
        this.carService = carService;
        this.userService = userService;
        this.jwtUtilities = jwtUtilities;
    }

    /**
     * Creates a new booking.
     * User is inferred from security context. Car is fetched based on carUuid from DTO.
     */
    @PostMapping // Changed from /create to be more RESTful (POST to collection)
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userService.read(userEmail); // Fetches user by email

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Should be handled by security filters
        }
        System.out.println("Car UUID: " + bookingRequestDTO.getCarUuid());
        Car carToBook = carService.read(bookingRequestDTO.getCarUuid());
        if (carToBook == null) {
            throw new ResourceNotFoundException("Car not found with UUID: " + bookingRequestDTO.getCarUuid());
        }
        if (!carToBook.isAvailable()) {
            throw new CarNotAvailableException("Car with UUID: " + carToBook.getUuid() + " is not available for booking.");
        }

        // Map DTO and fetched entities to a new Booking entity
        Booking bookingToCreate = BookingMapper.toEntity(bookingRequestDTO, currentUser, carToBook);
        // Service method createBooking(Booking booking) might set initial status, issuedDate, etc.
        // If not, set them here before calling service.
        // bookingToCreate.setStatus(RentalStatus.PENDING_CONFIRMATION); // Example
        // bookingToCreate.setIssuedDate(LocalDateTime.now()); // Example

        Booking createdBookingEntity = bookingService.createBooking(bookingToCreate); // Call existing service method

        // Update car availability if service doesn't do it
        // carToBook.setAvailable(false);
        // carService.updateCarEntity(carToBook); // Assuming a method that takes Car entity

        return ResponseEntity.status(HttpStatus.CREATED).body(BookingMapper.toDto(createdBookingEntity));
    }

    /**
     * Retrieves a specific booking by its UUID.
     */
    @GetMapping("/{bookingUuid}")
    public ResponseEntity<BookingResponseDTO> getBookingByUuid(@PathVariable UUID bookingUuid) {
        Booking bookingEntity = bookingService.readByUuid(bookingUuid); // Assuming service has readByUuid
        if (bookingEntity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(BookingMapper.toDto(bookingEntity));
    }

    /**
     * Retrieves all bookings for the currently authenticated user.
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDTO>> getCurrentUserBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userService.read(userEmail);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Existing service method takes integer user ID
        List<Booking> bookings = bookingService.getUserBookings(currentUser.getId());
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(BookingMapper.toDtoList(bookings));
    }

    /**
     * Updates an existing booking.
     * Path variable (UUID) identifies the booking. Request DTO contains update data.
     */
    @PutMapping("/{bookingUuid}")
    public ResponseEntity<BookingResponseDTO> updateBooking(
            @PathVariable UUID bookingUuid,
            @Valid @RequestBody BookingRequestDTO bookingRequestDTO // Or a more specific BookingUpdateDTO
    ) {
        Booking existingBooking = bookingService.read(bookingUuid); // Fetch by UUID
        if (existingBooking == null) {
            return ResponseEntity.notFound().build();
        }

        // Determine if car is being changed
        Car carForUpdate = existingBooking.getCar();
        if (bookingRequestDTO.getCarUuid() != null && !bookingRequestDTO.getCarUuid().equals(existingBooking.getCar().getUuid())) {
            carForUpdate = carService.read(bookingRequestDTO.getCarUuid());
            if (carForUpdate == null) {
                throw new ResourceNotFoundException("New car for update not found with UUID: " + bookingRequestDTO.getCarUuid());
            }
            if (!carForUpdate.isAvailable() && !carForUpdate.getUuid().equals(existingBooking.getCar().getUuid())) {
                throw new CarNotAvailableException("Selected new car is not available.");
            }
        }

        // Apply changes from DTO to the existingBooking entity
        // BookingMapper.updateEntityFromDto(bookingRequestDTO, existingBooking, carForUpdate);
        // Or manually:

        // Start building a new Booking entity based on existing one

        Booking.Builder builder = new Booking.Builder().copy(existingBooking);


        if (bookingRequestDTO.getBookingStartDate() != null) {
            builder.setStartDate(bookingRequestDTO.getBookingStartDate());
        }
        if (bookingRequestDTO.getBookingEndDate() != null) {
            builder.setEndDate(bookingRequestDTO.getBookingEndDate());
        }
        builder.setCar(carForUpdate); // Set the potentially updated car
        // existingBooking.setStatus(...); // If status can be updated via this DTO
        existingBooking = builder.build(); // Build the updated Booking entity
        Booking updatedBookingEntity = bookingService.update(existingBooking); // Call existing service method
        return ResponseEntity.ok(BookingMapper.toDto(updatedBookingEntity));
    }

    /**
     * Confirms a booking.
     */
    @PostMapping("/{bookingUuid}/confirm")
    public ResponseEntity<BookingResponseDTO> confirmBooking(@PathVariable UUID bookingUuid) {
        Booking bookingToConfirm = bookingService.readByUuid(bookingUuid); // Fetch by UUID
        if (bookingToConfirm == null) {
            return ResponseEntity.notFound().build();
        }
        // Call existing service method which takes integer ID
        Booking confirmedBookingEntity = bookingService.confirmBooking(bookingToConfirm.getId());
        return ResponseEntity.ok(BookingMapper.toDto(confirmedBookingEntity));
    }

    /**
     * Cancels a booking.
     */
    @PostMapping("/{bookingUuid}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable UUID bookingUuid) {
        Booking bookingToCancel = bookingService.readByUuid(bookingUuid); // Fetch by UUID
        if (bookingToCancel == null) {
            return ResponseEntity.notFound().build();
        }
        // Call existing service method which takes integer ID
        Booking canceledBookingEntity = bookingService.cancelBooking(bookingToCancel.getId());
        return ResponseEntity.ok(BookingMapper.toDto(canceledBookingEntity));
    }


    // --- Helper Endpoints ---

    @GetMapping("/available-cars")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsForBooking() {
        List<Car> cars = carService.findAllAvailableAndNonDeleted();
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(cars));
    }

    @GetMapping("/user-profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfileForBooking() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.read(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found for email: " + userEmail);
        }
        return ResponseEntity.ok(UserMapper.toDto(user));
    }
}