package za.ac.cput.service;

import za.ac.cput.domain.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);

    Booking updateBooking(Booking booking);

    void deleteBooking(int bookingId);

    List<Booking> getAllBookings();

    List<Booking> getBookingsByUserId(int userId);

    Booking getBookingById(int bookingId);
}