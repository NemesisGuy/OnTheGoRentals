package za.ac.cput.factory;
/**
 * ReservationsFactory.java
 * Class for the Reservations Factory
 * Author: Cwenga Dlova (214310671)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.Reservations;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ReservationsFactory implements IFactory<Reservations>{

    @Override
    public Reservations create(){
        return new Reservations.Builder()
        .setId(new Random().nextInt(1000000))
                .build();
    }

    @Override
    public Reservations getById(long id) {
        return null;
    }

    @Override
    public Reservations update(Reservations entity) {
        return null;
    }

    @Override
    public boolean delete(Reservations entity) {
        return false;
    }

    @Override
    public List<Reservations> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Reservations> getType() {
        return null;
    }
}