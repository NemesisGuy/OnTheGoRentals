package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.Feedback;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.FeedbackRepository;
import za.ac.cput.service.IFeedbackService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * FeedbackServiceImpl.java
 * Implementation of the {@link IFeedbackService} interface.
 * Manages Feedback entities, including CRUD operations.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 * <p>
 * Author: Peter Buckingham // Assuming based on consistent authorship
 * Date: [Original Date - e.g., 2025-05-15 from previous context]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service
public class FeedbackServiceImpl implements IFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackServiceImpl.class);
    private final FeedbackRepository feedbackRepository; // Renamed from repository, using interface

    /**
     * Constructs the FeedbackServiceImpl.
     *
     * @param feedbackRepository The repository for feedback persistence.
     */
    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) { // Changed to IFeedbackRepository
        this.feedbackRepository = feedbackRepository;
        log.info("FeedbackServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     * Creates a new feedback entry.
     * Ensures the entity is not marked as deleted and assigns a UUID if not present.
     */
    @Override
    @Transactional // Spring's Transactional
    public Feedback create(Feedback feedback) {
        log.info("Attempting to create new feedback. Name: '{}', Comment: {}",
                feedback.getName(), feedback.getComment()); // Assuming Feedback has getName and getRating

        Feedback entityToSave;
        if (feedback.getUuid() == null) {
            entityToSave = new Feedback.Builder().copy(feedback)
                    .setUuid(UUID.randomUUID())
                    .setDeleted(false)
                    .build();
            log.debug("Generated UUID '{}' for new feedback.", entityToSave.getUuid());
        } else {
            entityToSave = new Feedback.Builder().copy(feedback)
                    .setDeleted(false) // Ensure not deleted on creation
                    .build();
        }
        // Assuming other fields like submissionDate might be set by @PrePersist or are already on feedback

        Feedback savedFeedback = feedbackRepository.save(entityToSave);
        log.info("Successfully created feedback. ID: {}, UUID: '{}', Name: '{}'",
                savedFeedback.getId(), savedFeedback.getUuid(), savedFeedback.getName());
        return savedFeedback;
    }

    /**
     * {@inheritDoc}
     * Reads a feedback entry by its internal integer ID.
     */
    @Override
    public Feedback read(Integer id) {
        log.debug("Attempting to read feedback by ID: {}", id);
        Feedback feedback = feedbackRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (feedback == null) {
            log.warn("Feedback not found or is deleted for ID: {}", id);
        } else {
            log.debug("Feedback found for ID: {}. Name: '{}'", id, feedback.getName());
        }
        return feedback;
    }

    /**
     * {@inheritDoc}
     * Reads a feedback entry by its UUID.
     */
    @Override
    public Feedback read(UUID uuid) {
        log.debug("Attempting to read feedback by UUID: '{}'", uuid);
        Feedback feedback = feedbackRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (feedback == null) {
            log.warn("Feedback not found or is deleted for UUID: '{}'", uuid);
        } else {
            log.debug("Feedback found for UUID: '{}'. ID: {}", uuid, feedback.getId());
        }
        return feedback;
    }

    /**
     * {@inheritDoc}
     * Updates an existing feedback entry. Feedback is generally immutable once submitted by a user.
     * This method allows an admin to update it if business rules permit (e.g., correcting typos, categorizing).
     * The input {@code feedbackWithUpdates} should be the complete new state.
     *
     * @throws ResourceNotFoundException if the feedback with the given ID does not exist or is deleted.
     */
    @Override
    @Transactional // Spring's Transactional
    public Feedback update(Feedback feedbackWithUpdates) { // Parameter name implies it's the desired new state
        Integer feedbackId = feedbackWithUpdates.getId();
        log.info("Attempting to update feedback with ID: {}", feedbackId);

        if (feedbackId == null) {
            log.error("Update failed: Feedback ID cannot be null.");
            throw new IllegalArgumentException("Feedback ID cannot be null for update.");
        }
        // Check if the entity exists and is not deleted before updating
        if (!feedbackRepository.existsByIdAndDeletedFalse(feedbackId)) {
            log.warn("Update failed: Feedback not found or is deleted for ID: {}. System.out from original: 'debug update: false'", feedbackId);
            throw new ResourceNotFoundException("Feedback not found with ID: " + feedbackId + " for update.");
        }

        log.debug("Updating feedback ID: {}. New Name: '{}', New Comment: '{}'. System.out from original: 'update: true'",
                feedbackId, feedbackWithUpdates.getName(), feedbackWithUpdates.getComment());
        // 'feedbackWithUpdates' IS the new state, presumably built by the controller.

        Feedback savedFeedback = feedbackRepository.save(feedbackWithUpdates);
        log.info("Successfully updated feedback. ID: {}, UUID: '{}'",
                savedFeedback.getId(), savedFeedback.getUuid());
        return savedFeedback;
    }

    /**
     * {@inheritDoc}
     * Soft-deletes a feedback entry by its internal integer ID.
     */
    @Override
    @Transactional // Spring's Transactional
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete feedback with ID: {}", id);
        Optional<Feedback> feedbackOpt = feedbackRepository.findByIdAndDeletedFalse(id); // Fetch non-deleted first
        if (feedbackOpt.isPresent()) {
            Feedback feedback = feedbackOpt.get();
            Feedback deletedFeedback = new Feedback.Builder().copy(feedback).setDeleted(true).build();
            feedbackRepository.save(deletedFeedback);
            log.info("Successfully soft-deleted feedback with ID: {}", id);
            return true;
        }
        log.warn("Soft-delete failed: Feedback not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     * Retrieves all non-deleted feedback entries.
     */
    @Override
    public List<Feedback> getAll() {
        log.debug("Fetching all non-deleted feedback entries.");
        List<Feedback> feedbackList = feedbackRepository.findByDeletedFalse();
        log.debug("Found {} non-deleted feedback entries.", feedbackList.size());
        return feedbackList;
    }
}