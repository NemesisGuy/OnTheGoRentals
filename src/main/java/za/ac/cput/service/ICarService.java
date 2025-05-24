package za.ac.cput.service;
/**
 * Author: Peter Buckingham (220165289)
 */

import jakarta.validation.constraints.NotNull;
import za.ac.cput.domain.Car;

import java.util.List;
import java.util.UUID;

public interface ICarService extends IService<Car, Integer> {
    Car create(Car car);

    Car read(Integer id);

    Car update(Car car);

    boolean delete(Integer id);

    List<Car> getAll();

    List<Car> getAllAvailableCars();

    Car read(@NotNull(message = "Car UUID cannot be null") UUID carUuid);

    List<Car> findAllAvailableAndNonDeleted();
}
