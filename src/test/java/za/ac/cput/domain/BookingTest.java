package za.ac.cput.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {

    private int id;
    private User user;
    private Car car;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double totalPrice;
    private boolean confirmed;

    @Test
    public void testBooking() {
        Booking booking = new Booking.Builder()
                .id(id)
                .user(user)
                .car(car)
                .startDate(startDate)
                .endDate(endDate)
                .totalPrice(totalPrice)
                .confirmed(confirmed)
                .build();

        assertEquals(id, booking.getId());
        assertEquals(user, booking.getUser());
        assertEquals(car, booking.getCar());
        assertEquals(startDate, booking.getStartDate());
        assertEquals(endDate, booking.getEndDate());
        assertEquals(totalPrice, booking.getTotalPrice());
        assertEquals(confirmed, booking.isConfirmed());
    }
}
