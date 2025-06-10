package za.ac.cput.service;

import za.ac.cput.domain.settings.Settings;

/**
 * ISettingsService.java
 * Interface defining the contract for application settings services.
 * Manages CRUD operations for {@link Settings} entities, which typically
 * represent a singleton-like configuration record.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface ISettingsService { // Does not extend IService in your provided code

    /**
     * Creates a new settings entry. Typically, there's only one such entry in the system.
     *
     * @param settings The {@link Settings} entity to create.
     * @return The persisted {@link Settings} entity.
     */
    Settings create(Settings settings);

    /**
     * Retrieves the settings entry by its internal integer ID (usually a fixed ID like 1).
     *
     * @param id The internal integer ID of the settings entry.
     * @return The {@link Settings} entity, or {@code null} if not found or soft-deleted.
     */
    Settings read(Integer id);

    /**
     * Updates the existing settings entry.
     *
     * @param settings The {@link Settings} entity with updated information. Its ID should be set.
     * @return The updated {@link Settings} entity, or {@code null} if not found.
     */
    Settings update(Settings settings);

    /**
     * Soft-deletes the settings entry by its internal integer ID.
     *
     * @param id The internal integer ID of the settings entry to delete.
     * @return {@code true} if successfully deleted, {@code false} otherwise.
     */
    boolean delete(Integer id);

    /**
     * Retrieves all non-deleted settings entries (typically returns a list with one item).
     *
     * @return An iterable collection of non-deleted {@link Settings} entities.
     */
    Iterable<Settings> getAll();
}