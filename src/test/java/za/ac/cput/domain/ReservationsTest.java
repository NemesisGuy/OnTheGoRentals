package za.ac.cput.domain;

/**
 * ReservationsTest.java
 * Class for the Reservations test
 * Author: Cwenga Dlova (214310671)
 * Date:  01 April 2023
 */
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Reservations;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationsTest {

    @Test
    public void testReservations(){
        Reservations reservation = new Reservations.Builder()
                .setId(7896)
                .setPickUpLocation("Cape Town Airport")
                .setPickUpDate(LocalDate.parse("2023-04-06"))
                .setPickUpTime(Time.valueOf(LocalTime.parse("10:00")))
                .setReturnLocation("Cape Town Airport")
                .setReturnDate(LocalDate.parse("2023-04-10"))
                .setReturnTme(Time.valueOf(LocalTime.parse("15:00")))
                .build();

        System.out.println(reservation.toString());

    }
    @Test
    public void identityTest() {

        Reservations reservation1= new Reservations.Builder()
                .setId(7896)
                .setPickUpLocation("Cape Town Airport")
                .setPickUpDate(LocalDate.parse("2023-04-06"))
                .setPickUpTime(Time.valueOf(LocalTime.parse("10:00")))
                .setReturnLocation("Cape Town Airport")
                .setReturnDate(LocalDate.parse("2023-04-10"))
                .setReturnTme(Time.valueOf(LocalTime.parse("15:00")))
                .build();

        Reservations reservation2 = new Reservations.Builder()
                .setId(1234)
                .setPickUpLocation("Claremont")
                .setPickUpDate(LocalDate.parse("2023-04-06"))
                .setPickUpTime(Time.valueOf(LocalTime.parse("13:30")))
                .setReturnLocation("Cape Town Airport")
                .setReturnDate(LocalDate.parse("2023-04-10"))
                .setReturnTme(Time.valueOf(LocalTime.parse("17:00")))
                .build();

        assertNotSame(reservation2, reservation1);
    }


}