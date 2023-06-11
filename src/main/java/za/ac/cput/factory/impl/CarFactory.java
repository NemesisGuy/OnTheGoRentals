package za.ac.cput.factory.impl;

import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;
import za.ac.cput.factory.IFactory;


public class CarFactory implements IFactory<Car> {


    public Car createCar(int id, String make, String model, int year, String category, PriceGroup priceGroup, String licensePlate) {
        return Car.builder()
                //generate random number placeholder for id, will probably be replaced by database auto-increment value later
                //.id(new Random().nextInt(1000000))
                .id(id)
                .make(make)
                .model(model)
                .year(year)
                .category(category)
                .priceGroup(priceGroup)
                .licensePlate(licensePlate)
                .build();
    }


    @Override
    public Car create() {
        return Car.builder().build();
    }
}

