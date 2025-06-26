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
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IFileStorageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link ICarService} interface.
 * Provides transactional business logic for managing Car entities and their associated images.
 * This service layer enforces business rules, such as soft-deletes and an immutable update pattern.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated by: Peter Buckingham
 * Updated: 2025-06-22
 */
@Service("carServiceImpl")
@Transactional
public class CarServiceImpl implements ICarService {

    private static final Logger log = LoggerFactory.getLogger(CarServiceImpl.class);
    private final CarRepository carRepository;
    private final IFileStorageService fileStorageService;
    private final BookingRepository bookingRepository;


    /**
     * Constructs the CarServiceImpl with its required dependencies.
     *
     * @param carRepository      The repository for data access operations on Car entities.
     * @param fileStorageService The service for handling physical file storage operations (e.g., saving images).
     */
    @Autowired
    public CarServiceImpl(CarRepository carRepository, IFileStorageService fileStorageService,
                          BookingRepository bookingRepository) {
        this.carRepository = carRepository;
        this.fileStorageService = fileStorageService;
        this.bookingRepository = bookingRepository;

        log.info("CarServiceImpl initialized.");
    }

    /**
     * Persists a new Car entity to the database.
     * The entity's UUID and timestamps are set automatically via {@link jakarta.persistence.PrePersist} hooks.
     *
     * @param car The new {@link Car} entity to create.
     * @return The saved {@link Car} entity with its database-generated ID and UUID.
     */
    @Override
    public Car create(Car car) {
        log.info("Attempting to create new car. Make: '{}', Model: '{}'", car.getMake(), car.getModel());
        Car savedCar = carRepository.save(car);
        log.info("Successfully created car. ID: {}, UUID: {}, Make: '{}', Model: '{}'",
                savedCar.getId(), savedCar.getUuid(), savedCar.getMake(), savedCar.getModel());
        return savedCar;
    }

    /**
     * Retrieves a non-deleted Car by its primary key (internal database ID).
     *
     * @param id The internal ID of the car.
     * @return The found {@link Car}.
     * @throws ResourceNotFoundException if no car is found with the given ID, or it has been soft-deleted.
     */
    @Override
    @Transactional(readOnly = true)
    public Car read(Integer id) {
        log.debug("Attempting to read car by internal ID: {}", id);
        return carRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found or is deleted for ID: " + id));
    }

    /**
     * Retrieves a non-deleted Car by its public-facing UUID.
     *
     * @param carUuid The UUID of the car.
     * @return The found {@link Car}.
     * @throws ResourceNotFoundException if no car is found with the given UUID, or it has been soft-deleted.
     */
    @Override
    @Transactional(readOnly = true)
    public Car read(UUID carUuid) {
        log.debug("Attempting to read car by UUID: '{}'", carUuid);
        return carRepository.findByUuidAndDeletedFalse(carUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found or is deleted for UUID: " + carUuid));
    }

    /**
     * Updates an existing car in the database using an "immutable update" approach.
     *
     * @param carWithUpdates A {@link Car} object representing the desired new state. Must contain the ID of the car to update.
     * @return The updated and persisted {@link Car} entity.
     * @throws IllegalArgumentException  if the car ID is missing from the input object.
     * @throws ResourceNotFoundException if no car exists with the provided ID.
     */
    @Override
    public Car update(Car carWithUpdates) {
        Integer carId = carWithUpdates.getId();
        log.info("Attempting to update car with ID: {}", carId);

        if (carId == 0) {
            throw new IllegalArgumentException("A valid Car ID must be provided for an update.");
        }

        Car existingCar = carRepository.findByIdAndDeletedFalse(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + carId + " for update."));

        Car entityToSave = new Car.Builder()
                .copy(carWithUpdates)
                .setId(existingCar.getId())
                .setUuid(existingCar.getUuid())
                .setCreatedAt(existingCar.getCreatedAt())
                .setDeleted(existingCar.isDeleted())
                .build();

        Car updatedCar = carRepository.save(entityToSave);
        log.info("Successfully updated car. ID: {}", updatedCar.getId());
        return updatedCar;
    }

