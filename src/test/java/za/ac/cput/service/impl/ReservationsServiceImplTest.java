package za.ac.cput.service.impl;

/**
 * ReservationsServiceTest.java
 * Class for the Reservations service test
 * Author: Cwenga Dlova (214310671)
 * Date:  11 June 2023
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Reservations;
import za.ac.cput.factory.impl.ReservationsFactory;
import za.ac.cput.repository.impl.ReservationsRepositoryImpl;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationsServiceImplTest {

    private static ReservationsRepositoryImpl repository = ReservationsRepositoryImpl.getRepository();

    private static ReservationsFactory repositoryFactory = new ReservationsFactory();
    private static Reservations reservation = repositoryFactory.create();
    private static Reservations reservation2;

    @Test
    public void test_1() {
        Reservations created = repository.create(reservation);
        assertEquals(created.getId(), reservation.getId());
        System.out.println("Created: " + created);

    }
    @Test
    public void test_2() {
        Reservations updated = new Reservations.Builder().copy(reservation)
                .setPickUpLocation("Salt River")
                .setPickUpDate(LocalDate.parse("2023-05-01"))
                .setPickUpTime(Time.valueOf(LocalTime.of(12,00)))
                .build();
        Assertions.assertNotNull(repository.update(updated));
        System.out.println("Updated: " + updated);
    }
    @Test
    public void test_3() {
        List<Reservations> list = repository.getAllReservationsMade();
        System.out.println("\nShow all: ");
        for (Reservations reservation : list) {
            System.out.println(reservation);
        }
        assertNotNull(reservation);
    }


}