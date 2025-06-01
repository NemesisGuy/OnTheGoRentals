package za.ac.cput.domain.mapper;

import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.dto.request.CarCreateDTO;
import za.ac.cput.domain.dto.request.CarUpdateDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {

    public static CarResponseDTO toDto(Car car) {
        if (car == null) return null;
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

    public static List<CarResponseDTO> toDtoList(List<Car> cars) {
        if (cars == null) return null;
        return cars.stream().map(CarMapper::toDto).collect(Collectors.toList());
    }

    public static Car toEntity(CarCreateDTO createDto) {
        if (createDto == null) return null;
        Car.Builder builder = new Car.Builder()
                .setMake(createDto.getMake())
                .setModel(createDto.getModel())
                .setYear(createDto.getYear()) // Assumes int in DTO and Builder
                .setCategory(createDto.getCategory())
                .setPriceGroup(createDto.getPriceGroup())
                .setLicensePlate(createDto.getLicensePlate());

        // Handle nullable Boolean for availability
        if (createDto.getAvailable() != null) {
            builder.setAvailable(createDto.getAvailable());
        } else {
            builder.setAvailable(true); // Default to true if not specified in DTO
        }
        // uuid set by @PrePersist, id by DB, deleted defaults to false by entity builder/PrePersist
        builder.setDeleted(false); // Explicitly set default for clarity
        return builder.build();
    }

    public static Car applyUpdateDtoToEntity(CarUpdateDTO updateDto, Car existingCar) {
        if (updateDto == null || existingCar == null) {
            throw new IllegalArgumentException("Update DTO and existing Car entity must not be null.");
        }
        Car.Builder builder = new Car.Builder().copy(existingCar); // Start with existing values

        if (updateDto.getMake() != null) builder.setMake(updateDto.getMake());
        if (updateDto.getModel() != null) builder.setModel(updateDto.getModel());
        if (updateDto.getYear() != null) builder.setYear(updateDto.getYear());
        if (updateDto.getCategory() != null) builder.setCategory(updateDto.getCategory());
        if (updateDto.getPriceGroup() != null) builder.setPriceGroup(updateDto.getPriceGroup());
        if (updateDto.getLicensePlate() != null) builder.setLicensePlate(updateDto.getLicensePlate());
        if (updateDto.getAvailable() != null) builder.setAvailable(updateDto.getAvailable());

        return builder.build(); // Returns a new Car instance with merged data
    }
}