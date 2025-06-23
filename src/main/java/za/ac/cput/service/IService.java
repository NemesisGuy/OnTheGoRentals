package za.ac.cput.service;

import java.util.List;

/**
 * IService.java
 * A generic interface defining common CRUD (Create, Read, Update, Delete) operations.
 * This interface can be extended by more specific service interfaces for different entities.
 *
 * @param <T>  The type of the entity managed by the service.
 * @param <ID> The type of the ID of the entity.
 *             <p>
 *             Author: Peter Buckingham (220165289)
 *             Date: [Original Date of IService creation - Please specify if known]
 *             Updated by: Peter Buckingham
 *             Updated: 2025-05-29
 */
public interface IService<T, ID> {

    /**
     * Creates a new entity.
     *
     * @param t The entity object to create.
     * @return The created entity, typically with its ID populated.
     */
    T create(T t);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return The found entity, or {@code null} if no entity exists with the given ID.
     */
    T read(ID id);

    /**
     * Updates an existing entity.
     *
     * @param t The entity object with updated information. It should have its ID set
     *          to identify the record to update.
     * @return The updated entity, or {@code null} if the entity was not found for update.
     */
    T update(T t);

    /**
     * Deletes an entity by its ID.
     * This could be a hard delete or a soft delete depending on the implementation.
     *
     * @param id The ID of the entity to delete.
     * @return {@code true} if the entity was successfully deleted, {@code false} otherwise
     * (e.g., if the entity was not found).
     */
    boolean delete(ID id);
    /**
     * Retrieves all entities.
     *
     * @return A list of all entities managed by the service.
     */
    List<T> getAll();
}