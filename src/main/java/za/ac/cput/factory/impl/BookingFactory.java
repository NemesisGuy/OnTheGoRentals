package za.ac.cput.factory.impl;

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;

public class BookingFactory {
    public static Booking createBooking(int id, User user, Car car, LocalDateTime startDate, LocalDateTime endDate, double totalPrice, boolean confirmed) {
        return new Booking.Builder()
                .id(id)
                .user(user)
                .car(car)
                .startDate(startDate)
                .endDate(endDate)
                .totalPrice(totalPrice)
                .confirmed(confirmed)
                .build();
    }

}
