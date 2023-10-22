package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Booking;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.service.BookingService;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Booking create(Booking booking) {
        if (booking.getId() != 0) {
            throw new IllegalArgumentException("ID should not be set for a new entity.");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking read(int id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    public Booking updateById(int bookingId, Booking updatedBooking) {
        Booking existingBooking = bookingRepository.findById(bookingId).orElse(null);

        if (existingBooking != null) {
            existingBooking.setBookingDate(updatedBooking.getBookingDate());
            existingBooking.setReturnedDate(updatedBooking.getReturnedDate());
            // Update other properties as needed

            return bookingRepository.save(existingBooking);
        } else {
            return null; // You can also throw an exception to indicate the resource was not found
        }
    }

    @Override
    public boolean delete(int id) {
        bookingRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getBookingById(int bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    @Override
    public Booking update(Booking booking) {
        return null;
    }



}
