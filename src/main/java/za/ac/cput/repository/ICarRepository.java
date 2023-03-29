package za.ac.cput.repository;
/**
 * IRepository.java
 * Interface for the IRepository
 * Author: Peter Buckingham (220165289)
 * Date: 17 March 2021
 */

import za.ac.cput.domain.Car;

import java.util.List;

public interface ICarRepository extends IRepository<Car, String> {

    List<Car> getAllCars();

    List<Car> getCarsByCategory(String category);

    Car getCarById(String id);
}