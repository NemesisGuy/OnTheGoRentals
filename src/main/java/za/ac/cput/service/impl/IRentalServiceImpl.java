package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.Rental;
import za.ac.cput.factory.impl.RentalFactory;
import za.ac.cput.repository.IRentalRepository;
import za.ac.cput.service.IRentalService;

import java.util.ArrayList;

@Service("rentalServiceImpl")
public class IRentalServiceImpl implements IRentalService {
    @Autowired
    private IRentalRepository repository;
    @Autowired
    private RentalFactory rentalFactory;

    private IRentalServiceImpl(IRentalRepository repository) {
        this.repository = repository;
    }


    @Override
    public Rental create(Rental rental) {
        Rental newRental = rentalFactory.create(rental);
        return repository.save(newRental);

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
        System.out.println("IRentalServiceImpl.update : ");
        System.out.println("rental Id recived : " + rental.getRentalId());

        if (repository.existsById(rental.getRentalId())) {

            System.out.println("Rental " + rental.getRentalId() + " found");
            System.out.println(rental.toString());
            Rental updatedRental = rentalFactory.create(rental);
            return repository.save(updatedRental);
        }
        System.out.println("Rental " + rental.getRentalId() + " not found");
        return null;
    }


    @Override
    public boolean delete(Integer integer) {

        if (this.repository.existsById(integer)) {
            this.repository.deleteById(integer);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        if (this.repository.existsById(id)) {
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

