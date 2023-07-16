package za.ac.cput.repository;
/**
 * IReservationsRepository.java
 * interface for the Reservations repository
 * Author: Cwenga Dlova (214310671)
 * Date:  07 April 2023
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Reservations;

@Repository
public interface IReservationsRepository extends JpaRepository<Reservations, Integer> {


}

