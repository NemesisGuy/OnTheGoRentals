package za.ac.cput.factory.impl;

import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.security.User;

import java.time.LocalDateTime;

public class BookingFactory {

    public static Booking createBooking(int id, User user, Car car, LocalDateTime bookingDate, LocalDateTime returnedDate, String status) {
        return new Booking.Builder()
                .setId(id)
                .setUser(user)
                .setCar(car)
                .setStartDate(bookingDate)
                .setEndDate(returnedDate)
                .setStatus(status)
                .build();
    }
}
