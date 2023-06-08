package za.ac.cput.backend.controllers;
/**
 *  CarController.java
 *  This is the controller for the Car class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */
import org.springframework.web.bind.annotation.CrossOrigin;
import za.ac.cput.domain.Car;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.PriceGroup;

import java.util.ArrayList;
import java.util.List;

@RestController
//set url
public class CarController {
    private ArrayList<Car> createCarList() {
        ArrayList<Car> cars = new ArrayList<>();

        cars.add(Car.builder()
                .id(1233)
                .make("Toyota")
                .model("Corolla")
                .year(2022)
                .category("Sedan")
                .licensePlate("ABC1233")
                .priceGroup(PriceGroup.ECONOMY)
                .build());

        Car car1 = Car.builder()
                .id(123)
                .make("Toyota")
                .model("Corolla")
                .year(2021)
                .category("Sedan")
                .licensePlate("ABC123")
                .priceGroup(PriceGroup.ECONOMY)
                .build();

        Car car2 = Car.builder()
                .id(234)
                .make("Ford")
                .model("Mustang")
                .year(2022)
                .category("Sports")
                .licensePlate("DEF456")
                .priceGroup(PriceGroup.LUXURY)
                .build();

        Car car3 = Car.builder()
                .id(345)
                .make("Honda")
                .model("Civic")
                .year(2020)
                .category("Sedan")
                .priceGroup(PriceGroup.STANDARD)
                .licensePlate("GHI789")
                .build();

        Car car4 = Car.builder()
                .id(456)
                .make("BMW")
                .model("X5")
                .year(2021)
                .category("SUV")
                .priceGroup(PriceGroup.LUXURY)
                .licensePlate("JKL012")
                .build();

        Car car5 = Car.builder()
                .id(567)
                .make("Chevrolet")
                .model("Camaro")
                .year(2023)
                .category("Sports")
                .priceGroup(PriceGroup.PREMIUM)
                .licensePlate("MNO345")
                .build();

        Car car6 = Car.builder()
                .id(2235)
                .make("Lexsis")
                .model("LFA")
                .year(2023)
                .category("Super")
                .priceGroup(PriceGroup.EXOTIC)
                .licensePlate("ZARN789")
                .build();

        Car car7 = Car.builder()
                .id(5623)
                .make("Hynudai")
                .model("i10 Grand")
                .year(2023)
                .category("Hatchback")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("XNO587")
                .build();
        Car car8 = Car.builder()
                .id(159)
                .make("Opel")
                .model("Corsa")
                .year(2023)
                .category("Hatchback")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("PNO987")
                .build();
        Car car9 = Car.builder()
                .id(2965)
                .make("Mazda")
                .model("3")
                .year(2023)
                .category("Hatchback")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("ZARN559")
                .build();
        Car car10 = Car.builder()
                .id(2965)
                .make("Haval")
                .model("H2")
                .year(2023)
                .category("SUV")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("SAMK021")
                .build();

        cars.add(car1);
        cars.add(car2);
        cars.add(car3);
        cars.add(car4);
        cars.add(car5);
        cars.add(car6);
        cars.add(car7);
        cars.add(car8);
        cars.add(car9);
        cars.add(car10);// Add more cars to the list...

        return cars;
    }
    //All cars
    @GetMapping("/api/cars/all")
    public List<Car> getCars() {
        return createCarList();
    }

    //Economy
    @GetMapping("/api/cars/economy")
    public List<Car> getEconomyCars() {
        ArrayList<Car> economyCars = createCarList();
        economyCars.removeIf(car -> car.getPriceGroup() != PriceGroup.ECONOMY);
        return economyCars;
    }
    //Luxury
    @GetMapping("/api/cars/luxury")
    public List<Car> getLuxuryCars() {
        ArrayList<Car> luxuryCars = createCarList();
        luxuryCars.removeIf(car -> car.getPriceGroup() != PriceGroup.LUXURY);
        return luxuryCars;
    }



    @GetMapping("/api/cars/special")
    public List<Car> getSpecialCars() {
        ArrayList<Car> specialCars = createCarList();
        specialCars.removeIf(car -> car.getPriceGroup() != PriceGroup.SPECIAL);
        return specialCars;

    }
}
