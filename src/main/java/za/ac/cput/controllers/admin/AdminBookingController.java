package za.ac.cput.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.service.impl.BookingServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    @Autowired
    private BookingServiceImpl bookingService;

    @GetMapping("/list/all")
    public List<Booking> getAll() {
        return bookingService.getAll();
    }

    @PostMapping("/create")
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.create(booking);
    }

    @GetMapping("/read/{bookingId}") // Changed the mapping to be distinct
    public Booking readBooking(@PathVariable Integer bookingId) {
        return bookingService.read(bookingId);
    }

    @PutMapping("/update/{bookingId}")
    public Booking updateBooking(@PathVariable Integer bookingId, @RequestBody Booking updatedBooking) {
        return bookingService.update(updatedBooking);
    }


    @DeleteMapping("/admin/delete/{bookingId}")
    public boolean deleteBooking(@PathVariable Integer bookingId) {
        return bookingService.delete(bookingId);
    }
}
