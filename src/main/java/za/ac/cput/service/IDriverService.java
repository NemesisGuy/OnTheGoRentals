package za.ac.cput.service;

import za.ac.cput.domain.entity.Driver;

import java.util.List;
import java.util.UUID;

/**
 * IDriverService.java
 * Interface defining the contract for Driver related services.
 * Provides methods for CRUD operations on {@link Driver} entities.
 * <p>
 * Author: Peter Buckingham // Assuming based on consistent authorship
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface IDriverService { // Does not extend IService in your provided code

    /**
     * Creates a new driver.
     *
     * @param driver The {@link Driver} entity to create.
     * @return The persisted {@link Driver} entity.
     */
    Driver create(Driver driver);

    /**
     * Retrieves a driver by their internal integer ID.
     *
     * @param id The internal integer ID of the driver.
     * @return The {@link Driver} entity, or {@code null} if not found or soft-deleted.
     */
    Driver read(Integer id);

    /**
     * Updates an existing driver.
     *
     * @param driver The {@link Driver} entity with updated information. Its ID should be set.
     * @return The updated {@link Driver} entity, or {@code null} if not found.
     */
    Driver update(Driver driver);

    /**
     * Soft-deletes a driver by their internal integer ID.
     *
     * @param id The internal integer ID of the driver to delete.
     * @return {@code true} if successfully deleted, {@code false} otherwise.
     */
    boolean delete(Integer id);

    /**
     * Soft-deletes a driver by their UUID.
     *
     * @param uuid The UUID of the driver to delete.
     * @return {@code true} if successfully deleted, {@code false} otherwise.
     */
    boolean delete(UUID uuid);

    /**
     * Retrieves all non-deleted drivers.
     *
     * @return A list of all non-deleted {@link Driver} entities. Can be empty.
     */
    List<Driver> getAll();

    /**
     * Retrieves a driver by their UUID.
     * Note: Parameter name was 'Uuid' (uppercase U), changed to 'uuid' for convention.
     *
     * @param uuid The UUID of the driver (parameter name changed to 'uuid' for convention).
     * @return The {@link Driver} entity, or {@code null} if not found or soft-deleted.
     */
    Driver read(UUID uuid); // Parameter name 'uuid' (lowercase)
}