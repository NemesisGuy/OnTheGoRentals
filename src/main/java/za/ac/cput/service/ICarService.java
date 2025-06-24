package za.ac.cput.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.List;
import java.util.UUID;

/**
 * ICarService.java
 * Interface defining the contract for car-related services.
 * Extends the generic {@link IService} for basic CRUD operations and adds
 * car-specific query methods, such as finding available cars or filtering by price group/category.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface ICarService extends IService<Car, Integer> {

    /**
     * Creates a new car in the system.
     * * @param car The {@link Car} entity to create.
     * * @return The created {@link Car} entity, with its ID populated.
     * * This method is responsible for persisting a new car entity to the database.
     */
    @Override
    Car create(Car car);

    /**
     * Reads a car by its ID.
     * * @param id The ID of the car to retrieve.
     * * @return The {@link Car} entity if found, or {@code null} if not found or soft-deleted.
     * * This method retrieves a car entity from the database based on its ID.
     */
    @Override
    Car read(Integer id);

    /**
     * Updates an existing car in the system.
     * * @param car The {@link Car} entity with updated information.
     * * @return The updated {@link Car} entity.
     * * This method is responsible for updating an existing car entity in the database.
     */
    @Override
    Car update(Car car);

    /**
     * Deletes a car by its ID.
     * * This method performs a soft delete, marking the car as deleted without removing it from the database.
     * * @param id The ID of the car to delete.
     * * @return {@code true} if the car was found and soft-deleted, {@code false} otherwise.
     */
    @Override
    boolean delete(Integer id);

    /**
     * Soft-deletes a car by its UUID.
     * * This method marks the car as deleted without physically removing it from the database.
     *
     * @param uuid The UUID of the car to delete.
     * @return {@code true} if the car was found and soft-deleted, {@code false} otherwise.
     * * This method is used to remove a car from active listings without losing its data.
     * * @deprecated Use {@link #delete(Integer)} for consistency with other entities.
     */
    @Deprecated
    boolean delete(UUID uuid);

    /**
     * Retrieves all non-deleted cars in the system.
     *
     * @return A list of all non-deleted {@link Car} entities. Can be empty.
     */
    List<Car> getAll();
    /*

     */
/**
 * Retrieves a list of all cars currently marked as available for rental.
 * This typically means cars that are not soft-deleted and have their 'available' flag set to true.
 *
 * @return A list of available {@link Car} entities. Can be empty.
 * @deprecated Prefer {@link # findAllAvailableAndNonDeleted()} for clarity.
 */


    /**
     * Retrieves a car by its UUID.
     *
     * @param uuid The UUID of the car.
     * @return The {@link Car} entity, or {@code null} if not found or soft-deleted.
     */
    Car read(UUID uuid);

    /**
     * Retrieves a list of all available (and non-deleted) cars.
     * This method is used to fetch cars that are currently available for rental.
     *
     * @return A list of available {@link Car} entities. Can be empty.
     */
    @Transactional(readOnly = true)
    List<Car> getAllAvailableCars();


    /**
     * Retrieves a list of available (and non-deleted) cars belonging to a specific price group.
     *
     * @param priceGroup The {@link PriceGroup} to filter by.
     * @return A list of available {@link Car} entities matching the price group. Can be empty.
     */
    List<Car> getAvailableCarsByPrice(PriceGroup priceGroup);

    /**
     * Retrieves a list of available (and non-deleted) cars belonging to a specific category.
     *
     * @param category The category string to filter by (e.g., "SUV", "Sedan"). Case-sensitive.
     * @return A list of available {@link Car} entities matching the category. Can be empty.
     */
    List<Car> findAllAvailableByCategory(String category);


    /**
     * Adds images to a car identified by its UUID.
     * This method handles the uploading of multiple image files associated with a specific car.
     *
     * @param carUuid The UUID of the car to which images will be added.
     * @param files   A list of image files to upload.
     * @return The updated {@link Car} entity with the new images added.
     */
    Car addImagesToCar(UUID carUuid, List<MultipartFile> files);

}