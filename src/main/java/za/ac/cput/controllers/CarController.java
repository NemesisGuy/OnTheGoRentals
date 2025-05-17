package za.ac.cput.controllers;
/**
 * CarController.java
 * This is the controller for the Car class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.service.impl.CarServiceImpl;
import za.ac.cput.service.impl.RentalServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
//@CrossOrigin(origins = "http://localhost:5173")
public class CarController {

    @Autowired
    private CarServiceImpl carService;

    @Autowired
    private RentalServiceImpl rentalService;

    @GetMapping("/list/all")
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAll();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/list/economy")
    public ResponseEntity<List<Car>> getEconomyCars() {
        return ResponseEntity.ok(carService.getCarsByPriceGroup(PriceGroup.ECONOMY));
    }

    @GetMapping("/list/luxury")
    public ResponseEntity<List<Car>> getLuxuryCars() {
        return ResponseEntity.ok(carService.getCarsByPriceGroup(PriceGroup.LUXURY));
    }

    @GetMapping("/list/special")
    public ResponseEntity<List<Car>> getSpecialCars() {
        return ResponseEntity.ok(carService.getCarsByPriceGroup(PriceGroup.SPECIAL));
    }

    @GetMapping("/list/available/all")
    public ResponseEntity<List<Car>> getAvailableCars() {
        List<Car> cars = carService.getAll();
        cars.removeIf(car -> !rentalService.isCarAvailableByCarId(car));
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/list/available/economy")
    public ResponseEntity<List<Car>> getAvailableEconomyCars() {
        return ResponseEntity.ok(carService.getAvailableCarsByPrice(PriceGroup.ECONOMY));
    }

    @GetMapping("/list/available/luxury")
    public ResponseEntity<List<Car>> getAvailableLuxuryCars() {
        return ResponseEntity.ok(rentalService.getAvailableCarsByPrice(PriceGroup.LUXURY));
    }

    @GetMapping("/list/available/special")
    public ResponseEntity<List<Car>> getAvailableSpecialCars() {
        return ResponseEntity.ok(carService.getAvailableCarsByPrice(PriceGroup.SPECIAL));
    }

    @GetMapping("/read/{carId}")
    public ResponseEntity<Car> readCar(@PathVariable Integer carId) {
        Car car = carService.read(carId);
        if (car == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(car);
    }
}
