package za.ac.cput.service.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.PriceGroup;
import za.ac.cput.repository.ICarRepository;
import za.ac.cput.repository.ICarRepositoryImpl;
import za.ac.cput.service.ICarService;

import java.util.ArrayList;

public class ICarServiceImpl implements ICarService {
    private  ICarServiceImpl service = null;
    private static ICarRepositoryImpl repository = null;


    private ArrayList<Car> cars;

    public ICarServiceImpl() {
        repository = ICarRepositoryImpl.getRepository();
       // this.cars = createCarList();
    }


    public ICarServiceImpl getService() {
        if (service == null) {
            service = new ICarServiceImpl();
        }
        return service;
    }


    /* @Override ///this was recursive lol
    public  Car create(Car car) {
        ICarServiceImpl service = new ICarServiceImpl();
        Car car1 = service.create(car);
        return car1;
    }*/
   @Override

   public Car create(Car car) {
       return repository.create(car);
   }

    @Override
    public Car read(Integer integer) {
        return null;
    }

    @Override
    public Car read(int id) {
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public ArrayList<Car> getAll() {

        return (ArrayList<Car>) repository.getAllCars();
      // return null;
    }


}
