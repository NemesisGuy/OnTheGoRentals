package za.ac.cput.service;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import za.ac.cput.domain.Rental;

import java.util.ArrayList;

public interface IRentalService extends IService<Rental, Integer> {
    Rental create(Rental rental);

    Rental read(Integer id);

    Rental update(Rental rental);

    boolean delete(Integer id);

    ArrayList<Rental> getAll();

    boolean existsById(Integer id);


}
