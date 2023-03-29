package za.ac.cput.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void testCarConstructor() {
        String id = "1234";
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
}