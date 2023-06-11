package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;
import za.ac.cput.factory.impl.CarFactory;

class CarFactoryTest {
    @Test
    void testCreateCar() {
        CarFactory factory = new CarFactory();
        Car car = factory.createCar(1, "Toyota", "Corolla", 2022, "Compact", PriceGroup.ECONOMY ,"ABC 123 GP");

        Assertions.assertNotNull(car);
        Assertions.assertEquals(1, car.getId());
        Assertions.assertEquals("Toyota", car.getMake());
        Assertions.assertEquals("Corolla", car.getModel());
        Assertions.assertEquals(2022, car.getYear());
        Assertions.assertEquals("Compact", car.getCategory());
        Assertions.assertEquals("ABC 123 GP", car.getLicensePlate());
    }

}