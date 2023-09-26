package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.service.BookingService;

import java.util.List;

/**
 * BookingController.java
 * Author: Lonwabo Magazi-218331851
 * Date: September 2023
 */

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/list/all")
    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return bookings;
    }

    @GetMapping("/list/user/{userId}")
    public List<Booking> getBookingsByUser(@PathVariable("userId") int userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return bookings;
    }

    @PostMapping("/create")
    public Booking createBooking(@RequestBody Booking booking) {
        Booking newBooking = bookingService.createBooking(booking);
        return newBooking;
    }

    @PutMapping("/update")
    public Booking updateBooking(@RequestBody Booking booking) {
        Booking updatedBooking = bookingService.updateBooking(booking);
        return updatedBooking;
    }

    @DeleteMapping("/delete/{bookingId}")
    public void deleteBooking(@PathVariable("bookingId") int bookingId) {
        bookingService.deleteBooking(bookingId);
    }
}