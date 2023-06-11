package za.ac.cput.service.impl;

import za.ac.cput.domain.impl.Reservations;
import za.ac.cput.repository.impl.ReservationsRepositoryImpl;
import za.ac.cput.service.IReservationsService;

import java.util.List;

public class ReservationsServiceImpl implements IReservationsService {

    private static  ReservationsServiceImpl service = null;
    private  static  ReservationsRepositoryImpl repository= null;

    private ReservationsServiceImpl() {
        repository = ReservationsRepositoryImpl.getRepository();
    }

    public static IReservationsService getService(){
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
