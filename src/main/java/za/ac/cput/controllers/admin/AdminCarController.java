package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@RestController
@RequestMapping("/api/v1/admin/cars")
@Tag(name = "Admin: Car Management", description = "Endpoints for administrators to manage car inventory.")
public class AdminCarController {

    private static final Logger log = LoggerFactory.getLogger(AdminCarController.class);
    private final ICarService carService;
    private final IFileStorageService fileStorageService;
    private final String publicApiUrl; // <-- Add this field

    /**
     * Constructs an AdminCarController with necessary service dependencies and configuration properties.
     *
     * @param carService         The service for car business logic.
     * @param fileStorageService The service for handling file storage operations.
     * @param publicApiUrl       The public base URL of the API, injected from application properties.
     */
    @Autowired
    public AdminCarController(
            ICarService carService,
            IFileStorageService fileStorageService,
            @Value("${app.public-api-url}") String publicApiUrl // <-- Inject the property
    ) {
        this.carService = carService;
        this.fileStorageService = fileStorageService;
        this.publicApiUrl = publicApiUrl; // <-- Initialize it
        log.info("AdminCarController initialized.");
    }

    @Operation(summary = "Create a new car")
    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarCreateDTO carCreateDTO) {
        log.info("Admin request to create a new car with DTO: {}", carCreateDTO);
        Car carToCreate = CarMapper.toEntity(carCreateDTO);
        Car createdCar = carService.create(carToCreate);
        log.info("Successfully created car with UUID: {}", createdCar.getUuid());
        return new ResponseEntity<>(CarMapper.toDto(createdCar, fileStorageService, publicApiUrl), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all cars (Admin)")
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCarsForAdmin() {
        log.info("Admin request to get all cars.");
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(cars, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Get car by UUID (Admin)")
    @GetMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> getCarByUuidAdmin(@PathVariable UUID carUuid) {
        log.info("Admin request to get car by UUID: {}", carUuid);
        Car car = carService.read(carUuid);
        return ResponseEntity.ok(CarMapper.toDto(car, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Update an existing car")
    @PutMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> updateCar(@PathVariable UUID carUuid, @Valid @RequestBody CarUpdateDTO carUpdateDTO) {
        log.info("Admin request to update car with UUID: {}", carUuid);
        Car existingCar = carService.read(carUuid);
        Car carWithUpdates = CarMapper.applyUpdateDtoToEntity(carUpdateDTO, existingCar);
        Car updatedCar = carService.update(carWithUpdates);
        log.info("Successfully updated car data for UUID: {}", updatedCar.getUuid());
        return ResponseEntity.ok(CarMapper.toDto(updatedCar, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Delete a car")
    @DeleteMapping("/{carUuid}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carUuid) {
        log.info("Admin request to delete car with UUID: {}", carUuid);
        Car carToDelete = carService.read(carUuid);
        carService.delete(carToDelete.getId());
        log.info("Successfully soft-deleted car with UUID: {}.", carUuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload images for a car")
    @PostMapping("/{carUuid}/images")
    public ResponseEntity<CarResponseDTO> uploadCarImages(@PathVariable UUID carUuid, @RequestParam("images") List<MultipartFile> files) {
        if (files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new BadRequestException("No image files provided.");
        }
        Car updatedCar = carService.addImagesToCar(carUuid, files);
        log.info("Successfully added {} image(s) to car UUID: {}", files.size(), carUuid);
        return ResponseEntity.ok(CarMapper.toDto(updatedCar, fileStorageService, publicApiUrl));
    }

    @Operation(summary = "Delete a car image")
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

    @Operation(summary = "Get available cars (Admin)")
    @GetMapping("/list/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsForAdmin() {
        log.info("Admin request to get all available cars.");
//        List<Car> availableCars = carService.getAvailableCars();
        List<Car> availableCars = carService.getAllAvailableCars();
        if (availableCars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CarMapper.toDtoList(availableCars, fileStorageService, publicApiUrl));
    }
}