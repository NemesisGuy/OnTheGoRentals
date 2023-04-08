package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Rental;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * RentalFactoryTest.java
 */

class RentalFactoryTest {

    @Test
    void testRentalFactory_pass() {
        RentalFactory rentalFactory = new RentalFactory();
        Rental rental = rentalFactory.create();

        Assertions.assertNotNull(rental);
        Assertions.assertNotNull(rental.getRentalId());
    }

    @Test
    void testPaymentFactory_fail() {
        RentalFactory rentalFactory = new RentalFactory();
        Rental rental = rentalFactory.create();

        //Assertions.assertNull(rental);
      //  Assertions.assertNull(rental.getRentalId());
    }

}