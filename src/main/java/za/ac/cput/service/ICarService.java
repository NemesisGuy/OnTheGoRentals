package za.ac.cput.service;

import za.ac.cput.domain.impl.Car;

import java.util.ArrayList;

public interface ICarService extends IService<Car, Integer> {
    Car create(Car car);

    Car read(int id);

    Car update(Car car);

    boolean delete(int id);

    ArrayList<Car> getAll();
}
