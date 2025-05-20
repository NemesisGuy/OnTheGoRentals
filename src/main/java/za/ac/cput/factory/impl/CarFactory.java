package za.ac.cput.factory.impl;
/**
 * Author: Peter Buckingham (220169289)
 * Date: 10 June 2023
 * File: CarFactory.java
 */

import org.springframework.stereotype.Component;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.factory.IFactory;


@Component
public class CarFactory implements IFactory<Car> {


    public Car createCar(int id, String make, String model, int year, String category, PriceGroup priceGroup, String licensePlate, boolean available) {
        return Car.builder()

                .id(id)
                .make(make)
                .model(model)
                .year(year)
                .category(category)
                .priceGroup(priceGroup)
                .licensePlate(licensePlate)
                .available(available)
                .build();
    }


    @Override
    public Car create() {
        return Car.builder().build();
    }


    public Car create(Car car) {
        return Car.builder()
                .copy(car)
                .build();
    }
}

