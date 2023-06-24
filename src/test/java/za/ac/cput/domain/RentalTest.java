package za.ac.cput.domain;

import za.ac.cput.domain.impl.Rental;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class RentalTest {


    private int rentalId;
    private int userId;
    private int carId;
    private int rentalIssuer;
    private LocalDateTime rentalIssuedDate;
    private LocalDateTime rentalDateReturned;
    private int rentalReceiver;

    public void testRental() {

        Rental rental = new Rental.RentalBuilder()
                .setRentalId(rentalId)
                .setBorrower(userId)
                .setCar(carId)
                .setIssuer(rentalIssuer)
                .setIssuedDate(rentalIssuedDate)
                .setDateReturned(rentalDateReturned)
                .setReceiver(rentalReceiver)
                .build();

        System.out.println(rental.toString());

        assertEquals(rentalId, rental.getRentalId());
        assertEquals(userId, rental.getBorrower());
        assertEquals(carId, rental.getCar());
        assertEquals(rentalIssuer, rental.getIssuer());
        assertEquals(rentalIssuedDate, rental.getIssuedDate());

        assertEquals(rentalReceiver, rental.getReceiver());
    }
}
