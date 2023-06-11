package za.ac.cput.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.repository.impl.ICarRepositoryImpl;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void testCarConstructor() {
        int id = 1234;
        String make = "Toyota";
        String model = "Corolla";
        String category = "Sedan";
        int year = 2021;

        Car car = new Car.Builder()
                .id(id)
                .make(make)
                .model(model)
                .category(category)
                .year(year)
                .build();

        Assertions.assertEquals(id, car.getId());
        Assertions.assertEquals(make, car.getMake());
        Assertions.assertEquals(model, car.getModel());
        Assertions.assertEquals(category, car.getCategory());
        Assertions.assertEquals(year, car.getYear());
    }
        @Test
        public void testBuilder() {
            Car car = Car.builder()
                    .id(123)
                    .make("Toyota")
                    .model("Corolla")
                    .year(2021)
                    .category("Sedan")
                    .licensePlate("ABC123")
                    .build();

            Assertions.assertEquals(123, car.getId());
            Assertions.assertEquals("Toyota", car.getMake());
            Assertions.assertEquals("Corolla", car.getModel());
            Assertions.assertEquals(2021, car.getYear());
            Assertions.assertEquals("Sedan", car.getCategory());
            Assertions.assertEquals("ABC123", car.getLicensePlate());
        }

        @Test
        public void testEquals() {
            Car car1 = Car.builder()
                    .id(123)
                    .make("Toyota")
                    .model("Corolla")
                    .year(2021)
                    .category("Sedan")
                    .licensePlate("ABC123")
                    .build();

            Car car2 = Car.builder()
                    .id(123)
                    .make("Toyota")
                    .model("Corolla")
                    .year(2021)
                    .category("Sedan")
                    .licensePlate("ABC123")
                    .build();

            Car car3 = Car.builder()
                    .id(456)
                    .make("Honda")
                    .model("Civic")
                    .year(2022)
                    .category("Sedan")
                    .licensePlate("DEF456")
                    .build();

            Assertions.assertEquals(car1, car2);
            Assertions.assertNotEquals(car1, car3);
        }

        @Test
        public void testHashCode() {
            Car car1 = Car.builder()
                    .id(123)
                    .make("Toyota")
                    .model("Corolla")
                    .year(2021)
                    .category("Sedan")
                    .licensePlate("ABC123")
                    .build();

            Car car2 = Car.builder()
                    .id(123)
                    .make("Toyota")
                    .model("Corolla")
                    .year(2021)
                    .category("Sedan")
                    .licensePlate("ABC123")
                    .build();

            Car car3 = Car.builder()
                    .id(456)
                    .make("Honda")
                    .model("Civic")
                    .year(2022)
                    .category("Sedan")
                    .licensePlate("DEF456")
                    .build();

            Assertions.assertEquals(car1.hashCode(), car2.hashCode());
            Assertions.assertNotEquals(car1.hashCode(), car3.hashCode());
        }
    @Test
    void testGetAllCarsExecutionTime() {
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            ICarRepositoryImpl carRepository = new ICarRepositoryImpl();
            List<Car> cars = carRepository.getAllCars();
            Car carCorolla = new Car.Builder().id(1).make("Toyota").model("Corolla").year(2020).category("Sedan").licensePlate("CA123456").build();
            Car carCorsa = new Car.Builder().id(2).make("Opel").model("Corsa").year(2022).category("Hatch").licensePlate("CA123469").build();
            carRepository.create(carCorolla);
            carRepository.create(carCorsa);
            assertNotNull(cars);
            assertTrue(cars.size() > 0);
        });
    }
    }
