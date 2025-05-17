package za.ac.cput.controllers.admin;

/**
 * AdminCarController.java
 * Controller for the Car entity (admin only)
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.service.impl.CarServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cars")
public class AdminCarController {

    @Autowired
    private CarServiceImpl carService;

    @GetMapping("/all")
    public ResponseEntity<List<Car>> getCars() {
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cars);
    }

    @PostMapping("/create")
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        car.setPriceGroup(mapPriceGroup(car.getPriceGroupString()));
        Car createdCar = carService.create(car);
        return ResponseEntity.status(201).body(createdCar);
    }

    @GetMapping("/read/{carId}")
    public ResponseEntity<Car> readCar(@PathVariable Integer carId) {
        Car car = carService.read(carId);
        if (car == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(car);
    }

    @PutMapping("/update/{carId}")
    public ResponseEntity<Car> updateCar(@PathVariable int carId, @RequestBody Car updatedCar) {
       // updatedCar.setId(carId);
        Car car = carService.update(updatedCar);
        return ResponseEntity.ok(car);
    }

    @DeleteMapping("/delete/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable Integer carId) {
        boolean deleted = carService.delete(carId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private PriceGroup mapPriceGroup(String priceGroupString) {
        try {
            return PriceGroup.valueOf(priceGroupString.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return PriceGroup.NONE;
        }
    }
}
