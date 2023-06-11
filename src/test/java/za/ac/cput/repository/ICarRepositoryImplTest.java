package za.ac.cput.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import za.ac.cput.domain.impl.Car;
import za.ac.cput.repository.impl.ICarRepositoryImpl;

import java.util.List;

class ICarRepositoryImplTest {
    private ICarRepository repository;
    private Car car1, car2;

    @BeforeEach
    void setUp() {
        repository = new ICarRepositoryImpl();
        car1 = Car.builder()
                .id(1)
                .make("Toyota")
                .model("Corolla")
                .year(2019)
                .category("Sedan")
                .licensePlate("ABC123GP")
                .build();
        car2 = Car.builder()
                .id(2)
                .make("Ford")
                .model("Mustang")
                .year(2020)
                .category("Sports Car")
                .licensePlate("DEF456GP")
                .build();
    }

    @Test
    void create() {
        Car createdCar = repository.create(car1);
        assertNotNull(createdCar);
        assertEquals(car1, createdCar);
        assertEquals(1, repository.getAllCars().size());
    }

    @Test
    void read() {
        repository.create(car1);
        Car readCar = repository.read(1);
        assertNotNull(readCar);
        assertEquals(car1, readCar);
    }

    @Test
    void update() {
        repository.create(car1);
        Car updatedCar = Car.builder()
                .id(1)
                .make("Toyota")
                .model("Corolla")
                .year(2021)
                .category("Sedan")
                .licensePlate("ABC123GP")
                .build();
        Car returnedCar = repository.update(updatedCar);
        assertNotNull(returnedCar);
        assertEquals(updatedCar, returnedCar);
    }

    @Test
    void delete() {
        repository.create(car1);
        boolean deleteResult = repository.delete(1);
        assertTrue(deleteResult);
        assertEquals(0, repository.getAllCars().size());
    }

    @Test
    void getAllCars() {
        repository.create(car1);
        repository.create(car2);
        List<Car> allCars = repository.getAllCars();
        assertNotNull(allCars);
        assertEquals(2, allCars.size());
        assertTrue(allCars.contains(car1));
        assertTrue(allCars.contains(car2));
    }

    @Test
    void getCarsByCategory() {
        repository.create(car1);
        repository.create(car2);
        List<Car> carsByCategory = repository.getCarsByCategory("Sedan");
        assertNotNull(carsByCategory);
        assertEquals(1, carsByCategory.size());
        assertTrue(carsByCategory.contains(car1));
    }

    @Test
    void getCarById() {
        repository.create(car1);
        Car carById = repository.getCarById(1);
        assertNotNull(carById);
        assertEquals(car1, carById);
    }
}
