package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;
import za.ac.cput.service.impl.ICarServiceImpl;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private ICarServiceImpl carService;

    @PostMapping("/cars/create")
    public Car createCar(@RequestBody Car car) {
        System.out.println("/api/admin/cars/create was triggered");
        car.setPriceGroup(mapPriceGroup(car.getPriceGroupString()));
        System.out.println("PriceGroup was set to: " + car.getPriceGroup());
        System.out.println("CarService was created...attempting to create car...");
        Car createdCar = carService.create(car);
        return createdCar;
    }

    private PriceGroup mapPriceGroup(String priceGroupString) {
        try {
            return PriceGroup.valueOf(priceGroupString);
        } catch (IllegalArgumentException e) {
            return PriceGroup.NONE;
        }
    }

    @DeleteMapping("/cars/delete/{carId}")
    public boolean deleteCar(@PathVariable Integer carId) {
        System.out.println("/api/admin/cars/delete was triggered");
        System.out.println("CarService was created...attempting to delete car...");
        return carService.delete(carId);
    }

    @PutMapping("/cars/update/{carId}")
    public Car updateCar(@PathVariable int carId, @RequestBody Car updatedCar) {
        Car updated = carService.update(updatedCar);
        return updated;
    }


}
//.delete(`http://localhost:8080/api/admin/cars/delete/${carId}`)