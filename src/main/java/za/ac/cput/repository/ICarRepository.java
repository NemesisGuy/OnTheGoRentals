package za.ac.cput.repository;
/**
 * ICarRepository.java
 * Interface for the ICarRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import za.ac.cput.domain.Car;
import za.ac.cput.domain.PriceGroup;

import java.util.List;

public interface ICarRepository extends IRepository<Car, Integer> {

    Car create(Car car);
    Car read(Integer id);
    Car update(Car car);
    boolean delete(Integer id);
    List<Car> getAllCars();
    Car getCarById(Integer id);
    List<Car> getCarsByCategory(String category);
    List<Car> getCarsByPriceGroup(PriceGroup priceGroup);







}