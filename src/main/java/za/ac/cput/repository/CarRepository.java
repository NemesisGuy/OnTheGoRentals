package za.ac.cput.repository;
/**
 * CarRepository.java
 * Interface for the CarRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
//O R M

public interface CarRepository extends JpaRepository<Car, Integer> {

    List<Car> findByPriceGroupAndDeletedFalse(PriceGroup priceGroup);
    //List<Car> findAvailableCars();
    /// is car by id available
    boolean existsByIdAndAvailableTrue(int id);
    boolean existsByIdAndAvailableTrueAndDeletedFalse(int id);
    List<Car> findByDeletedFalse();
    List<Car> findByDeletedTrue();
    Optional<Car> findByUuidAndDeletedFalse(UUID id);
    Optional<Car> findByUuid(UUID uuid);
    List<Car> findAllByAvailableTrueAndDeletedFalse();
    List<Car> findAllByAvailableTrueAndDeletedFalseAndCategory(String category);
    List<Car> findAllByAvailableTrueAndDeletedFalseAndPriceGroup(PriceGroup priceGroup);
    Optional<Car> findByIdAndDeletedFalse(Integer id);
}