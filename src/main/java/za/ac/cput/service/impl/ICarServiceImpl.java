package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.repository.ICarRepository;
import za.ac.cput.service.ICarService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Service("carServiceImpl")
public class ICarServiceImpl implements ICarService {

    private  ICarRepository repository = null;


    public ICarServiceImpl(ICarRepository repository) {

        this.repository = repository;
    }



    @Override
    public Car read(Integer integer) {

        Optional<Car> optionalCar = this.repository.findById(integer);
        return optionalCar.orElse(null);
    }

    @Override
    public Car create(Car car) {
        return this.repository.save(car);

    }

    @Override
    public Car read(int id) {
        //optional
        Optional<Car> optionalCar = this.repository.findById(id);
        return optionalCar.orElse(null);
    }

    @Override
    public Car update(Car car) {
        if(this.repository.existsById(car.getId())) {
           return this.repository.save(car);
        }

        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        if (this.repository.existsById(integer)) {
            this.repository.deleteById(integer);
            return true;
        }

        return false ;
    }

    @Override
    public boolean delete(int id) {
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
