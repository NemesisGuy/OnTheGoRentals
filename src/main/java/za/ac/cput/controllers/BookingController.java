package za.ac.cput.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car; // Make sure you import the Car class
import za.ac.cput.domain.security.User;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IUserService;
import za.ac.cput.service.impl.BookingServiceImpl;
import za.ac.cput.service.ICarService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class BookingController {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ICarService IcarService;

    // Include the jwtUtilities and userService dependencies
    @Autowired
    private JwtUtilities jwtUtilities;
    @Autowired
    private IUserService iUserService;

    @PostMapping("/admin/create")
    public Booking createBooking(@RequestBody Booking booking, HttpServletRequest request) {
        // Check if the user is authenticated with a valid token
        String token = jwtUtilities.getToken(request);
        if (token != null && jwtUtilities.validateToken(token)) {
            String userEmail = jwtUtilities.extractUsername(token);

            // Get the user by email and associate it with the booking
            User user = iUserService.read(Integer.valueOf(userEmail));
            if (user != null) {
                booking.setUser(user);
                // Implement your logic to create a booking here using bookingService
                // return bookingService.create(booking);
                return booking;
            } else {
                // Handle the case where the user doesn't exist
                return null;
            }
        } else {
            // Handle the case where the token is invalid or not present
            // You might want to return an error response or throw an exception
            return null; // Modify this to suit your needs
        }
    }
    @GetMapping("/cars/all")
    public List<Car> getAllCars() {
        List<Car> cars = IcarService.getAll();
        return cars;
    }

    @GetMapping("/profile")
    public User getUserProfile(HttpServletRequest request) {
        // Include the jwtUtilities and userService logic here
        String token = jwtUtilities.getToken(request);

        if (token != null && jwtUtilities.validateToken(token)) {
            String userEmail = jwtUtilities.extractUsername(token);

            return iUserService.read(Integer.valueOf(userEmail));
        } else {
            // Handle the case where the token is invalid or not present
            // You might want to return an error response or throw an exception
            return null; // Modify this to suit your needs
        }
    }

    @PutMapping("/update/{bookingId}")
    public Booking updateBooking(@PathVariable Integer bookingId, @RequestBody Booking booking) {
        // Implement your logic to update a booking here using bookingService
        // return bookingService.updateById(bookingId, booking);
        return booking;
    }
}
