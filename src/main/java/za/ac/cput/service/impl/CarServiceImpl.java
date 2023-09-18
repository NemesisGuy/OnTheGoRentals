package za.ac.cput.service.impl;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Car;
import za.ac.cput.factory.impl.CarFactory;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.service.ICarService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (this.repository.existsById((int) car.getId())) {
            Car updatedCar = carFactory.create(car);
            return this.repository.save(updatedCar);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
            return true;
        }

        return false;

    }

    @Override
    public ArrayList<Car> getAll() {

        ArrayList<Car> all = (ArrayList<Car>) this.repository.findAll();
        return all;


    }



}
