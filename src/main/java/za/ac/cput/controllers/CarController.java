package za.ac.cput.controllers;
/**
 *  CarController.java
 *  This is the controller for the Car class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;
import za.ac.cput.service.impl.ICarServiceImpl;

import java.util.ArrayList;
import java.util.List;


@RestController


//set url
@CrossOrigin(origins = "http://localhost:5173")
public class CarController {

    //All cars
  /*  @GetMapping("/api/cars/all")
    public List<Car> getCars() {

        return createCarList();
    }*/
    @Autowired
    private ICarServiceImpl carService;
    @GetMapping("/api/cars/all")
    public List<Car> getCars() {
        List<Car> allCars = new ArrayList<>(carService.getAll());
        return allCars;
    }

    @GetMapping("/api/cars/economy")
    public List<Car> getEconomyCars() {
        List<Car> economyCars = new ArrayList<>(carService.getAll());
        economyCars.removeIf(car -> car.getPriceGroup() != PriceGroup.ECONOMY);
        return economyCars;
    }

    @GetMapping("/api/cars/luxury")
    public List<Car> getLuxuryCars() {
        List<Car> luxuryCars = new ArrayList<>(carService.getAll());
        luxuryCars.removeIf(car -> car.getPriceGroup() != PriceGroup.LUXURY);
        return luxuryCars;
    }

    @GetMapping("/api/cars/special")
    public List<Car> getSpecialCars() {
        List<Car> specialCars = new ArrayList<>(carService.getAll());
        specialCars.removeIf(car -> car.getPriceGroup() != PriceGroup.SPECIAL);
        return specialCars;
    }



}
