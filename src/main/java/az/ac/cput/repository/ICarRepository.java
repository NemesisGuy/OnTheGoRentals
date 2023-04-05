package az.ac.cput.repository;
/**
 * IRepository.java
 * Interface for the IRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import az.ac.cput.domain.Car;

import java.util.List;

public interface ICarRepository extends IRepository<Car, Integer> {

    Car read(Integer id);

    List<Car> getAllCars();

    List<Car> getCarsByCategory(String category);

    Car getCarById(Integer id);
}