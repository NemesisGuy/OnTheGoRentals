package za.ac.cput.service.impl;

/**
 * ReservationsServiceImpl.java
 * Class for the Reservations service implementation
 * Author: Cwenga Dlova (214310671)
 * Date:  11 June 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Reservations;
import za.ac.cput.repository.IReservationsRepository;
import za.ac.cput.service.IReservationsService;

import java.util.List;

@Service
public class ReservationsServiceImpl implements IReservationsService {

    @Autowired
    private IReservationsRepository repository;

    private ReservationsServiceImpl(IReservationsRepository repository) {
        this.repository = repository;
    }


    @Override
    public Reservations create(Reservations reservations) {
        return this.repository.save(reservations);
    }

    @Override
    public Reservations update(Reservations reservations) {
        if (this.repository.existsById(reservations.getId()))
            return this.repository.save(reservations);
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Reservations> getAll() {
        return this.repository.findAll();
    }
}
