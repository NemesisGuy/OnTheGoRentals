package za.ac.cput.repository;
/**
 * IReservationsRepository.java
 * interface for the Reservations repository
 * Author: Cwenga Dlova (214310671)
 * Date:  07 April 2023
 */
import za.ac.cput.domain.Reservations;

import java.util.List;
import java.util.Set;

public interface IReservationsReposatory extends IRepository <Reservations, Integer>{
    List<Reservations> getAllReservationsMade();


}

