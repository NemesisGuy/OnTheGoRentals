package za.ac.cput.service.impl;
/**
 * Author: Peter Buckingham (220165289)
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.factory.impl.CarFactory;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.service.ICarService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("carServiceImpl")
public class CarServiceImpl implements ICarService {

    @Autowired
    private CarRepository repository;
    @Autowired
    private CarFactory carFactory;


    public CarServiceImpl(CarRepository repository) {

        this.repository = repository;
    }

    @Override
    public Car create(Car car) {
        car = Car.builder().copy(car).deleted(false).available(true).build();

        Car newCar = carFactory.create(car);
        return repository.save(newCar);

    }

    @Override
    public Car read(Integer id) {
        //optional
        Optional<Car> optionalCar = this.repository.findById(id);
        return optionalCar.orElse(null);
    }


    @Override
    public Car update(Car car) {
        if (this.repository.findByUuid(car.getUuid()) != null) {
            Car updatedCar = carFactory.create(car);
            return this.repository.save(updatedCar);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Car car = this.repository.findById(id).orElse(null);
        if (car != null && !car.isDeleted()) {
            car = Car.builder().copy(car).deleted(true).build();
            this.repository.save(car);
            return true;
        }

        return false;

    }public boolean delete(UUID id) {
        Car car = this.repository.findByUuid(id).orElse(null);
        if (car != null && !car.isDeleted()) {
            car = Car.builder().copy(car).deleted(true).build();
            this.repository.save(car);
            return true;
        }

        return false;

    }


    @Override
    public List<Car> getAll() {
        List<Car> allCars =  this.repository.findByDeletedFalse();
        return allCars;
    }

    //get all available cars
    public List<Car> getAllAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        List<Car> cars = getAll();
        for (Car car : cars) {
            if (car.isAvailable()) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    //check if car is available
    public boolean isCarAvailable(Car car) {
        return car.isAvailable();
    }

    //TODO: This is to be used to replace filtering currently done in the controller layer
    public List<Car> getAvailableCarsByPrice(PriceGroup priceGroup) {
        // Join to rentals table and check availability
        // check if car is available
        ArrayList<Car> availableCars = new ArrayList<>(repository.findByPriceGroupAndDeletedFalse(priceGroup));
        for (Car car : availableCars)   //for each car in available cars
        {
            if (!car.isAvailable()) //if car is not available
            {
                availableCars.remove(car); //remove car from available cars
            }
        }
        return availableCars;


    }

    public List<Car> getCarsByPriceGroup(PriceGroup priceGroup) {
        return repository.findByPriceGroupAndDeletedFalse(priceGroup);
    }


    public Car readByUuid(UUID carId) {
        Optional<Car> optionalCar = this.repository.findByUuidAndDeletedFalse(carId);
        return optionalCar.orElse(null);
    }
    @Override
    public Car read(UUID carUuid) {
        Optional<Car> optionalCar = this.repository.findByUuidAndDeletedFalse(carUuid);
        return optionalCar.orElse(null);
    }


    @Override
    public List<Car> findAllAvailableAndNonDeleted() {
        List<Car> allCars = this.repository.findAllByAvailableTrueAndDeletedFalse();
        return allCars;
    }
}
