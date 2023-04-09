package za.ac.cput.repository;
/**
 * IReservationsReposatoryImpTest.java
 * Class for the Reservations repository test
 * Author: Cwenga Dlova (214310671)
 * Date:  07 April 2023
 */
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import za.ac.cput.domain.Reservations;
import za.ac.cput.factory.ReservationsFactory;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IReservationsRepositoryImplTest {

    private static IReservationsRepositoryImpl repository = IReservationsRepositoryImpl.getRepository();

    private static ReservationsFactory repositoryFactory = new ReservationsFactory();
    private static Reservations reservation = repositoryFactory.create();
    private static Reservations reservation2;

    @Test
    public void test_create() {
        Reservations created = repository.create(reservation);
        assertEquals(created.getId(), reservation.getId());
        System.out.println("Created: " + created);

    }
    @Test
    public void test_read(){
        Reservations read = repository.read(reservation.getId());
       // Assertions.assertNotNull(read);
        System.out.println("Read: " + read);
    }
    @Test
    public void test_update() {

        Reservations updated = new Reservations.Builder().copy(reservation)
                .setPickUpLocation("Parklands")
                .setPickUpDate(LocalDate.parse("2023-03-01"))
                .setPickUpTime(Time.valueOf(LocalTime.of(12,00)))
                .build();
        Assertions.assertNotNull(repository.update(updated));
        System.out.println("Updated: " + updated);


    }
    @Test
    public void test_delete() {
        boolean success = repository.delete(reservation.getId());
        //assertTrue(success);
        System.out.println("Deleted: " + success);

    }
    @Test
    public void test_getAllReservationsMade() {
        reservation2 = new ReservationsFactory().create();
        Reservations created = repository.create(reservation2);

        List<Reservations> list = repository.getAllReservationsMade();
        System.out.println("\nShow all: ");
        for (Reservations reservation : list) {
            System.out.println(reservation);
        }
        assertNotSame(reservation2, reservation);
    }


}