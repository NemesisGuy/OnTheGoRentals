package za.ac.cput.factory;

import za.ac.cput.domain.Rental;

import java.util.List;
import java.util.Random;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * RentalFactory Class.java
 */

public class RentalFactory implements IFactoryRental {
        @Override
        public Rental create() {
            return new Rental.RentalBuilder()
                    .setRentalId(new Random().nextInt(1000000))
                    .build();
        }
    @Override
    public Rental getById(long id) {
        return null;
    }

    @Override
    public Rental update(Rental entity) {
        return null;
    }

    @Override
    public boolean delete(Rental entity) {
        return false;
    }

    @Override
    public List<Rental> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Rental> getType() {
        return null;
    }
}

