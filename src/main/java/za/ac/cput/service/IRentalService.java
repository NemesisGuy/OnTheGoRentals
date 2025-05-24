package za.ac.cput.service;

/**
 * Author: Peter Buckingham (220165289)
 */

import za.ac.cput.domain.Rental;
import za.ac.cput.domain.security.User;

import java.util.ArrayList;
import java.util.List;

public interface IRentalService extends IService<Rental, Integer> {
    Rental create(Rental rental);

    Rental read(Integer id);

    Rental update(Rental rental);

    boolean delete(Integer id);

    List<Rental> getAll();

    boolean existsById(Integer id);

    List<Rental> getRentalHistoryByUser(User currentUserEntity);

    //s List<Rental> getRentalHistoryByUser(User user);  // Returns a list of rentals
}
