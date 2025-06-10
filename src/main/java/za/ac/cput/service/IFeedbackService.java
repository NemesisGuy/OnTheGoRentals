package za.ac.cput.service;

import za.ac.cput.domain.entity.Feedback;

import java.util.List;
import java.util.UUID;

/**
 * IFeedbackService.java
 * Interface defining the contract for Feedback related services.
 * Provides methods for creating feedback submissions and managing them.
 * <p>
 * Author: Peter Buckingham // Assuming based on consistent authorship
 * Date: [Original Date - e.g., 2025-05-15 from previous context]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface IFeedbackService { // Does not extend IService in your provided code

    /**
     * Creates a new feedback submission.
     *
     * @param feedback The {@link Feedback} entity to create.
     * @return The persisted {@link Feedback} entity.
     */
    Feedback create(Feedback feedback);

    /**
     * Retrieves a feedback submission by its internal integer ID.
     *
     * @param id The internal integer ID.
     * @return The {@link Feedback} entity, or {@code null} if not found or soft-deleted.
     */
    Feedback read(Integer id);

    /**
     * Retrieves a feedback submission by its UUID.
     *
     * @param uuid The UUID of the submission.
     * @return The {@link Feedback} entity, or {@code null} if not found or soft-deleted.
     */
    Feedback read(UUID uuid);

    /**
     * Updates an existing feedback submission.
     * (Note: Feedback is often immutable after user submission; this might be an admin function).
     *
     * @param feedback The {@link Feedback} entity with updated information. Its ID should be set.
     * @return The updated {@link Feedback} entity, or {@code null} if not found.
     */
    Feedback update(Feedback feedback);

    /**
     * Soft-deletes a feedback submission by its internal integer ID.
     *
     * @param id The internal integer ID of the submission to delete.
     * @return {@code true} if successfully deleted, {@code false} otherwise.
     */
    boolean delete(Integer id);

    /**
     * Retrieves all non-deleted feedback submissions.
     *
     * @return A list of all non-deleted {@link Feedback} entities. Can be empty.
     */
    List<Feedback> getAll();
}