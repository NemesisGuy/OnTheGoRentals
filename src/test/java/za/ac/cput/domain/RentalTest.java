package za.ac.cput.domain;

import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.Rental;
import za.ac.cput.domain.impl.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RentalTest {


    private int rentalId;
    private User user;
    private Car car;
    private int rentalIssuer;
    private LocalDateTime rentalIssuedDate;
    private LocalDateTime rentalDateReturned;
    private int rentalReceiver;

    public void testRental() {

        Rental rental = new Rental.Builder()
                .setId(rentalId)
                .setUser(user)
                .setCar(car)
                .setIssuer(rentalIssuer)
                .setIssuedDate(rentalIssuedDate)
                .setDateReturned(rentalDateReturned)
                .setReceiver(rentalReceiver)
                .build();

        System.out.println(rental.toString());

        assertEquals(rentalId, rental.getRentalId());

        assertEquals(car.getId(), rental.getRentalId());
        assertEquals(rentalIssuer, rental.getIssuer());
        assertEquals(rentalIssuedDate, rental.getIssuedDate());

        assertEquals(rentalReceiver, rental.getReceiver());
    }
}
