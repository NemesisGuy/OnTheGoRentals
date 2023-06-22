package za.ac.cput.domain;

import za.ac.cput.domain.impl.Rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class RentalTest {


    private int rentalId;
    private int userId;
    private int carId;
    private String rentalIssuer;
    private String rentalIssuedDate;
    private String rentalDateReturned;
    private String rentalReceiver;

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
        assertEquals(rentalDateReturned, rental.getDateReturned());
        assertEquals(rentalReceiver, rental.getReceiver());
    }
}
