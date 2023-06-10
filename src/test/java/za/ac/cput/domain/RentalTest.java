package za.ac.cput.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RentalTest {
    
    private int rentalId;
    private String rentalBorrower;
    private String rentalCar;
    private String rentalIssuer;
    private String rentalIssuedDate;
    private String rentalDateReturned;
    private String rentalReceiver;

    public void testRental ()  {

        Rental rental = new Rental.RentalBuilder()
                .setRentalId(rentalId)
                .setBorrower(rentalBorrower)
                .setCar(rentalCar)
                .setIssuer(rentalIssuer)
                .setIssuedDate(rentalIssuedDate)
                .setDateReturned(rentalDateReturned)
                .setReceiver(rentalReceiver)
                .build();

        System.out.println(rental.toString());

        assertEquals(rentalId,rental.getRentalId());
        assertEquals(rentalBorrower,rental.getBorrower());
        assertEquals(rentalCar,rental.getCar());
        assertEquals(rentalIssuer,rental.getIssuer());
        assertEquals(rentalIssuedDate,rental.getIssuedDate());
        assertEquals(rentalDateReturned,rental.getDateReturned());
        assertEquals(rentalReceiver,rental.getReceiver());
    }
}
