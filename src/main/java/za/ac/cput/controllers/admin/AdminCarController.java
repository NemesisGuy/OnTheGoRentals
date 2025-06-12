package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.domain.dto.request.CarCreateDTO;
import za.ac.cput.domain.dto.request.CarUpdateDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.CarImage;
import za.ac.cput.domain.enums.ImageType;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.service.FileStorageService;
import za.ac.cput.service.ICarService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AdminCarController.java
 * Controller for administrative operations on Car entities.
 * Supports creating, reading, updating, deleting cars, and managing multiple images per car.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@RestController
@RequestMapping("/api/v1/admin/cars")
public class AdminCarController {

    private static final Logger log = LoggerFactory.getLogger(AdminCarController.class);
    private final ICarService carService;
    private final FileStorageService fileStorageService;

    @Autowired
    public AdminCarController(ICarService carService, FileStorageService fileStorageService) {
        this.carService = carService;
        this.fileStorageService = fileStorageService;
        log.info("AdminCarController initialized.");
    }

    /**
     * Creates a new car. Images should be uploaded separately after creation.
     *
     * @param carCreateDTO The DTO for creating a car.
     * @return A ResponseEntity with the created car's DTO.
     */
    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarCreateDTO carCreateDTO) {
        log.info("Admin request to create a new car with DTO: {}", carCreateDTO);
        Car carToCreate = CarMapper.toEntity(carCreateDTO);
        Car createdCar = carService.create(carToCreate);
        log.info("Successfully created car with UUID: {}", createdCar.getUuid());
        return new ResponseEntity<>(CarMapper.toDto(createdCar), HttpStatus.CREATED);
    }

    /**
     * Retrieves all cars for the admin view. The mapper now handles image URL generation.
     *
     * @return A list of all cars as DTOs.
     */
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCarsForAdmin() {
        log.info("Admin request to get all cars.");
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            log.info("No cars found.");
            return ResponseEntity.noContent().build();
        }
        // The CarMapper.toDtoList now handles building the imageUrls list automatically.
        List<CarResponseDTO> dtos = CarMapper.toDtoList(cars);
        log.info("Successfully retrieved {} cars.", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Retrieves a specific car by its UUID. The mapper handles image URL generation.
     *
     * @param carUuid The UUID of the car to retrieve.
     * @return The car's DTO.
     */
    @GetMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> getCarByUuidAdmin(@PathVariable UUID carUuid) {
        log.info("Admin request to get car by UUID: {}", carUuid);
        Car car = carService.read(carUuid);
        return ResponseEntity.ok(CarMapper.toDto(car));
    }

    /**
     * Updates a car's non-image details.
     *
     * @param carUuid      The UUID of the car to update.
     * @param carUpdateDTO The DTO with the updated data.
     * @return The updated car's DTO.
     */
    @PutMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> updateCar(
            @PathVariable UUID carUuid,
            @Valid @RequestBody CarUpdateDTO carUpdateDTO) {
        log.info("Admin request to update car with UUID: {}. Update DTO: {}", carUuid, carUpdateDTO);
        Car existingCar = carService.read(carUuid);
        Car carWithUpdates = CarMapper.applyUpdateDtoToEntity(carUpdateDTO, existingCar);
        Car updatedCar = carService.update(carWithUpdates);
        log.info("Successfully updated car data for UUID: {}", updatedCar.getUuid());
        return ResponseEntity.ok(CarMapper.toDto(updatedCar));
    }

    /**
     * Soft-deletes a car.
     *
     * @param carUuid The UUID of the car to delete.
     * @return A No Content response.
     */
    @DeleteMapping("/{carUuid}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carUuid) {
        log.info("Admin request to delete car with UUID: {}", carUuid);
        Car carToDelete = carService.read(carUuid);
        // Deleting the car will also delete associated CarImage entities due to CascadeType.ALL
        // and physical files due to the logic in CarServiceImpl.
        carService.delete(carToDelete.getId());
        log.info("Successfully soft-deleted car with UUID: {}.", carUuid);
        return ResponseEntity.noContent().build();
    }


    // --- NEW ENDPOINTS FOR MULTIPLE IMAGE MANAGEMENT ---

    /**
     * Uploads one or more images and associates them with an existing car.
     *
     * @param carUuid The UUID of the car to add images to.
     * @param files   A list of files sent with the multipart key "images".
     * @return The updated car DTO with the full list of image URLs.
     */
    @PostMapping("/{carUuid}/images")
    public ResponseEntity<CarResponseDTO> uploadCarImages(
            @PathVariable UUID carUuid,
            @RequestParam("images") List<MultipartFile> files) {

        log.info("Request to upload {} image(s) for car UUID: {}", files.size(), carUuid);
        if (files.isEmpty()) {
            throw new BadRequestException("No image files provided.");
        }

        Car existingCar = carService.read(carUuid);

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String filename = fileStorageService.save(ImageType.CAR.getFolder(), file);

            CarImage newImage = CarImage.builder()
                    .fileName(filename)
                    .imageType(ImageType.CAR.getFolder())
                    .car(existingCar)
                    .build();

            existingCar.getImages().add(newImage);
        }

        Car updatedCar = carService.update(existingCar);
        log.info("Successfully added {} image(s) to car UUID: {}", files.size(), carUuid);
        return ResponseEntity.ok(CarMapper.toDto(updatedCar));
    }

    /**
     * Deletes a specific image from a car.
     *
     * @param carUuid   The UUID of the parent car.
     * @param imageUuid The UUID of the image to delete.
     * @return A No Content (204) response on success.
     */
    @DeleteMapping("/{carUuid}/images/{imageUuid}")
    public ResponseEntity<Void> deleteCarImage(
            @PathVariable UUID carUuid,
            @PathVariable UUID imageUuid) {

        log.info("Request to delete image UUID {} from car UUID {}", imageUuid, carUuid);
        Car existingCar = carService.read(carUuid);

        Optional<CarImage> imageToDeleteOpt = existingCar.getImages().stream()
                .filter(img -> img.getUuid().equals(imageUuid))
                .findFirst();

        if (imageToDeleteOpt.isPresent()) {
            CarImage image = imageToDeleteOpt.get();
            String filename = image.getFileName();
            String fileType = image.getImageType();

            // orphanRemoval=true in the Car entity means this will delete the CarImage from the DB
            existingCar.getImages().remove(image);
            carService.update(existingCar);

            // Now, delete the physical file from storage
            fileStorageService.delete(fileType, filename);

            log.info("Successfully deleted image UUID {} and its file '{}'", imageUuid, filename);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Image UUID {} not found on car UUID {}", imageUuid, carUuid);
            return ResponseEntity.notFound().build();
        }
    }
    ////api/v1/admin/cars/list/available
    /**
     * Retrieves all available cars for admin view.
     * This endpoint is used to list cars that are currently available for rental.
     *
     * @return A list of available cars as DTOs.
     */
    @GetMapping("/list/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsForAdmin() {
        log.info("Admin request to get all available cars.");
        List<Car> availableCars = carService.getAvailableCars();
        if (availableCars.isEmpty()) {
            log.info("No available cars found.");
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> dtos = CarMapper.toDtoList(availableCars);
        log.info("Successfully retrieved {} available cars.", dtos.size());
        return ResponseEntity.ok(dtos);
    }

}