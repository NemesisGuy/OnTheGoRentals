package za.ac.cput.factory.impl;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingFactoryTest {

    @Test
    void testCreateBooking() {
        int id = 1;
        User user = new User();
        Car car = new Car();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);
        double totalPrice = 250.0;
        boolean confirmed = true;

        Booking booking = BookingFactory.createBooking(id, user, car, startDate, endDate, totalPrice, confirmed);

        assertEquals(id, booking.getId());
        assertEquals(user, booking.getUser());
        assertEquals(car, booking.getCar());
        assertEquals(startDate, booking.getStartDate());
        assertEquals(endDate, booking.getEndDate());
        assertEquals(totalPrice, booking.getTotalPrice());
        assertEquals(confirmed, booking.isConfirmed());
    }
}
