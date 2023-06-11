package za.ac.cput.repository.impl;
/**
 * IReservstionsReposatoryImpl.java
 * Class for the Reservations repository
 * Author: Cwenga Dlova (214310671)
 * Date:  07 April 2023
 */
import za.ac.cput.domain.impl.Reservations;
import za.ac.cput.repository.IReservationsReposatory;

import java.util.ArrayList;
import java.util.List;

public class IReservationsRepositoryImpl implements IReservationsReposatory {

    private List<Reservations> reservationsDB;

    private static IReservationsRepositoryImpl repository = null;

    private IReservationsRepositoryImpl(){
        reservationsDB = new ArrayList<>();
    }

    public static IReservationsRepositoryImpl getRepository() {
        if (repository == null) {
            repository = new IReservationsRepositoryImpl();
        }
        return repository;
    }

    @Override
    public Reservations create(Reservations reservations) {
        reservationsDB.add(reservations);
        return reservations;
    }

    @Override
    public Reservations read(Integer id) {

        Reservations reservations = reservationsDB.stream().filter(r -> r.getId() == id).findAny().orElse(null) ;
        return reservations;
    }


    @Override
    public Reservations update(Reservations reservations) {
        Reservations reservations1 = read(reservations.getId());
        if (reservations1 != null) {
            reservationsDB.remove(reservations1);
            reservationsDB.add(reservations);
            return reservations;
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Reservations deleteReservation = read(id);
        if (deleteReservation != null)
            return false;
        reservationsDB.remove(deleteReservation);
        return true;
    }


    @Override
    public List<Reservations> getAllReservationsMade() {
        return reservationsDB;
    }

}
