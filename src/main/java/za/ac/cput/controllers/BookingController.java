package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.service.impl.BookingServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class BookingController {

    @Autowired
    private BookingServiceImpl bookingService;

    @PostMapping("/admin/create")
    public Booking createBooking(@RequestBody Booking booking) {
        return booking;
    }

    @GetMapping("/read/{bookingId}")
    public Booking readBooking(@PathVariable Integer bookingId) {
        return bookingService.read(bookingId);
    }

    @PutMapping("/update/{bookingId}")
    public Booking updateBooking(@PathVariable Integer bookingId, @RequestBody Booking booking) {
        return bookingService.updateById(bookingId, booking);
    }

    @DeleteMapping("/delete/{bookingId}")
    public boolean deleteBooking(@PathVariable Integer bookingId) {
        return bookingService.delete(bookingId);
    }
}
