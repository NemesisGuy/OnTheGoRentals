package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;
import za.ac.cput.domain.impl.User;
import za.ac.cput.service.impl.ICarServiceImpl;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/cars")
public class AdminCarController {
    @Autowired
    private ICarServiceImpl carService;

    @PostMapping("/create")
    public Car createCar(@RequestBody Car car) {
        System.out.println("/api/admin/cars/create was triggered");
        car.setPriceGroup(mapPriceGroup(car.getPriceGroupString()));
        System.out.println("PriceGroup was set to: " + car.getPriceGroup());
        System.out.println("CarService was created...attempting to create car...");
        Car createdCar = carService.create(car);
        return createdCar;
    }

    @PutMapping("/update/{carId}")
    public Car updateCar(@PathVariable int carId, @RequestBody Car updatedCar) {
        Car updated = carService.update(updatedCar);
        return updated;
    }

    @DeleteMapping("/delete/{carId}")
    public boolean deleteCar(@PathVariable Integer carId) {
        System.out.println("/api/admin/cars/delete was triggered");
        System.out.println("CarService was created...attempting to delete car...");
        return carService.delete(carId);
    }

    private PriceGroup mapPriceGroup(String priceGroupString) {
        try {
            return PriceGroup.valueOf(priceGroupString);
        } catch (IllegalArgumentException e) {
            return PriceGroup.NONE;
        }
    }


}
//.delete(`http://localhost:8080/api/admin/cars/delete/${carId}`)