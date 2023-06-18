package za.ac.cput.repository;
/**
 * IReservationsRepository.java
 * interface for the Reservations repository
 * Author: Cwenga Dlova (214310671)
 * Date:  07 April 2023
 */

import za.ac.cput.domain.impl.Reservations;


import java.util.List;

public interface IReservationsRepository extends IRepository<Reservations, Integer> {
    List<Reservations> getAllReservationsMade();


}

