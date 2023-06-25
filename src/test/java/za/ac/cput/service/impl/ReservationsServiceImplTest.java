package za.ac.cput.service.impl;

/**
 * ReservationsServiceImplTest.java
 * Class for the Reservations service test
 * Author: Cwenga Dlova (214310671)
 * Date:  11 June 2023
 */

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.impl.Reservations;
import za.ac.cput.factory.impl.ReservationsFactory;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest

class ReservationsServiceImplTest {


    @Autowired

    private  ReservationsServiceImpl service;

    private  Reservations reservation = ReservationsFactory.createReservations(11,"Cape Town Airport", (LocalDate.parse("2023-06-01")), (Time.valueOf(LocalTime.of(12, 00))), "Cape Town", (LocalDate.parse("2023-06-07")),(Time.valueOf(LocalTime.of(15, 00))) );

    @Test
    public void a_testCreate() {
        Reservations created = service.create(reservation);
        assertEquals(reservation.getId(), created.getId());
        System.out.println("Created: " + created);

    }
    @Test
    public void b_testUpdate() {
        Reservations updated = new Reservations.Builder().copy(reservation)
                .setPickUpDate(LocalDate.parse("2023-06-02"))
                .setPickUpTime(Time.valueOf(LocalTime.of(12,00)))
                .setReturnDate(LocalDate.parse("2023-06-08"))
                .setReturnTme(Time.valueOf(LocalTime.of(15,30)))
                .build();
        System.out.println("Updated: " + updated);
        assertNotEquals(reservation, updated);
    }
    @Test
    public void c_testGetAll() {
        List<Reservations> list = service.getAll();
        System.out.println("\nShow all: ");
        for (Reservations reservations : list) {
            System.out.println(reservations);
        }
        assertNotNull(reservation);
    }


}