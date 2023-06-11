package za.ac.cput.factory;
/**
 * ReservationsFactoryTest.java
 * Class for the Reservations factory test
 * Author: Cwenga Dlova (214310671)
 * Date:  05 April 2023
 */

import org.junit.jupiter.api.Assertions;
import za.ac.cput.domain.impl.Reservations;
import org.junit.jupiter.api.Test;
import za.ac.cput.factory.impl.ReservationsFactory;

class ReservationsFactoryTest {

    @Test
    public void testReservationFactory(){

        ReservationsFactory reservationsFactory = new ReservationsFactory();
        Reservations reservations = reservationsFactory.create();

        Assertions.assertNotNull(reservations);
        Assertions.assertNotNull(reservations.getId());


    }

}