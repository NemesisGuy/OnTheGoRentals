/*
package za.ac.cput.factory.impl;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.security.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingFactoryTest {

    @Test
    public void createBookingTest() {
        int id = 1;
        User user = new User(); // You can create a User object here
        Car car = new Car(); // You can create a Car object here
        LocalDateTime bookingDate = LocalDateTime.now();
        LocalDateTime returnedDate = LocalDateTime.now().plusDays(1);

        Booking booking = BookingFactory.createBooking(id, user, car, bookingDate, returnedDate);

        assertNotNull(booking);
        assertEquals(id, booking.getId());
        assertEquals(user, booking.getUser());
        assertEquals(car, booking.getCar());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(returnedDate, booking.getReturnedDate());
    }
}
*/
