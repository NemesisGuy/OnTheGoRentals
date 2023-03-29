package za.ac.cput.factory;

import za.ac.cput.domain.Car;

import java.util.List;
import java.util.UUID;

public class CarFactory implements IFactory<Car> {

    @Override
    public Car create() {
        // implement logic to create a new Car object
        return Car.builder()
                .id(UUID.randomUUID().toString())
                .build();

    }

    @Override
    public Car getById(long id) {
        // implement logic to retrieve a Car object by ID
        return null;
    }

    @Override
    public Car update(Car entity) {
        // implement logic to update a Car object
        return null;
    }

    @Override
    public boolean delete(Car entity) {
        // implement logic to delete a Car object
        return false;
    }

    @Override
    public List<Car> getAll() {
        // implement logic to retrieve all Car objects
        return null;
    }

    @Override
    public long count() {
        // implement logic to count the number of Car objects
        return 0;
    }

    @Override
    public Class<Car> getType() {
        return Car.class;
    }
}

