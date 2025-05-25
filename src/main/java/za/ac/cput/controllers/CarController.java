package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.service.impl.CarServiceImpl;
import za.ac.cput.service.impl.RentalServiceImpl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarServiceImpl carService;
    private final RentalServiceImpl rentalService;

    @Autowired
    public CarController(CarServiceImpl carService, RentalServiceImpl rentalService) {
        this.carService = carService;
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        List<Car> cars = carService.getAll();
        List<CarResponseDTO> carDTOs = cars.stream().map(CarMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(carDTOs);
    }

    @GetMapping("/price-group/{group}")
    public ResponseEntity<List<CarResponseDTO>> getCarsByPriceGroup(@PathVariable PriceGroup group) {
        List<Car> cars = carService.getCarsByPriceGroup(group);
        List<CarResponseDTO> carDTOs = cars.stream().map(CarMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(carDTOs);
    }

    @GetMapping("/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCars() {
        List<Car> availableCars = carService.getAll().stream()
                .filter(car -> car.isAvailable() && !car.isDeleted())
                .collect(Collectors.toList());
        List<CarResponseDTO> carDTOs = availableCars.stream().map(CarMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(carDTOs);
    }

    @GetMapping("/available/price-group/{group}")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsByPriceGroup(@PathVariable PriceGroup group) {
        List<Car> cars = carService.getAvailableCarsByPrice(group);
        List<CarResponseDTO> carDTOs = cars.stream().map(CarMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(carDTOs);
    }

    @GetMapping("/{carId}")
    public ResponseEntity<CarResponseDTO> getCarById(@PathVariable UUID carId) {
        Car car = carService.readByUuid(carId);
        if (car == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CarMapper.toDto(car));
    }
}
