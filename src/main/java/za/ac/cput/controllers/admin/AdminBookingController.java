package za.ac.cput.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.dto.response.BookingResponseDTO;
import za.ac.cput.domain.mapper.BookingMapper;
import za.ac.cput.service.impl.IBookingServiceImpl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    @Autowired
    private IBookingServiceImpl bookingService;

    @GetMapping("/list/all")
    public ResponseEntity<List<BookingResponseDTO>> getAll() {
        List<Booking> bookings = bookingService.getAll();
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<BookingResponseDTO> dtos = bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/create")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody Booking booking) {
        Booking newBooking = bookingService.create(booking);
        return ResponseEntity.ok(BookingMapper.toDto(newBooking));
    }

    @GetMapping("/read/{bookingId}")
    public ResponseEntity<BookingResponseDTO> readBooking(@PathVariable UUID bookingId) {
        Booking bookingFromUuid = bookingService.readByUuid(bookingId);
        Booking booking = bookingService.read(bookingFromUuid.getId());
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(BookingMapper.toDto(booking));
    }

    @PutMapping("/update/{bookingId}")
    public ResponseEntity<BookingResponseDTO> updateBooking(@PathVariable Integer bookingId,
                                                    @RequestBody Booking updatedBooking) {
        updatedBooking.setId(bookingId);
        Booking updated = bookingService.update(updatedBooking);
        return ResponseEntity.ok(BookingMapper.toDto(updated));
    }

    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer bookingId) {
        boolean deleted = bookingService.delete(bookingId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
