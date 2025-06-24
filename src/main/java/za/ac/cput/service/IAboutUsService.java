package za.ac.cput.service;

import za.ac.cput.domain.entity.AboutUs;

import java.util.List;
import java.util.UUID;

/**
 * IAboutUsService.java
 * Interface defining the contract for "About Us" page content services.
 * Provides methods for CRUD operations on {@link AboutUs} entities.
 * <p>
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface IAboutUsService {

    /**
     * Creates a new "About Us" entry.
     *
     * @param aboutUs The {@link AboutUs} entity to create.
     * @return The persisted {@link AboutUs} entity.
     */
    AboutUs create(AboutUs aboutUs);

    /**
     * Retrieves an "About Us" entry by its internal integer ID.
     *
     * @param id The internal integer ID.
     * @return The {@link AboutUs} entity, or {@code null} if not found or soft-deleted.
     */
    AboutUs read(int id); // Consider changing to Integer for consistency with IService

    /**
     * Retrieves an "About Us" entry by its UUID.
     *
     * @param uuid The UUID of the entry.
     * @return The {@link AboutUs} entity, or {@code null} if not found or soft-deleted.
     */
    AboutUs read(UUID uuid);

    /**
     * Updates an existing "About Us" entry.
     *
     * @param aboutUs The {@link AboutUs} entity with updated information. Its ID should be set.
     * @return The updated {@link AboutUs} entity, or {@code null} if not found.
     */
    AboutUs update(AboutUs aboutUs);

    /**
     * Soft-deletes an "About Us" entry by its internal integer ID.
     *
     * @param id The internal integer ID of the entry to delete.
     * @return {@code true} if successfully deleted, {@code false} otherwise.
     */
    boolean delete(int id); // Consider changing to Integer

    /**
     * Retrieves all non-deleted "About Us" entries.
     *
     * @return A list of all non-deleted {@link AboutUs} entities. Can be empty.
     */
    List<AboutUs> getAll();
}