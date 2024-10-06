package za.ac.cput.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.security.User;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.exception.UnauthorisedException;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IUserService;
import za.ac.cput.service.impl.BookingServiceImpl;
import za.ac.cput.service.impl.RentalServiceImpl;
import za.ac.cput.service.impl.UserService;

import java.util.List;

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
    private UserService userService;
    @Autowired
    private RentalServiceImpl rentalServiceImpl;
    @Autowired
    private RentalServiceImpl rentalService;

    // Method to create a booking (using the Booking object)
    @PostMapping("/create")
    public ResponseEntity<Booking> createUserBooking(@RequestBody Booking booking) {
                Booking newBooking = bookingService.createBooking(booking);
                return ResponseEntity.ok(newBooking);

    }

    // Method to confirm a booking
    @PostMapping("/confirm")
    public ResponseEntity<Booking> confirmBooking(@RequestBody Booking booking) {
        // Here, we assume booking contains the ID and necessary data for confirmation
        Booking confirmedBooking = bookingService.confirmBooking(booking.getId());
        return ResponseEntity.ok(confirmedBooking);
    }

    // Method to cancel a booking
    @PostMapping("/cancel")
    public ResponseEntity<Booking> cancelBooking(@RequestBody Booking booking) {
        // Here, we assume booking contains the ID for cancellation
        Booking canceledBooking = bookingService.cancelBooking(booking.getId());
        return ResponseEntity.ok(canceledBooking);
    }

    // Get all bookings by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable int userId) {
        List<Booking> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    // Get all cars (for selecting in booking)
    @GetMapping("/cars/all")
    public List<Car> getAllCars() {
        //return rentalService.getAllAvailableCars();
        return carService.getAllAvailableCars();
    }
    // Get user profile

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String token = jwtUtilities.getToken(request);

        if (token != null && jwtUtilities.validateToken(token)) {
            String userEmail = jwtUtilities.extractUsername(token);
            User user = userService.read(userEmail);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                throw new ResourceNotFoundException("User not found");
            }
        } else {
            throw new UnauthorisedException("Invalid or missing token");
        }
    }

    // Get user profile
   /* @GetMapping("/profile")
    public User getUserProfile(HttpServletRequest request) {
        String token = jwtUtilities.getToken(request);

        if (token != null && jwtUtilities.validateToken(token)) {
            String userEmail = jwtUtilities.extractUsername(token);
           // return userService.read(Integer.valueOf(userEmail));
            return userService.read(userEmail);
        } else {
            throw new UnauthorisedException("Invalid or missing token");
          //  return null; // Unauthorized or invalid token
        }
    }*/
//    @GetMapping("/profile")
//    public User getUserProfile(HttpServletRequest request) {
//        String token = jwtUtilities.getToken(request);
//
//        if (token != null && jwtUtilities.validateToken(token)) {
//            String userEmail = jwtUtilities.extractUsername(token);
//
//
//            return userService.read(userEmail);
//        } else {
//            // Handle case where token is invalid or not present
//            // You might want to return an error response or throw an exception
//            return null; // Modify this to suit your needs
//        }
//    }

    // Method to update a booking
    @PutMapping("/update")
    public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking) {
        Booking updatedBooking = bookingService.updateById(booking);
        return ResponseEntity.ok(updatedBooking);
    }
}
