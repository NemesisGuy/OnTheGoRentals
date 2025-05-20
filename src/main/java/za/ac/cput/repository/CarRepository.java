package za.ac.cput.repository;
/**
 * CarRepository.java
 * Interface for the CarRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.List;
import java.util.Optional;
//O R M

public interface CarRepository extends JpaRepository<Car, Integer> {


    List<Car> findByPriceGroupAndRentalsReturnedDateIsNotNull(PriceGroup priceGroup);
    List<Car> findByPriceGroupAndRentalsReturnedDateIsNotNullAndDeletedFalse(PriceGroup priceGroup);

    List<Car> findByPriceGroupAndRentalsReturnedDateIsNotNullAndAvailableTrue(PriceGroup priceGroup);
    List<Car> findByPriceGroupAndRentalsReturnedDateIsNotNullAndAvailableTrueAndDeletedFalse(PriceGroup priceGroup);

    List<Car> findByPriceGroup(PriceGroup priceGroup);
    List<Car> findByPriceGroupAndDeletedFalse(PriceGroup priceGroup);


    //List<Car> findAvailableCars();

    /// is car by id available
    boolean existsByIdAndAvailableTrue(int id);
    boolean existsByIdAndAvailableTrueAndDeletedFalse(int id);
    List<Car> findByDeletedFalse();
    List<Car> findByDeletedTrue();



}