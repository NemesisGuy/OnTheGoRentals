package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Car;
import za.ac.cput.domain.dto.request.CarRequestDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


import za.ac.cput.domain.Car; // Your Car Entity

// Import CarCreateDTO, CarUpdateDTO if you have them

import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {

    /**
     * Maps a Car entity to a CarResponseDTO.
     *
     * @param car The Car entity.
     * @return The corresponding CarResponseDTO, or null if the car entity is null.
     */
    public static CarResponseDTO toDto(Car car) {
        if (car == null) {
            return null;
        }

        return CarResponseDTO.builder()
                .uuid(car.getUuid())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .category(car.getCategory())
                .priceGroup(car.getPriceGroup())
                .licensePlate(car.getLicensePlate())
                .available(car.isAvailable())
                .build();
    }

    /**
     * Maps a list of Car entities to a list of CarResponseDTOs.
     *
     * @param cars List of Car entities.
     * @return List of CarResponseDTOs.
     */
    public static List<CarResponseDTO> toDtoList(List<Car> cars) {
        if (cars == null) {
            return null;
        }
        return cars.stream()
                .map(CarMapper::toDto)
                .collect(Collectors.toList());
    }

    // Add toEntity methods if you need to map CarCreateDTO or CarUpdateDTO to Car entity
    // Example for a hypothetical CarCreateDTO:
    /*
    public static Car toEntity(CarCreateDTO createDto) {
        if (createDto == null) return null;
        Car car = new Car();
        // UUID set by @PrePersist
        car.setMake(createDto.getMake());
        car.setModel(createDto.getModel());
        car.setYear(createDto.getYear());
        car.setCategory(createDto.getCategory());
        car.setPriceGroup(createDto.getPriceGroup());
        car.setLicensePlate(createDto.getLicensePlate());
        car.setAvailable(createDto.isAvailable());
        car.setDeleted(false);
        return car;
    }
    */

    // Example for updating an existing car from a hypothetical CarUpdateDTO:
    /*
    public static void updateEntityFromDto(CarUpdateDTO updateDto, Car existingCar) {
        if (updateDto == null || existingCar == null) return;
        // selectively update fields from updateDto to existingCar
        if (updateDto.getMake() != null) existingCar.setMake(updateDto.getMake());
        // ... etc.
        if (updateDto.getAvailable() != null) existingCar.setAvailable(updateDto.getAvailable());
    }
    */
}