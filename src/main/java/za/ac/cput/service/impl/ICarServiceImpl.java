package za.ac.cput.service.impl;

import za.ac.cput.domain.impl.Car;
import za.ac.cput.repository.ICarRepository;
import za.ac.cput.repository.impl.ICarRepositoryImpl;
import za.ac.cput.service.ICarService;

import java.util.ArrayList;

public class ICarServiceImpl implements ICarService {
    private  ICarServiceImpl service = null;
    private static ICarRepositoryImpl repository = null;
    private ArrayList<Car> cars;

    public ICarServiceImpl() {
        repository = ICarRepositoryImpl.getRepository();
    }

    public ICarServiceImpl(ICarRepositoryImpl repository) {
        this.repository = repository;
    }

    public ICarServiceImpl(ICarRepository carRepository) {
        this.repository = repository;
    }

    public ICarRepositoryImpl getRepository() {
        if (repository == null) {
            repository = ICarRepositoryImpl.getRepository();
        }
        return repository;
    }

    public ICarServiceImpl getService() {
        if (service == null) {
            service = new ICarServiceImpl();
        }
        return service;
    }

   @Override
   public Car create(Car car) {

       return repository.create(car);
   }

    @Override
    public Car read(Integer integer) {

        return repository.read(integer);
    }

    @Override
    public Car read(int id) {

        return repository.read(id);
    }

    @Override
    public Car update(Car car) {

        return repository.update(car);
    }

    @Override
    public boolean delete(Integer integer) {

       return repository.delete(integer);
    }


    @Override
    public boolean delete(int id) {

        return repository.delete(id);

    }

    @Override
    public ArrayList<Car> getAll() {

        return (ArrayList<Car>) repository.getAllCars();

    }


}
