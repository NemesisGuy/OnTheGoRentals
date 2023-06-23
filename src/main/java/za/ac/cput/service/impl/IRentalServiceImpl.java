package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.Rental;
import za.ac.cput.domain.impl.User;
import za.ac.cput.repository.IRentalRepository;
import za.ac.cput.service.IRentalService;

import java.util.ArrayList;

@Service("rentalServiceImpl")
public class IRentalServiceImpl implements IRentalService {
    private IRentalRepository repository = null;

    private IRentalServiceImpl(IRentalRepository repository) {
        this.repository = repository;
    }


    @Override
    public Rental create(Rental rental) {
        return null;
    }

    @Override
    public Rental read(Integer integer) {
        return null;
    }

    @Override
    public Rental read(int id) {
        return null;
    }

    @Override
    public Rental update(Rental rental) {
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public ArrayList<Rental> getAll() {

        return (ArrayList<Rental>) this.repository.findAll();
    }
}