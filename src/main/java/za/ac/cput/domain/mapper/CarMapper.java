package za.ac.cput.domain.mapper;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import za.ac.cput.domain.dto.request.CarCreateDTO;
import za.ac.cput.domain.dto.request.CarUpdateDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.CarImage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CarMapper.java
 * A utility class for mapping between Car domain entities and DTOs.
 * This version is updated to handle a one-to-many relationship with CarImage entities,
 * building a list of full image URLs for the response.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
public class CarMapper {

    /**
     * Converts a {@link Car} entity to a {@link CarResponseDTO}.
     * This method iterates over the car's list of associated {@link CarImage} entities
     * and constructs a full, accessible URL for each one.
     *
     * @param car The {@link Car} entity to convert.
     * @return The resulting {@link CarResponseDTO}, or null if the input is null.
     */
    public static CarResponseDTO toDto(Car car) {
        if (car == null) {
            return null;
        }

        // Base mapping from entity to DTO
        CarResponseDTO dtoBuilder = CarResponseDTO.builder()
                .uuid(car.getUuid())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .category(car.getCategory())
                .priceGroup(car.getPriceGroup())
                .licensePlate(car.getLicensePlate())
                .available(car.isAvailable())
                .vin(car.getVin())
                .build();

        // NEW LOGIC: Build a list of image URLs from the car's images collection
        if (car.getImages() != null && !car.getImages().isEmpty()) {
            List<String> imageUrls = car.getImages().stream()
                    .map(image -> ServletUriComponentsBuilder
                            .fromCurrentContextPath()       // Gets base URL (e.g., http://localhost:8080)
                            .path("/api/v1/files/")         // Appends the file controller's path
                            .path(image.getImageType() + "/") // Appends the folder (e.g., "cars/")
                            .path(image.getFileName())        // Appends the filename
                            .toUriString())
                    .collect(Collectors.toList());

            dtoBuilder.setImageUrls(imageUrls);
        } else {
            // Ensure the list is empty and not null if there are no images
            dtoBuilder.setImageUrls((Collections.emptyList()));
        }

        // Note: The old single imageFileName and imageType fields are no longer mapped from the Car.
        // They should be removed from the CarResponseDTO class for a cleaner API.

        return dtoBuilder;
    }

    /**
     * Converts a list of {@link Car} entities to a list of {@link CarResponseDTO}s.
     *
     * @param cars The list of {@link Car} entities.
     * @return A list of {@link CarResponseDTO}s.
     */
    public static List<CarResponseDTO> toDtoList(List<Car> cars) {
        if (cars == null) {
            return Collections.emptyList();
        }
        return cars.stream()
                .map(CarMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a {@link CarCreateDTO} to a {@link Car} entity.
     *
     * @param createDto The DTO with creation data.
     * @return The resulting {@link Car} entity.
     */
    public static Car toEntity(CarCreateDTO createDto) {
        if (createDto == null) {
            return null;
        }
        // The builder now uses the class-level @Builder annotation from Car
        return new Car.Builder()
                .setMake(createDto.getMake())
                .setModel(createDto.getModel())
                .setYear(createDto.getYear())
                .setCategory(createDto.getCategory())
                .setPriceGroup(createDto.getPriceGroup())
                .setLicensePlate(createDto.getLicensePlate())
                .setVin(createDto.getVin())
                .setAvailable(createDto.getAvailable() != null ? createDto.getAvailable() : true)
                .setDeleted(false)
                .build();
    }

    /**
     * Applies updates from a {@link CarUpdateDTO} to an existing {@link Car} entity.
     * This method uses the Car's own builder pattern with the 'applyTo' method.
     *
     * @param updateDto   The DTO containing the update data.
     * @param existingCar The existing {@link Car} entity to be updated.
     * @return The same existingCar instance, now modified with the new state.
     */
    public static Car applyUpdateDtoToEntity(CarUpdateDTO updateDto, Car existingCar) {
        if (updateDto == null || existingCar == null) {
            throw new IllegalArgumentException("Update DTO and existing Car entity must not be null.");
        }

        // Create a builder initialized with the existing car's state
        Car.Builder builder = new Car.Builder().copy(existingCar);

        // Apply updates from the DTO to the builder
        if (updateDto.getMake() != null) builder.setMake(updateDto.getMake());
        if (updateDto.getModel() != null) builder.setModel(updateDto.getModel());
        if (updateDto.getYear() != null) builder.setYear(updateDto.getYear());
        if (updateDto.getCategory() != null) builder.setCategory(updateDto.getCategory());
        if (updateDto.getPriceGroup() != null) builder.setPriceGroup(updateDto.getPriceGroup());
        if (updateDto.getLicensePlate() != null) builder.setLicensePlate(updateDto.getLicensePlate());
        if (updateDto.getAvailable() != null) builder.setAvailable(updateDto.getAvailable());
        if (updateDto.getVin() != null) builder.setVin(updateDto.getVin());

        // Apply the builder's state back to the managed entity
        return builder.applyTo(existingCar);
    }
}