    /**
     * Soft-deletes a car by its internal ID.
     * This sets the 'deleted' flag to true and 'available' to false.
     *
     * @param id The internal ID of the car to delete.
     * @return {@code true} if the car was found and soft-deleted, {@code false} otherwise.
     */
    @Override
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete car with internal ID: {}", id);
        return carRepository.findByIdAndDeletedFalse(id).map(car -> {
            car = new Car.Builder()
                    .copy(car)
                    .setDeleted(true)
                    .setAvailable(false)
                    .build();
            carRepository.save(car);
            log.info("Successfully soft-deleted car ID: {}", id);
            return true;
        }).orElse(false);
    }

    /**
     * Soft-deletes a car by its public-facing UUID.
     * This sets the 'deleted' flag to true and 'available' to false.
     *
     * @param uuid The UUID of the car to delete.
     * @return {@code true} if the car was found and soft-deleted, {@code false} otherwise.
     * @deprecated Use {@link #delete(Integer)} instead for consistency with other services.
     */
    @Deprecated
    @Override
    public boolean delete(UUID uuid) {
        log.info("Attempting to soft-delete car with UUID: '{}'", uuid);
        return carRepository.findByUuidAndDeletedFalse(uuid).map(car -> {
            car = new Car.Builder()
                    .copy(car)
                    .setDeleted(true)
                    .setAvailable(false)
                    .build();
            carRepository.save(car);
            log.info("Successfully soft-deleted car UUID: '{}'", uuid);
            return true;
        }).orElse(false);
    }

    /**
     * Retrieves a list of all non-deleted cars.
     *
     * @return A {@link List} of all active cars.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Car> getAll() {
        log.debug("Fetching all non-deleted cars.");
        return carRepository.findByDeletedFalse();
    }

    /**
     * Retrieves a list of all non-deleted cars that are currently marked as available.
     *
     * @return A {@link List} of available cars.
     */
    @Transactional(readOnly = true)
    @Override
    public List<Car> getAllAvailableCars() {
        log.debug("Fetching all cars that are available and not deleted.");
        return carRepository.findAllByAvailableTrueAndDeletedFalse();
    }

    /**
     * Retrieves a list of available, non-deleted cars, filtered by price group.
     *
     * @param priceGroup The {@link PriceGroup} to filter by.
     * @return A {@link List} of available cars in that price group.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Car> getAvailableCarsByPrice(PriceGroup priceGroup) {
        log.debug("Fetching available and non-deleted cars by price group: {}", priceGroup);
        return carRepository.findAllByAvailableTrueAndDeletedFalseAndPriceGroup(priceGroup);
    }

    /**
     * Retrieves a list of available, non-deleted cars, filtered by category string.
     *
     * @param category The category name to filter by.
     * @return A {@link List} of available cars in that category.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Car> findAllAvailableByCategory(String category) {
        log.debug("Fetching available and non-deleted cars by category: '{}'", category);
        return carRepository.findAllByAvailableTrueAndDeletedFalseAndCategory(category);
    }

    /**
     * Adds multiple images to an existing car in a single atomic transaction.
     *
     * @param carUuid The UUID of the car to add images to.
     * @param files   A list of {@link MultipartFile} objects to add.
     * @return The updated {@link Car} entity with the new images added to its collection.
     * @throws ResourceNotFoundException if no car is found with the given UUID.
     */
    @Override
    @Transactional
    public Car addImagesToCar(UUID carUuid, List<MultipartFile> files) {
        Car existingCar = carRepository.findByUuid(carUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with UUID: " + carUuid));

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String fileKey = fileStorageService.save(file, ImageType.CAR.getFolder());
            String filename = fileKey.substring(fileKey.lastIndexOf("/") + 1);

            CarImage newImage = CarImage.builder()
                    .fileName(filename)
                    .imageType(ImageType.CAR.getFolder())
                    .car(existingCar)
                    .build();

            existingCar.getImages().add(newImage);
        }

        return carRepository.save(existingCar);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Car> findAvailableCarsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching available cars for date range: {} to {}", startDate, endDate);
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date.");
        }

        // Convert LocalDate to LocalDateTime for the query
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // Step 1: Find all car IDs that are busy during this period.
        List<Integer> busyCarIds = bookingRepository.findBookedCarIdsByDateRange(startDateTime, endDateTime);

        // Handle the case where no cars are booked.
        // The `NotIn` clause fails with an empty list in some JPA providers.
        if (busyCarIds.isEmpty()) {
            // If no cars are busy, then all available cars are returned.
            return carRepository.findAllByAvailableTrueAndDeletedFalse();
        }

        // Step 2: Find all available cars whose IDs are NOT in the busy list.
        return carRepository.findByAvailableTrueAndDeletedFalseAndIdNotIn(busyCarIds);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Car> getAvailableCarsByPrice(PriceGroup priceGroup, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching available cars by price group: {} for date range: {} to {}", priceGroup, startDate, endDate);
        List<Integer> busyCarIds = getBusyCarIds(startDate, endDate);

        if (busyCarIds.isEmpty()) {
            return carRepository.findAllByAvailableTrueAndDeletedFalseAndPriceGroup(priceGroup);
        }
        return carRepository.findByAvailableTrueAndDeletedFalseAndPriceGroupAndIdNotIn(priceGroup, busyCarIds);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Car> findAllAvailableByCategory(String category, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching available cars by category: '{}' for date range: {} to {}", category, startDate, endDate);
        List<Integer> busyCarIds = getBusyCarIds(startDate, endDate);

        if (busyCarIds.isEmpty()) {
            return carRepository.findAllByAvailableTrueAndDeletedFalseAndCategory(category);
        }
        return carRepository.findByAvailableTrueAndDeletedFalseAndCategoryAndIdNotIn(category, busyCarIds);
    }

    // This is the main availability method, it remains largely the same


    /**
     * Private helper method to encapsulate the logic for finding busy car IDs.
     * This avoids code duplication across the public service methods.
     */
    private List<Integer> getBusyCarIds(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required for availability checks.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date.");
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return bookingRepository.findBookedCarIdsByDateRange(startDateTime, endDateTime);
    }
}