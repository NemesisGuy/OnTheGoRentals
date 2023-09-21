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
import za.ac.cput.domain.PriceGroup;

import java.util.List;


public interface CarRepository extends JpaRepository<Car, Integer> {


    List<Car> findByPriceGroupAndRentalsReturnedDateIsNotNull(PriceGroup priceGroup);

    //List<Car> findAvailableCars();
    ///is car by id available
    boolean existsByIdAndIsAvailableIsTrue(int id);

    //set car isavailable to false
    // void setCarToNotAvailable(int id);
//    Car setIsAvailableToFalse(int id);
    //set car isavailable to true
//    Car setIsAvailableToTrue(int id);
    //set car isavailable to false
    @Modifying
    @Query("UPDATE Car c SET c.isAvailable = false WHERE c.id = :id")
    void setIsAvailableToFalse(@Param("id") int id);

    //set car isavailable to true
    @Modifying
    @Query("UPDATE Car c SET c.isAvailable = true WHERE c.id = :id")
    void setIsAvailableToTrue(@Param("id") int id);

}