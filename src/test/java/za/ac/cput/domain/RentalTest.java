package za.ac.cput.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RentalTest {

    public void testRental ()  {

        Rental rental = new Rental.RentalBuilder()
                .build();

        rental.setRentalId(Integer.parseInt("218331851"));
        rental.setBorrower(" ");
        rental.setCar("Range Rover");
        rental.setIssuer("Land Rover ");
        rental.setIssuedDate("23/03/2023");
        rental.setDateReturned("23/04/2023 ");
        rental.setReceiver("Asiphe ");

        System.out.println(rental.toString());


        assertEquals("218331851",rental.getRentalId());

        assertEquals("Lonwabo Magazi",rental.getBorrower());

        assertEquals("218331851@mycput.ac.za",rental.getCar());

        assertEquals(" ",rental.getIssuer());
        assertEquals("218331851@mycput.ac.za",rental.getIssuedDate());
        assertEquals("218331851@mycput.ac.za",rental.getDateReturned());
        assertEquals("218331851@mycput.ac.za",rental.getReceiver());
    }


}
