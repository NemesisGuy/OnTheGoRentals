package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IFileStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AdminCarController.java
 * Controller for administrative operations on Car entities.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/cars")
@Tag(name = "Admin: Car Management", description = "Endpoints for administrators to manage car inventory.")
public class AdminCarController {

    private static final Logger log = LoggerFactory.getLogger(AdminCarController.class);
    private final ICarService carService;
    private final IFileStorageService fileStorageService;

    /**
     * Constructs an AdminCarController with necessary service dependencies.
     *
     * @param carService         The service for car business logic.
     * @param fileStorageService The service for handling file storage operations.
     */
    @Autowired
    public AdminCarController(ICarService carService, IFileStorageService fileStorageService) {
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
    @Operation(summary = "Create a new car", description = "Allows administrators to add a new car to the inventory.")
    @ApiResponses(value = @ApiResponse(responseCode = "201", description = "Car created successfully", content = @Content(schema = @Schema(implementation = CarResponseDTO.class))))
    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarCreateDTO carCreateDTO) {
        log.info("Admin request to create a new car with DTO: {}", carCreateDTO);
        Car carToCreate = CarMapper.toEntity(carCreateDTO);
        Car createdCar = carService.create(carToCreate);
        log.info("Successfully created car with UUID: {}", createdCar.getUuid());
        return new ResponseEntity<>(CarMapper.toDto(createdCar, fileStorageService), HttpStatus.CREATED);
    }


    /**
     * Retrieves all cars for administrative purposes.
     *
     * @return A ResponseEntity containing a list of all car DTOs.
     */
    @Operation(summary = "Get all cars", description = "Retrieves a list of all cars in the system.")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved car list"))
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCarsForAdmin() {
        log.info("Admin request to get all cars.");
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Pass service to mapper to generate image URLs for each car
        return ResponseEntity.ok(CarMapper.toDtoList(cars, fileStorageService));
    }

    /**
     * Retrieves a specific car by its UUID for administrative purposes.
     *
     * @param carUuid The UUID of the car to retrieve.
     * @return A ResponseEntity containing the car's DTO.
     */
    @Operation(summary = "Get car by UUID", description = "Retrieves a specific car by its UUID.")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Car found", content = @Content(schema = @Schema(implementation = CarResponseDTO.class))))
    @GetMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> getCarByUuidAdmin(@PathVariable UUID carUuid) {
        log.info("Admin request to get car by UUID: {}", carUuid);
        Car car = carService.read(carUuid);
        // Pass service to mapper to generate image URLs
        return ResponseEntity.ok(CarMapper.toDto(car, fileStorageService));
    }

    /**
     * Updates an existing car's non-image details.
     *
     * @param carUuid      The UUID of the car to update.
     * @param carUpdateDTO The DTO with the updated data.
     * @return The updated car's DTO.
     */
    @Operation(summary = "Update an existing car", description = "Updates the details of an existing car.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car updated successfully", content = @Content(schema = @Schema(implementation = CarResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Car not found with the specified UUID")
    })
    @PutMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> updateCar(
            @Parameter(description = "UUID of the car to update", required = true) @PathVariable UUID carUuid,
            @Valid @RequestBody CarUpdateDTO carUpdateDTO) {
        log.info("Admin request to update car with UUID: {}", carUuid);
        Car existingCar = carService.read(carUuid);
        Car carWithUpdates = CarMapper.applyUpdateDtoToEntity(carUpdateDTO, existingCar);
        Car updatedCar = carService.update(carWithUpdates);
        log.info("Successfully updated car data for UUID: {}", updatedCar.getUuid());
        return ResponseEntity.ok(CarMapper.toDto(updatedCar, fileStorageService));
    }

    /**
     * Soft-deletes a car.
     *
     * @param carUuid The UUID of the car to delete.
     * @return A ResponseEntity with no content.
     */
    @Operation(summary = "Delete a car", description = "Soft-deletes a car by its UUID.")
    @DeleteMapping("/{carUuid}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carUuid) {
        log.info("Admin request to delete car with UUID: {}", carUuid);
        Car carToDelete = carService.read(carUuid);
        carService.delete(carToDelete.getId());
        log.info("Successfully soft-deleted car with UUID: {}.", carUuid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Uploads one or more images and associates them with an existing car.
     *
     * @param carUuid The UUID of the car to add images to.
     * @param files   A list of files sent with the multipart key "images".
     * @return The updated car DTO with the full list of image URLs.
     */
    @Operation(summary = "Upload images for a car", description = "Uploads one or more images to an existing car.")
    @PostMapping("/{carUuid}/images")
    public ResponseEntity<CarResponseDTO> uploadCarImages(
            @PathVariable UUID carUuid,
            @RequestParam("images") List<MultipartFile> files) {
        if (files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new BadRequestException("No image files provided.");
        }
        Car updatedCar = carService.addImagesToCar(carUuid, files);
        log.info("Successfully added {} image(s) to car UUID: {}", files.size(), carUuid);
        // Pass service to mapper for the response
        return ResponseEntity.ok(CarMapper.toDto(updatedCar, fileStorageService));
    }

    /**
     * Deletes a specific image associated with a car.
     *
     * @param carUuid   The UUID of the car.
     * @param imageUuid The UUID of the image to delete.
     * @return A ResponseEntity with no content.
     */
    @Operation(summary = "Delete a car image", description = "Deletes a specific image associated with a car.")
    @DeleteMapping("/{carUuid}/images/{imageUuid}")
    public ResponseEntity<Void> deleteCarImage(@PathVariable UUID carUuid, @PathVariable UUID imageUuid) {
        log.info("Request to delete image UUID {} from car UUID {}", imageUuid, carUuid);
        Car existingCar = carService.read(carUuid);
        Optional<CarImage> imageToDeleteOpt = existingCar.getImages().stream()
                .filter(img -> img.getUuid().equals(imageUuid))
                .findFirst();

        if (imageToDeleteOpt.isPresent()) {
            CarImage image = imageToDeleteOpt.get();
            String keyToDelete = image.getImageType() + "/" + image.getFileName();

            existingCar.getImages().remove(image);
            carService.update(existingCar);
            fileStorageService.delete(keyToDelete);

            log.info("Successfully deleted image UUID {} and its file with key '{}'", imageUuid, keyToDelete);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves a list of all cars currently available for booking.
     *
     * @return A ResponseEntity containing a list of available car DTOs.
     */
    @Operation(summary = "Get available cars", description = "Retrieves a list of all cars currently marked as available.")
    @GetMapping("/list/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsForAdmin() {
        log.info("Admin request to get all available cars.");
        List<Car> availableCars = carService.getAvailableCars();
        if (availableCars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Pass service to mapper to generate image URLs
        return ResponseEntity.ok(CarMapper.toDtoList(availableCars, fileStorageService));
    }
}