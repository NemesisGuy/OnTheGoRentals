package za.ac.cput.service;

import za.ac.cput.domain.impl.Reservations;

import java.util.List;

public interface IReservationsService {

    Reservations create(Reservations reservations);
    Reservations update(Reservations reservations);
    boolean delete(Integer id);
    List<Reservations> getAll();
}
