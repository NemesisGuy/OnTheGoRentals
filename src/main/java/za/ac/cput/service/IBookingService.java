package za.ac.cput.service;

import za.ac.cput.domain.entity.Booking;

import java.util.List;
import java.util.UUID;

public interface IBookingService {
    Booking create(Booking booking);

    Booking read(int bookingId);
    Booking read(UUID uuid);

    Booking update(Booking booking);

    boolean delete(int bookingId);

    List<Booking> getAll();



}
