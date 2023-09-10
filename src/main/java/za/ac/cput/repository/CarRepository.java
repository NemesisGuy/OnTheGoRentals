package za.ac.cput.repository;
/**
 * CarRepository.java
 * Interface for the CarRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.PriceGroup;

import java.util.List;


public interface CarRepository extends JpaRepository<Car, Integer> {


    List<Car> findByPriceGroupAndRentalsReturnedDateIsNotNull(PriceGroup priceGroup);
}