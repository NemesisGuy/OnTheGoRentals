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

        return this.repository.save(rental);
    }

    @Override
    public Rental read(Integer integer) {
      return this.repository.findById(integer).orElse(null);
    }


    @Override
    public Rental read(int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public Rental update(Rental rental) {
        return this.repository.save(rental);
    }

    @Override
    public boolean delete(Integer integer) {

        if (this.repository.existsById(integer))
        {
            this.repository.deleteById(integer);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        if (this.repository.existsById(id))
        {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Rental> getAll() {

        return (ArrayList<Rental>) this.repository.findAll();
    }
}