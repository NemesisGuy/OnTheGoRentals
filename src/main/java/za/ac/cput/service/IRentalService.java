package za.ac.cput.service;

import za.ac.cput.domain.impl.Rental;

import java.util.ArrayList;

public interface IRentalService extends IService <Rental, Integer>{
    Rental create(Rental rental);

    Rental read(int id);

    Rental update(Rental rental);

    boolean delete(int id);
    ArrayList<Rental> getAll();


}
