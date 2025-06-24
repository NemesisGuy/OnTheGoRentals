package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.Feedback;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    /**
     * Finds a feedback entry by its internal integer ID, if it is not marked as deleted.
     * This method is used to retrieve feedback entries that are still active and not soft-deleted.
     *
     * @param id The internal integer ID of the feedback entry.
     * @return An {@link Optional} containing the {@link Feedback} entity if found and not deleted,
     */
    Optional<Feedback> findByIdAndDeletedFalse(Integer id);

    /**
     * Retrieves all feedback entries that are not marked as deleted.
     *
     * @return A list of {@link Feedback} entities that are active (not deleted).
     * This list can be empty if no active feedback entries exist.
     */
    List<Feedback> findByDeletedFalse();

    /**
     * Finds a feedback entry by its UUID, if it is not marked as deleted.
     *
     * @param uuid The UUID of the feedback entry.
     * @return An {@link Optional} containing the {@link Feedback} entity if found and not deleted, or empty if not found or deleted.
     */
    Optional<Feedback> findByUuidAndDeletedFalse(UUID uuid);

    /**
     * Checks if a feedback entry exists by its ID and is not marked as deleted.
     *
     * @param feedbackId The internal integer ID of the feedback entry.
     * @return {@code true} if the feedback entry exists and is not deleted, {@code false} otherwise.
     */
    boolean existsByIdAndDeletedFalse(Integer feedbackId);
}
