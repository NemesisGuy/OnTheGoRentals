/*
package za.ac.cput.domain;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.security.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingTest {

    @Test
    public void testBooking() {
        // Create a sample User, Car, and LocalDateTime objects for testing
        User user = new User();
        Car car = new Car();
        LocalDateTime bookingDate = LocalDateTime.now();
        LocalDateTime returnedDate = LocalDateTime.now();

        // Create a Booking object using the Builder
        Booking booking = Booking.builder()
                .user(user)
                .car(car)
                .bookingDate(bookingDate)
                .returnedDate(returnedDate)
                .build();

        // Perform assertions
        assertNotNull(booking);
        assertEquals(user, booking.getUser());
        assertEquals(car, booking.getCar());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(returnedDate, booking.getReturnedDate());
    }
}
*/
