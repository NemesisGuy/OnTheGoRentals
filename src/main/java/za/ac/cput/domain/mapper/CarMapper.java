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
 * A stateless utility class for mapping between Car domain entities and DTOs.
 * The `toDto` methods require the public API URL to correctly resolve image URLs.
 *
 * @author Peter Buckingham (220165289)
 * @version 3.0
 */
public class CarMapper {

    /**
     * Converts a Car entity to a CarResponseDTO, including generating fully qualified URLs for its images.
     *
     * @param car                The Car entity to convert.
     * @param fileStorageService The service for file operations (can be null if not needed).
     * @param publicApiUrl       The base public URL of the API (e.g., "https://otgrapi.nemesisnet.co.za").
     * @return A CarResponseDTO.
     */
    public static CarResponseDTO toDto(Car car, IFileStorageService fileStorageService, String publicApiUrl) {
        if (car == null) {
            return null;
        }

        List<String> imageUrls = Collections.emptyList();
        // The publicApiUrl must be provided to generate image URLs.
        if (publicApiUrl != null && !publicApiUrl.isBlank() && car.getImages() != null && !car.getImages().isEmpty()) {
            imageUrls = car.getImages().stream()
                    .map(image -> {
                        String key = image.getImageType() + "/" + image.getFileName();
                        // THE FIX: Construct a URL to our own API proxy, not MinIO directly.
                        return publicApiUrl + "/api/v1/files/" + key;
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
     * @param fileStorageService The service for file operations.
     * @param publicApiUrl       The base public URL of the API.
     * @return A list of CarResponseDTOs.
     */
    public static List<CarResponseDTO> toDtoList(List<Car> cars, IFileStorageService fileStorageService, String publicApiUrl) {
        if (cars == null) {
            return Collections.emptyList();
        }
        return cars.stream()
                .map(car -> toDto(car, fileStorageService, publicApiUrl))
                .collect(Collectors.toList());
    }

    /**
     * Converts a CarCreateDTO to a new Car entity.
     */
    public static Car toEntity(CarCreateDTO createDto) {
        if (createDto == null) return null;
        return new Car.Builder()
                .setMake(createDto.getMake())
                .setModel(createDto.getModel())
                .setYear(createDto.getYear())
                .setCategory(createDto.getCategory())
                .setPriceGroup(createDto.getPriceGroup())
                .setLicensePlate(createDto.getLicensePlate())
                .setVin(createDto.getVin())
                .setAvailable(createDto.getAvailable() != null ? createDto.getAvailable() : true)
                .build();
    }

    /**
     * Applies updates from a CarUpdateDTO to an existing Car entity.
     */
    public static Car applyUpdateDtoToEntity(CarUpdateDTO updateDto, Car existingCar) {
        if (updateDto == null || existingCar == null) {
            throw new IllegalArgumentException("Update DTO and existing Car entity must not be null.");
        }
        Car.Builder builder = new Car.Builder().copy(existingCar);
        if (updateDto.getMake() != null) builder.setMake(updateDto.getMake());
        if (updateDto.getModel() != null) builder.setModel(updateDto.getModel());
        if (updateDto.getYear() != null) builder.setYear(updateDto.getYear());
        if (updateDto.getCategory() != null) builder.setCategory(updateDto.getCategory());
        if (updateDto.getPriceGroup() != null) builder.setPriceGroup(updateDto.getPriceGroup());
        if (updateDto.getLicensePlate() != null) builder.setLicensePlate(updateDto.getLicensePlate());
        if (updateDto.getAvailable() != null) builder.setAvailable(updateDto.getAvailable());
        if (updateDto.getVin() != null) builder.setVin(updateDto.getVin());
        return builder.applyTo(existingCar);
    }
}