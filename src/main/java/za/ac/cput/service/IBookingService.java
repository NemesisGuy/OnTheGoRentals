package za.ac.cput.service;

import za.ac.cput.domain.Booking;

import java.util.List;

public interface IBookingService {
    Booking create(Booking booking);

    Booking read(int bookingId);

    Booking updateById(Booking updatedBooking);

    boolean delete(int bookingId);

    List<Booking> getAll();

    Booking getBookingById(int bookingId);

    Booking update(Booking booking);

}
