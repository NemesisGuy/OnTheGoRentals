package za.ac.cput.service;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import za.ac.cput.domain.Car;

import java.util.ArrayList;
import java.util.List;

public interface ICarService extends IService<Car, Integer> {
    Car create(Car car);

    Car read(Integer id);

    Car update(Car car);

    boolean delete(Integer id);

    ArrayList<Car> getAll();

    List<Car> getAllAvailableCars();
}
