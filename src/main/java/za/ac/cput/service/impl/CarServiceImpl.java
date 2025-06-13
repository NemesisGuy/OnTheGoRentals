package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.CarImage;
import za.ac.cput.domain.enums.ImageType;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IFileStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CarServiceImpl.java
 * Implementation of the {@link ICarService} interface.
 * Provides business logic for managing Car entities.
 * Entities are treated as immutable; updates are performed using a Builder pattern.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@Service("carServiceImpl")
@Transactional
public class CarServiceImpl implements ICarService {

    private static final Logger log = LoggerFactory.getLogger(CarServiceImpl.class);
    private final CarRepository carRepository;
    private final IFileStorageService fileStorageService; // Assuming this is injected for image handling

    @Autowired
    public CarServiceImpl(CarRepository carRepository , IFileStorageService fileStorageService) {
        this.carRepository = carRepository;
        this.fileStorageService = fileStorageService; // Injecting the file storage service
        log.info("CarServiceImpl initialized.");
    }

    @Override
    public Car create(Car car) {
        log.info("Attempting to create new car. Make: '{}', Model: '{}'", car.getMake(), car.getModel());
        // Car's @PrePersist handles UUID, createdAt, updatedAt, deleted=false
        Car savedCar = carRepository.save(car);
        log.info("Successfully created car. ID: {}, UUID: {}, Make: '{}', Model: '{}'",
                savedCar.getId(), savedCar.getUuid(), savedCar.getMake(), savedCar.getModel());
        return savedCar;
    }

    @Override
    public Car read(Integer id) {
        log.debug("Attempting to read car by internal ID: {}", id);
        Optional<Car> optionalCar = carRepository.findByIdAndDeletedFalse(id);
        if (optionalCar.isPresent()) {
            Car car = optionalCar.get();
            log.debug("Car found for ID: {}. UUID: '{}', Make: '{}'", id, car.getUuid(), car.getMake());
            return car;
        }
        log.warn("Car not found or is deleted for ID: {}", id);
        return null;
    }

    @Override
    public Car read(UUID carUuid) {
        log.debug("Attempting to read car by UUID: '{}'", carUuid);
        Optional<Car> optionalCar = carRepository.findByUuidAndDeletedFalse(carUuid);
        if (optionalCar.isPresent()) {
            Car car = optionalCar.get();
            log.debug("Car found for UUID: '{}'. ID: {}, Make: '{}'", carUuid, car.getId(), car.getMake());
            return car;
        }
        log.warn("Car not found or is deleted for UUID: '{}'", carUuid);
        return null;
    }

    /**
     * {@inheritDoc}
     * The 'carWithUpdates' parameter should be a fully formed Car entity
     * representing the desired new state, including the correct ID and UUID of the
     * car to be updated.
     */
    @Override
    public Car update(Car carWithUpdates) {
        Integer carId = carWithUpdates.getId();
        UUID carUuid = carWithUpdates.getUuid();
        log.info("Attempting to update car. Provided ID: {}, Provided UUID: '{}'", carId, carUuid);

        if (carId == null || carId == 0) { // ID 0 can mean uninitialized for primitive int
            log.error("Update failed: Car ID is missing or invalid (0) in the input for update. Cannot identify car to update.");
            throw new IllegalArgumentException("A valid Car ID must be provided in the car object for an update.");
        }

        // Fetch the existing car by its ID to ensure it exists and is not deleted.
        Car existingCar = carRepository.findByIdAndDeletedFalse(carId)
                .orElseThrow(() -> {
                    log.warn("Update failed: Car not found or is deleted for ID: {}", carId);
                    return new ResourceNotFoundException("Car not found with ID: " + carId + " for update.");
                });

        log.debug("Found existing car for update: ID: {}, UUID: '{}', Current Make: '{}', Model: '{}'",
                existingCar.getId(), existingCar.getUuid(), existingCar.getMake(), existingCar.getModel());

        // The carWithUpdates object IS the new desired state.
        // The builder in the controller should have copied existingCar and applied DTO changes.
        // We must ensure the ID and UUID from the existingCar are preserved on carWithUpdates.
        // The @PreUpdate in Car entity will set updatedAt.

        Car entityToSave = new Car.Builder()
                .copy(carWithUpdates) // This has all the new field values from the DTO
                .setId(existingCar.getId())       // Ensure ID from DB record is used
                .setUuid(existingCar.getUuid())     // Ensure UUID from DB record is used (it's updatable=false)
                .setCreatedAt(existingCar.getCreatedAt()) // Preserve original creation timestamp
                // deleted flag should not be changed by a general update method
                .setDeleted(existingCar.isDeleted())
                .build(); // This will also trigger @PreUpdate via the save operation

        log.debug("State of car entity being saved for update: {}", entityToSave);
        Car updatedCar = carRepository.save(entityToSave);

        log.info("Successfully updated car. ID: {}, UUID: '{}', New Make: '{}', New Model: '{}'",
                updatedCar.getId(), updatedCar.getUuid(), updatedCar.getMake(), updatedCar.getModel());
        return updatedCar;
    }


