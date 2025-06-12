package za.ac.cput.factory.impl;
/**
 * Author: Peter Buckingham (220169289)
 * Date: 10 June 2023
 * File: CarFactory.java
 */

import org.springframework.stereotype.Component;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.factory.IFactory;


@Component
public class CarFactory implements IFactory<Car> {


    public Car createCar(int id, String make, String model, int year, String category, PriceGroup priceGroup, String licensePlate, boolean available) {
        return new Car.Builder()

                .setId(id)
                .setMake(make)
                .setModel(model)
                .setYear(year)
                .setCategory(category)
                .setPriceGroup(priceGroup)
                .setLicensePlate(licensePlate)
                .setAvailable(available)
                .build();
    }


    @Override
    public Car create() {
        return new Car.Builder().build();
    }


    public Car create(Car car) {
        return new Car.Builder()
                .copy(car)
                .build();
    }
}

