package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.Rental;

import java.util.List;
import java.util.Optional;

/**
 * Author: Peter Buckingham (220165289)
 * Date: March 2023
 * RentalRepository.java
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {


    Optional<Rental> findAllByCarId(int id);
    Optional<Rental> findTopByCarIdOrderByReturnedDateDesc(int id);

    List<Rental> findByUserIdAndReturnedDateIsNull(int id);


}

