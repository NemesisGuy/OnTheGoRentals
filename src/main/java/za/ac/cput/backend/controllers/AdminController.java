package za.ac.cput.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.PriceGroup;
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
}
