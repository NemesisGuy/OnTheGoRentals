package za.ac.cput.factory;
/**
 * ReservationsFactory.java
 * Class for the Reservations Factory
 * Author: Cwenga Dlova (214310671)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.Reservations;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ReservationsFactory implements IFactory<Reservations>{

    @Override
    public Reservations create(){
        return new Reservations.Builder()
        .setId(new Random().nextInt(1000000))
                .setPickUpLocation("Cape Town")
                .setPickUpDate(LocalDate.parse("2023-02-25"))
                .setPickUpTime(Time.valueOf(LocalTime.of(10,00)))
                .setReturnLocation("Cape Town")
                .setReturnDate(LocalDate.parse("2023-02-28"))
                .setReturnTme(Time.valueOf(LocalTime.of(14,00)))
                .build();
    }


    public Reservations getById(long id) {
        return null;
    }


    public Reservations update(Reservations entity) {
        return null;
    }


    public boolean delete(Reservations entity) {
        return false;
    }


    public List<Reservations> getAll() {
        return null;
    }


    public long count() {
        return 0;
    }


    public Class<Reservations> getType() {
        return null;
    }
}