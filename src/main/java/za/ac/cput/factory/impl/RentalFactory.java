package za.ac.cput.factory.impl;

import za.ac.cput.domain.impl.Rental;

import java.util.List;
import java.util.Random;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * RentalFactory Class.java
 */

public class RentalFactory {

        public Rental create() {
            return new Rental.RentalBuilder()
                    .setRentalId(new Random().nextInt(1000000))
                    .build();
        }

    public Rental getById(long id) {
        return null;
    }


    public Rental update(Rental entity) {
        return null;
    }


    public boolean delete(Rental entity) {
        return false;
    }


    public List<Rental> getAll() {
        return null;
    }


    public long count() {
        return 0;
    }


    public Class<Rental> getType() {
        return null;
    }

}

