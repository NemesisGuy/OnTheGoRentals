/*
package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.ac.cput.domain.impl.Car;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ICarServiceImplTest {

    private CarServiceImpl carService;
    @Mock
    private ICarRepositoryImpl carRepository;


*/
/*    @BeforeEach
    void setUp() {
        //  carRepository = Mockito.mock(CarRepository.class);
        MockitoAnnotations.openMocks(this);
        carService = new CarServiceImpl(carRepository);
    }*//*


    @Test
    void create_ValidCar_ReturnsCreatedCar() {
        // Arrange
        Car car = Car.builder().id(1).licensePlate("ABC123").make("Toyota").model("Camry").build();

        when(carRepository.create(any(Car.class))).thenReturn(car);

        // Act
        Car createdCar = carService.create(car);

        // Assert
        assertNotNull(createdCar);
        assertEquals(car.getLicensePlate(), createdCar.getLicensePlate());
        assertEquals(car.getMake(), createdCar.getMake());
        assertEquals(car.getModel(), createdCar.getModel());

        verify(carRepository, times(1)).create(any(Car.class));
    }

    @Test
    void read_ExistingId_ReturnsCar() {
        // Arrange
        int carId = 1;
        Car car = Car.builder().id(1).licensePlate("ABC123").make("Toyota").model("Camry").build();

        when(carRepository.read(carId)).thenReturn(car);

        // Act
        Car foundCar = carService.read(carId);

        // Assert
        assertNotNull(foundCar);
        assertEquals(carId, foundCar.getId());
        assertEquals(car.getLicensePlate(), foundCar.getLicensePlate());
        assertEquals(car.getMake(), foundCar.getMake());
        assertEquals(car.getModel(), foundCar.getModel());

        verify(carRepository, times(1)).read(carId);
    }

    @Test
    void update_ExistingCar_ReturnsUpdatedCar() {
        // Arrange
        Car car = Car.builder().id(1).licensePlate("ABC123").make("Toyota").model("Camry").build();
        Car updatedCar = Car.builder().id(1).licensePlate("XYZ789").make("Honda").model("Accord").build();


        when(carRepository.update(any(Car.class))).thenReturn(updatedCar);

        // Act
        Car result = carService.update(car);

        // Assert
        assertNotNull(result);
        assertEquals(updatedCar.getId(), result.getId());
        assertEquals(updatedCar.getLicensePlate(), result.getLicensePlate());
        assertEquals(updatedCar.getMake(), result.getMake());
        assertEquals(updatedCar.getModel(), result.getModel());

        verify(carRepository, times(1)).update(any(Car.class));
    }

    @Test
    void delete_ExistingId_ReturnsTrue() {
        // Arrange
        int carId = 1;

        when(carRepository.delete(carId)).thenReturn(true);

        // Act
        boolean result = carService.delete(carId);

        // Assert
        assertTrue(result);

        verify(carRepository, times(1)).delete(carId);
    }

    @Test
    void getAll_ReturnsListOfCars() {
        // Arrange
        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).licensePlate("ABC123").make("Toyota").model("Camry").build());
        cars.add(Car.builder().id(2).licensePlate("XYZ789").make("Honda").model("Accord").build());

        when(carRepository.getAllCars()).thenReturn(cars);

        // Act
        List<Car> result = carService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(cars.size(), result.size());
        assertEquals(cars.get(0).getId(), result.get(0).getId());
        assertEquals(cars.get(1).getId(), result.get(1).getId());
        // Assert other properties as needed

        verify(carRepository, times(1)).getAllCars();
    }
}
*/
