package za.ac.cput.controllers;
/**
 * CarController.java
 * This is the controller for the Car class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.impl.ICarServiceImpl;
import za.ac.cput.service.impl.IRentalServiceImpl;

import java.util.ArrayList;
import java.util.List;


@RestController


//set url
@CrossOrigin(origins = "http://localhost:5173")
public class CarController {

    @Autowired
    private ICarServiceImpl carService;
    @Autowired
    private IRentalServiceImpl rentalService;


    @GetMapping("/api/cars/list/all")
    public List<Car> getCars() {
        List<Car> allCars = new ArrayList<>(carService.getAll());
        return allCars;
    }

    @GetMapping("/api/cars/list/economy")
    public List<Car> getEconomyCars() {
        List<Car> economyCars = new ArrayList<>(carService.getAll());
        economyCars.removeIf(car -> car.getPriceGroup() != PriceGroup.ECONOMY);
        return economyCars;
    }

    @GetMapping("/api/cars/list/luxury")
    public List<Car> getLuxuryCars() {
        List<Car> luxuryCars = new ArrayList<>(carService.getAll());
        luxuryCars.removeIf(car -> car.getPriceGroup() != PriceGroup.LUXURY);
        return luxuryCars;
    }

    @GetMapping("/api/cars/list/special")
    public List<Car> getSpecialCars() {
        List<Car> specialCars = new ArrayList<>(carService.getAll());
        specialCars.removeIf(car -> car.getPriceGroup() != PriceGroup.SPECIAL);
        return specialCars;
    }
    @GetMapping("/api/cars/list/available/all")
    public List<Car> getAllAvailableCars() {
        List<Car> allCars = new ArrayList<>(carService.getAll());
        allCars.removeIf(car -> !rentalService.isCarAvailableByCarId(car));
        return allCars;
    }

    @GetMapping("/api/cars/list/available/economy")
    public List<Car> getAvailableEconomyCars() {
        List<Car> economyCars = new ArrayList<>(carService.getAll());
        economyCars.removeIf(car -> car.getPriceGroup() != PriceGroup.ECONOMY || !rentalService.isCarAvailableByCarId(car));
        return economyCars;
    }

    @GetMapping("/api/cars/list/available/luxury")
    public List<Car> getAvailableLuxuryCars() {
        List<Car> luxuryCars = new ArrayList<>(carService.getAll());
        luxuryCars.removeIf(car -> car.getPriceGroup() != PriceGroup.LUXURY || !rentalService.isCarAvailableByCarId(car));
        return luxuryCars;
    }

    @GetMapping("/api/cars/list/available/special")
    public List<Car> getAvailableSpecialCars() {
        List<Car> specialCars = new ArrayList<>(carService.getAll());
        specialCars.removeIf(car -> car.getPriceGroup() != PriceGroup.SPECIAL || !rentalService.isCarAvailableByCarId(car));
        return specialCars;
    }


}
