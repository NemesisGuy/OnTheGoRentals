package za.ac.cput.factory;
/**
 * ReservationsFactoryTest.java
 * Class for the Reservations factory test
 * Author: Cwenga Dlova (214310671)
 * Date:  05 April 2023
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Reservations;
import za.ac.cput.factory.impl.ReservationsFactory;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

class ReservationsFactoryTest {

    private Reservations reserve;

    @BeforeEach
    public void startUp() {
        reserve = ReservationsFactory.createReservations(01, ("Cape Town"), LocalDate.parse("2023-02-25"), Time.valueOf(LocalTime.of(10, 00)), "Cape Town", LocalDate.parse("2023-02-28"), Time.valueOf(LocalTime.of(14, 00)));
    }

    @Test
    public void testReservationFactory() {

        Assertions.assertNotNull(reserve);
        Assertions.assertNotNull(reserve.getId());
    }

}