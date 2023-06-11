package za.ac.cput.repository.impl;
/**
 * ICarRepository.java
 * Interface for the ICarRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;
import za.ac.cput.repository.ICarRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ICarRepositoryImpl implements ICarRepository {
    private List<Car> cars;
    private static ICarRepositoryImpl repository = null;

    public ICarRepositoryImpl() {

        cars = createCarList();

    }

    public static ICarRepositoryImpl getRepository() {
        if (repository == null)
            repository = new ICarRepositoryImpl();
        return repository;
    }

    @Override
    public Car create(Car entity) {
        if (cars.add(entity)) {
            return entity;
        }
        return null;
    }

    @Override
    public Car read(Integer id) {
        return cars.stream()
                .filter(car -> car.getId() == id)
                .findFirst()
                .orElse(null);
    }


    @Override
    public Car update(Car entity) {
        Car carToUpdate = read(entity.getId());
        System.out.println("car to update: " + carToUpdate.toString());

        if (carToUpdate != null) {
            cars.remove(carToUpdate);
            cars.add(entity);
            return entity;
        }

        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Car carToDelete = read(id);

        if (carToDelete != null) {
            cars.remove(carToDelete);
            return true;
        }

        return false;
    }

    @Override
    public List<Car> getAllCars() {

        return this.cars;
    }

    @Override
    public List<Car> getCarsByCategory(String category) {
        return cars.stream()
                .filter(car -> car.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> getCarsByPriceGroup(PriceGroup priceGroup) {
        return cars.stream()
                .filter(car -> car.getCategory().equalsIgnoreCase(priceGroup.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public Car getCarById(Integer id) {

        return read(id);
    }

    private ArrayList<Car> createCarList() {
        ArrayList<Car> cars = new ArrayList<>();

        cars.add(Car.builder()
                .id(1)
                .make("Nissan")
                .model("350Z")
                .year(2022)
                .category("Sedan")
                .licensePlate("ABC123456")
                .priceGroup(PriceGroup.SPECIAL)
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
                .priceGroup(PriceGroup.SPECIAL)
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
                .id(69420)
                .make("Haval")
                .model("H2")
                .year(2023)
                .category("SUV")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("SAMK021")
                .build();
        Car car11 = Car.builder()
                .id(4523)
                .make("Mercedes")
                .model("AMG GT")
                .year(2023)
                .category("Sports")
                .priceGroup(PriceGroup.SPECIAL)
                .licensePlate("SAMK021")
                .build();
        Car car12 = Car.builder()
                .id(555)
                .make("Wolkswagen")
                .model("Polo")
                .year(2023)
                .category("Hatchback")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("OTGR025")
                .build();
        Car car13 = Car.builder()
                .id(553)
                .make("Kia")
                .model("Picanto")
                .year(2023)
                .category("Hatchback")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("OTGR026")
                .build();
        Car car14 = Car.builder()
                .id(559)
                .make("Renault")
                .model("Kwid")
                .year(2023)
                .category("Hatchback")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("OTGR027")
                .build();
        Car car15 = Car.builder()
                .id(560)
                .make("Suzuki")
                .model("Swift")
                .year(2023)
                .category("Hatchback")
                .priceGroup(PriceGroup.ECONOMY)
                .licensePlate("OTGR028")
                .build();
        Car car16 = Car.builder()
                .id(561)
                .make("Maserati")
                .model("GranCabrio")
                .year(2023)
                .category("Sedan")
                .priceGroup(PriceGroup.LUXURY)
                .licensePlate("OTGR031")
                .build();
        Car car17 = Car.builder()
                .id(562)
                .make("Mercedes")
                .model("S600")
                .year(2023)
                .category("Sedan")
                .priceGroup(PriceGroup.LUXURY)
                .licensePlate("OTGR032")
                .build();
        Car car18 = Car.builder()
                .id(563)
                .make("BMW")
                .model("M5")
                .year(2023)
                .category("Sedan")
                .priceGroup(PriceGroup.LUXURY)
                .licensePlate("OTGR033")
                .build();
        Car car19 = Car.builder()
                .id(564)
                .make("Audi")
                .model("R8")
                .year(2023)
                .category("Sports")
                .priceGroup(PriceGroup.SPECIAL)
                .licensePlate("OTGR034")
                .build();
        Car car20 = Car.builder()
                .id(565)
                .make("Jaguar")
                .model("F-Type")
                .year(2023)
                .category("Cabriolet")
                .priceGroup(PriceGroup.LUXURY)
                .licensePlate("OTGR035")
                .build();

// Add more cars to the list...
        cars.add(car1);
        cars.add(car2);
        cars.add(car3);
        cars.add(car4);
        cars.add(car5);
        cars.add(car6);
        cars.add(car7);
        cars.add(car8);
        cars.add(car9);
        cars.add(car10);
        cars.add(car11);
        cars.add(car12);
        cars.add(car13);
        cars.add(car14);
        cars.add(car15);
        cars.add(car16);
        cars.add(car17);
        cars.add(car18);
        cars.add(car19);
        cars.add(car20);

        return cars;
    }
}