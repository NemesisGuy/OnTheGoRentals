package za.ac.cput.factory;

import za.ac.cput.domain.Car;

import java.util.List;
import java.util.Random;


public class CarFactory implements IFactoryCar {

    @Override
    public Car create() {
        // implement logic to create a new Car object
        return Car.builder()
                //generate random number placeholder for id, will probably be replaced by database auto-increment value later
                .id(new Random().nextInt(1000000))
                .build();

    }


    @Override
    public Car createCar() {
        return Car.builder().build();
    }

    @Override
    public Car createCar(int id, String make, String model, int year, String category, String licensePlate) {
        return Car.builder()
                .id(id)
                .make(make)
                .model(model)
                .year(year)
                .category(category)
                .licensePlate(licensePlate)
                .build();
    }
}

