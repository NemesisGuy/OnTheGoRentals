package za.ac.cput.controllers.admin;

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
import za.ac.cput.domain.enums.ImageType;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IFileStorageService; // <-- CORRECT: Ensure this is the new core interface

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cars")
@Tag(name = "Admin Car Management", description = "Endpoints for administrators to manage car inventory.")
public class AdminCarController {

    private static final Logger log = LoggerFactory.getLogger(AdminCarController.class);
    private final ICarService carService;
    private final IFileStorageService fileStorageService; // <-- CORRECT: Use the core interface type

    @Autowired
    public AdminCarController(ICarService carService, IFileStorageService fileStorageService) { // <-- CORRECT: Constructor accepts the core interface
        this.carService = carService;
        this.fileStorageService = fileStorageService;
        log.info("AdminCarController initialized.");
    }

    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarCreateDTO carCreateDTO) {
        log.info("Admin request to create a new car with DTO: {}", carCreateDTO);
        Car carToCreate = CarMapper.toEntity(carCreateDTO);
        Car createdCar = carService.create(carToCreate);
        log.info("Successfully created car with UUID: {}", createdCar.getUuid());
        return new ResponseEntity<>(CarMapper.toDto(createdCar), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCarsForAdmin() {
        log.info("Admin request to get all cars.");
        List<Car> cars = carService.getAll();
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> dtos = CarMapper.toDtoList(cars);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{carUuid}")
    public ResponseEntity<CarResponseDTO> getCarByUuidAdmin(@PathVariable UUID carUuid) {
        log.info("Admin request to get car by UUID: {}", carUuid);
        Car car = carService.read(carUuid);
        return ResponseEntity.ok(CarMapper.toDto(car));
    }

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

    @DeleteMapping("/{carUuid}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carUuid) {
        log.info("Admin request to delete car with UUID: {}", carUuid);
        Car carToDelete = carService.read(carUuid);
        carService.delete(carToDelete.getId());
        log.info("Successfully soft-deleted car with UUID: {}.", carUuid);
        return ResponseEntity.noContent().build();
    }

    /*@PostMapping("/{carUuid}/images")
    public ResponseEntity<CarResponseDTO> uploadCarImages(
            @PathVariable UUID carUuid,
            @RequestParam("images") List<MultipartFile> files) {
        log.info("Request to upload {} image(s) for car UUID: {}", files.size(), carUuid);
        if (files.isEmpty()) throw new BadRequestException("No image files provided.");

        Car existingCar = carService.read(carUuid);

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            // This now returns a "key" like "cars/image.jpg"
            String fileKey = fileStorageService.save(file, ImageType.CAR.getFolder());
            // We need to save just the filename part in the entity
            String filename = fileKey.substring(fileKey.lastIndexOf("/") + 1);

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
    }*/
    /**
     * Uploads one or more images and associates them with an existing car.
     * The core logic is now delegated to the transactional ICarService.
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
        if (files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new BadRequestException("No image files provided.");
        }

        // Delegate the entire operation to the service layer.
        Car updatedCar = carService.addImagesToCar(carUuid, files);

        log.info("Successfully added {} image(s) to car UUID: {}", files.size(), carUuid);
        // Use the injected mapper to convert the final result to a DTO
        return ResponseEntity.ok(CarMapper.toDto(updatedCar));
    }

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

            // CORRECT: Combine folder and filename to create the key for the delete method
            String keyToDelete = image.getImageType() + "/" + image.getFileName();

            existingCar.getImages().remove(image);
            carService.update(existingCar);

            // Now, delete the physical file using the correct key
            fileStorageService.delete(keyToDelete);

            log.info("Successfully deleted image UUID {} and its file with key '{}'", imageUuid, keyToDelete);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Image UUID {} not found on car UUID {}", imageUuid, carUuid);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCarsForAdmin() {
        log.info("Admin request to get all available cars.");
        List<Car> availableCars = carService.getAvailableCars();
        if (availableCars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> dtos = CarMapper.toDtoList(availableCars);
        return ResponseEntity.ok(dtos);
    }

}