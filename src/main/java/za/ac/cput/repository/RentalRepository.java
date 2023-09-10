package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.Rental;

import java.util.List;
import java.util.Optional;

/**
 * Peter Buckingham (220169289)
 * Date: March 2023
 * RentalRepository.java
 */

public interface RentalRepository extends JpaRepository<Rental, Integer> {


    Optional<Rental> findAllByCarId(int id);
    Optional<Rental> findTopByCarIdOrderByReturnedDateDesc(int id);

    List<Rental> findByUserIdAndReturnedDateIsNull(int id);


}