    @Override
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete car with internal ID: {}", id);
        return carRepository.findByIdAndDeletedFalse(id).map(car -> {
            log.debug("Found car for soft-deletion: ID: {}, UUID: '{}'", id, car.getUuid());
            Car deletedCarState = new Car.Builder()
                    .copy(car)
                    .setDeleted(true)
                    .setAvailable(false) // Typically, a deleted car is not available
                    .build();
            carRepository.save(deletedCarState);
            log.info("Successfully soft-deleted car ID: {}", id);
            return true;
        }).orElseGet(() -> {
            log.warn("Soft-delete failed: Car not found or already deleted for ID: {}", id);
            return false;
        });
    }

    @Override
    public boolean delete(UUID uuid) {
        log.info("Attempting to soft-delete car with UUID: '{}'", uuid);
        return carRepository.findByUuidAndDeletedFalse(uuid).map(car -> {
            log.debug("Found car for soft-deletion: UUID: '{}', ID: {}", uuid, car.getId());
            Car deletedCarState = new Car.Builder()
                    .copy(car)
                    .setDeleted(true)
                    .setAvailable(false)
                    .build();
            carRepository.save(deletedCarState);
            log.info("Successfully soft-deleted car UUID: '{}'", uuid);
            return true;
        }).orElseGet(() -> {
            log.warn("Soft-delete failed: Car not found or already deleted for UUID: '{}'", uuid);
            return false;
        });
    }

    @Override
    public List<Car> getAll() {
        log.debug("Fetching all non-deleted cars.");
        return carRepository.findByDeletedFalse();
    }

    @Override
    public List<Car> getAllAvailableCars() {
        log.debug("Fetching all cars that are available and not deleted.");
        return carRepository.findAllByAvailableTrueAndDeletedFalse();
    }

    @Override
    public List<Car> getCarsByPriceGroup(PriceGroup priceGroup) {
        log.debug("Fetching non-deleted cars by price group: {}", priceGroup);
        return carRepository.findByPriceGroupAndDeletedFalse(priceGroup);
    }

    @Override
    public List<Car> findAllAvailableAndNonDeleted() {
        log.debug("Fetching all available and non-deleted cars.");
        return carRepository.findAllByAvailableTrueAndDeletedFalse();
    }

    @Override
    public List<Car> getAvailableCarsByPrice(PriceGroup priceGroup) {
        log.debug("Fetching available and non-deleted cars by price group: {}", priceGroup);
        return carRepository.findAllByAvailableTrueAndDeletedFalseAndPriceGroup(priceGroup);
    }

    @Override
    public List<Car> findAllAvailableByCategory(String category) {
        log.debug("Fetching available and non-deleted cars by category: '{}'", category);
        return carRepository.findAllByAvailableTrueAndDeletedFalseAndCategory(category);
    }

    @Override
    public List<Car> findAllAvailableByPriceGroup(PriceGroup priceGroup) {
        log.warn("Calling findAllAvailableByPriceGroup, which is functionally similar to getAvailableCarsByPrice. Consolidate if possible.");
        return carRepository.findAllByAvailableTrueAndDeletedFalseAndPriceGroup(priceGroup);
    }

    @Override
    public List<Car> getAvailableCars() {
        log.debug("Fetching all available and non-deleted cars.");
        return carRepository.findAllByAvailableTrueAndDeletedFalse();
    }

    @Override
    @Transactional // This makes the entire method an "all or nothing" operation.
    public Car addImagesToCar(UUID carUuid, List<MultipartFile> files) {
        // 1. Find the car entity. Throws an exception if not found.
        Car existingCar = carRepository.findByUuid(carUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with UUID: " + carUuid));

        // 2. Loop through the files and process each one.
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            // Save the physical file using the storage service.
            String fileKey = fileStorageService.save(file, ImageType.CAR.getFolder());
            String filename = fileKey.substring(fileKey.lastIndexOf("/") + 1);

            // Create the new CarImage entity.
            CarImage newImage = CarImage.builder()
                    .fileName(filename)
                    .imageType(ImageType.CAR.getFolder())
                    .car(existingCar) // Associate it with the car.
                    .build();

            // Add the new image to the car's list of images.
            // Because of the CascadeType.ALL setting on the Car's 'images' field,
            // this new CarImage will be saved automatically when the car is saved.
            existingCar.getImages().add(newImage);
        }

        // 3. Save the updated car entity. The @Transactional annotation ensures
        // that all changes (including the new CarImage entities) are saved together.
        // If any file fails to save, the transaction will roll back, and no changes
        // will be made to the database.
        return carRepository.save(existingCar);
    }

}