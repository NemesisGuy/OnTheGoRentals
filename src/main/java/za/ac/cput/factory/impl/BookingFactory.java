package za.ac.cput.factory.impl;

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.security.User;

import java.time.LocalDateTime;

public class BookingFactory {

    public static Booking createBooking(int id, User user, Car car, LocalDateTime bookingDate, LocalDateTime returnedDate, String status) {
        return new Booking.Builder()
                .id(id)
                .user(user)
                .car(car)
                .bookingDate(bookingDate)
                .returnedDate(returnedDate)
                .status(status)
                .build();
    }
}
