package za.ac.cput.repository;
/**
 * ICarRepository.java
 * Interface for the ICarRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import za.ac.cput.domain.Car;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ICarRepositoryImpl implements ICarRepository {
    private List<Car> cars;

    public ICarRepositoryImpl() {
        cars = new ArrayList<>();
    }

    @Override
    public Car create(Car entity) {
        cars.add(entity);
        return entity;
    }

    @Override
    public Car read(Integer id) {
        return cars.stream()
                .filter(car -> car.getId()==id)
                .findFirst()
                .orElse(null);
    }


    @Override
    public Car update(Car entity) {
        Car carToUpdate = read(entity.getId());

        if (carToUpdate != null) {
            cars.remove(carToUpdate);
            cars.add(entity);
            return entity;
        }

        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Car carToDelete = read(id);

        if (carToDelete != null) {
            cars.remove(carToDelete);
            return true;
        }

        return false;
    }

    @Override
    public List<Car> getAllCars() {

        return Collections.unmodifiableList(cars);
    }

    @Override
    public List<Car> getCarsByCategory(String category) {
        return cars.stream()
                .filter(car -> car.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public Car getCarById(Integer id) {

        return read(id);
    }
}