package za.ac.cput.service;

import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.List;
import java.util.UUID;

/**
 * ICarService.java
 * Interface defining the contract for car-related services.
 * Extends the generic {@link IService} for basic CRUD operations and adds
 * car-specific query methods, such as finding available cars or filtering by price group/category.
 *
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface ICarService extends IService<Car, Integer> {

    /**
     * {@inheritDoc}
     */
    @Override
    Car create(Car car);

    /**
     * {@inheritDoc}
     */
    @Override
    Car read(Integer id);

    /**
     * {@inheritDoc}
     */
    @Override
    Car update(Car car);

    /**
     * {@inheritDoc}
     */
    @Override
    boolean delete(Integer id);

    /**
     * Soft-deletes a car by its UUID.
     *
     * @param uuid The UUID of the car to delete.
     * @return {@code true} if the car was found and soft-deleted, {@code false} otherwise.
     */
    boolean delete(UUID uuid);

    /**
     * Retrieves all non-deleted cars in the system.
     *
     * @return A list of all non-deleted {@link Car} entities. Can be empty.
     */
    List<Car> getAll();

    /**
     * Retrieves a list of all cars currently marked as available for rental.
     * This typically means cars that are not soft-deleted and have their 'available' flag set to true.
     *
     * @return A list of available {@link Car} entities. Can be empty.
     * @deprecated Prefer {@link #findAllAvailableAndNonDeleted()} for clarity.
     */
    @Deprecated
    List<Car> getAllAvailableCars();

    /**
     * Retrieves a car by its UUID.
     *
     * @param uuid The UUID of the car.
     * @return The {@link Car} entity, or {@code null} if not found or soft-deleted.
     */
    Car read(UUID uuid);

    /**
     * Retrieves a list of non-deleted cars belonging to a specific price group.
     *
     * @param priceGroup The {@link PriceGroup} to filter by.
     * @return A list of {@link Car} entities matching the price group. Can be empty.
     */
    List<Car> getCarsByPriceGroup(PriceGroup priceGroup);

    /**
     * Retrieves a list of all cars that are not soft-deleted and are currently marked as available.
     *
     * @return A list of available and non-deleted {@link Car} entities. Can be empty.
     */
    List<Car> findAllAvailableAndNonDeleted();

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
     * Retrieves a list of available (and non-deleted) cars belonging to a specific price group.
     * This seems to be a duplicate of {@link #getAvailableCarsByPrice(PriceGroup)}.
     *
     * @param priceGroup The {@link PriceGroup} to filter by.
     * @return A list of available {@link Car} entities matching the price group. Can be empty.
     * @deprecated Prefer {@link #getAvailableCarsByPrice(PriceGroup)} if functionality is identical.
     */
    @Deprecated
    List<Car> findAllAvailableByPriceGroup(PriceGroup priceGroup);
}