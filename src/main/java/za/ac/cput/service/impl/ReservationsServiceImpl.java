package za.ac.cput.service.impl;

/**
 * ReservationsServiceImpl.java
 * Class for the Reservations service implementation
 * Author: Cwenga Dlova (214310671)
 * Date:  11 June 2023
 */

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.Reservations;
import za.ac.cput.repository.impl.ReservationsRepositoryImpl;
import za.ac.cput.service.IReservationsService;

import java.util.List;
@Service
public class ReservationsServiceImpl implements IReservationsService {

    private static  ReservationsServiceImpl service = null;
    private  static  ReservationsRepositoryImpl repository= null;

    public ReservationsServiceImpl() {
        repository = ReservationsRepositoryImpl.getRepository();
    }

    public static ReservationsServiceImpl getService(){
        if(service == null){
            service = new ReservationsServiceImpl();
        }
        return service;
    }

    @Override
    public Reservations create(Reservations reservations){
        Reservations created = repository.create(reservations);
        return created;
    }

    @Override
    public Reservations update(Reservations reservations) {
        Reservations updated = repository.update(reservations);
        return updated;
    }

    @Override
    public boolean delete(Integer id) {
        boolean success = repository.delete(id);
        return success;
    }

    @Override
    public List<Reservations> getAll() {

        return repository.getAllReservationsMade();
    }
}
