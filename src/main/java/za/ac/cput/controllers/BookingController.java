package za.ac.cput.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.dto.BookingDTO;
import za.ac.cput.domain.dto.UserDTO;
import za.ac.cput.domain.mapper.BookingMapper;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.security.User;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.exception.UnauthorisedException;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.impl.BookingServiceImpl;
import za.ac.cput.service.impl.UserServiceorig;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ICarService carService;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private UserServiceorig userService;

    @PostMapping("/create")
    public ResponseEntity<BookingDTO> createUserBooking(@RequestBody Booking booking) {
        Booking newBooking = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookingMapper.toDto(newBooking));
    }

    @PostMapping("/confirm")
    public ResponseEntity<BookingDTO> confirmBooking(@RequestBody Booking booking) {
        Booking confirmedBooking = bookingService.confirmBooking(booking.getId());
        return ResponseEntity.ok(BookingMapper.toDto(confirmedBooking));
    }

    @PostMapping("/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@RequestBody Booking booking) {
        Booking canceledBooking = bookingService.cancelBooking(booking.getId());
        return ResponseEntity.ok(BookingMapper.toDto(canceledBooking));
    }

    @PutMapping("/update")
    public ResponseEntity<BookingDTO> updateBooking(@RequestBody Booking booking) {
        Booking updatedBooking = bookingService.updateById(booking);
        return ResponseEntity.ok(BookingMapper.toDto(updatedBooking));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable int userId) {
        List<Booking> bookings = bookingService.getUserBookings(userId);
        if (bookings == null || bookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingDTOs);
    }

    @GetMapping("/cars/all")
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllAvailableCars();
        if (cars == null || cars.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest request) {
        String token = jwtUtilities.getToken(request);

        if (token == null || !jwtUtilities.validateToken(token)) {
            throw new UnauthorisedException("Invalid or missing token");
        }

        String userEmail = jwtUtilities.extractUserEmail(token);
        User user = userService.read(userEmail);

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        return ResponseEntity.ok(UserMapper.toDto(user));
    }
}
