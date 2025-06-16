package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.CarCreateDTO;
import za.ac.cput.domain.dto.request.CarUpdateDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.service.IFileStorageService;

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
     * Converts a Car entity to a CarResponseDTO, including generating URLs for its images.
     *
     * @param car                The Car entity to convert.
     * @param fileStorageService The service for generating image URLs.
     * @return A CarResponseDTO.
     */
    public static CarResponseDTO toDto(Car car, IFileStorageService fileStorageService) {
        if (car == null) {
            return null;
        }

        List<String> imageUrls = Collections.emptyList();
        // The service must be provided to generate image URLs.
        if (fileStorageService != null && car.getImages() != null && !car.getImages().isEmpty()) {
            imageUrls = car.getImages().stream()
                    .map(image -> {
                        String key = image.getImageType() + "/" + image.getFileName();
                        return fileStorageService.getUrl(key).toString();
                    })
                    .collect(Collectors.toList());
        }

        return CarResponseDTO.builder()
                .uuid(car.getUuid())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .licensePlate(car.getLicensePlate())
                .category(car.getCategory())
                .priceGroup(car.getPriceGroup())
                .vin(car.getVin())
                .available(car.isAvailable())
                .imageUrls(imageUrls)
                .build();
    }

    /**
     * Converts a list of Car entities to a list of CarResponseDTOs.
     *
     * @param cars               The list of Car entities.
     * @param fileStorageService The service for generating image URLs for each car.
     * @return A list of CarResponseDTOs.
     */
    public static List<CarResponseDTO> toDtoList(List<Car> cars, IFileStorageService fileStorageService) {
        if (cars == null) {
            return Collections.emptyList();
        }
        return cars.stream()
                .map(car -> toDto(car, fileStorageService))
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