package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Rental;

import java.util.List;
import java.util.Optional;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * IRentalRepository.java
 */

public interface IRentalRepository extends JpaRepository<Rental, Integer> {


    Optional<Rental> findAllByCarId(int id);
    Optional<Rental> findTopByCarIdOrderByReturnedDateDesc(int id);

    List<Rental> findByUserIdAndReturnedDateIsNull(int id);
}

