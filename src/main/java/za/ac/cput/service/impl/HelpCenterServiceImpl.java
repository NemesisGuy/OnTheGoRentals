package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.HelpCenter;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IHelpCenterRepository;
import za.ac.cput.service.IHelpCenterService;

import java.util.*;

/**
 * HelpCenterServiceImpl.java
 * Implementation of the {@link IHelpCenterService} interface.
 * Manages Help Center topics/articles, including CRUD operations and category-based filtering.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 * <p>
 * Author: Aqeel Hanslo (219374422) // Assuming from previous context
 * Date: [Original Date - Please specify if known, e.g., 29 August 2023]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service("helpCenterServiceImpl") // Explicit bean name, class name changed
public class HelpCenterServiceImpl implements IHelpCenterService {

    private static final Logger log = LoggerFactory.getLogger(HelpCenterServiceImpl.class);
    private final IHelpCenterRepository helpCenterRepository; // Renamed from repository

    /**
     * Constructs the HelpCenterServiceImpl.
     *
     * @param helpCenterRepository The repository for Help Center data persistence.
     */
    @Autowired
    public HelpCenterServiceImpl(IHelpCenterRepository helpCenterRepository) {
        this.helpCenterRepository = helpCenterRepository;
        log.info("HelpCenterServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     * Creates a new Help Center topic/article.
     * Ensures the entity is not marked as deleted and assigns a UUID if not present.
     */
    @Override
    @Transactional // Spring's Transactional
    public HelpCenter create(HelpCenter helpCenter) {
        log.info("Attempting to create new Help Center topic. Question: '{}'", helpCenter.getContent());
        // Ensure UUID and deleted flag are set for new entities if not handled by @PrePersist
        HelpCenter entityToSave;
        if (helpCenter.getUuid() == null) {
            entityToSave = new HelpCenter.Builder().copy(helpCenter)
                    .setUuid(UUID.randomUUID())
                    .setDeleted(false)
                    .build();
            log.debug("Generated UUID '{}' for new Help Center topic.", entityToSave.getUuid());
        } else {
            entityToSave = new HelpCenter.Builder().copy(helpCenter)
                    .setDeleted(false) // Ensure not deleted on creation
                    .build();
        }

        HelpCenter savedHelpCenter = helpCenterRepository.save(entityToSave);
        log.info("Successfully created Help Center topic. ID: {}, UUID: '{}', Question: '{}'",
                savedHelpCenter.getId(), savedHelpCenter.getUuid(), savedHelpCenter.getContent());
        return savedHelpCenter;
    }

    /**
     * {@inheritDoc}
     * Reads a Help Center topic/article by its internal integer ID.
     */
    @Override
    public HelpCenter read(Integer id) {
        log.debug("Attempting to read Help Center topic by ID: {}", id);
        HelpCenter helpCenter = helpCenterRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (helpCenter == null) {
            log.warn("Help Center topic not found or is deleted for ID: {}", id);
        } else {
            log.debug("Help Center topic found for ID: {}. Question: '{}'", id, helpCenter.getContent());
        }
        return helpCenter;
    }

    /**
     * {@inheritDoc}
     * Reads a Help Center topic/article by its UUID.
     */
    @Override
    public HelpCenter read(UUID uuid) {
        log.debug("Attempting to read Help Center topic by UUID: '{}'", uuid);
        HelpCenter helpCenter = helpCenterRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (helpCenter == null) {
            log.warn("Help Center topic not found or is deleted for UUID: '{}'", uuid);
        } else {
            log.debug("Help Center topic found for UUID: '{}'. ID: {}", uuid, helpCenter.getId());
        }
        return helpCenter;
    }

    /**
     * {@inheritDoc}
     * Updates an existing Help Center topic/article.
     * The input {@code helpCenterWithUpdates} should be the complete new state.
     *
     * @throws ResourceNotFoundException if the topic with the given ID does not exist.
     */
    @Override
    @Transactional // Spring's Transactional
    public HelpCenter update(HelpCenter helpCenterWithUpdates) { // Parameter name implies it's the desired new state
        Integer topicId = helpCenterWithUpdates.getId();
        log.info("Attempting to update Help Center topic with ID: {}", topicId);
        if (topicId == null) {
            log.error("Update failed: HelpCenter ID cannot be null.");
            throw new IllegalArgumentException("HelpCenter ID cannot be null for update.");
        }
        if (!helpCenterRepository.existsByIdAndDeletedFalse(topicId)) {
            log.warn("Update failed: Help Center topic not found or is deleted for ID: {}", topicId);
            throw new ResourceNotFoundException("HelpCenter topic not found with ID: " + topicId + " for update.");
        }

        log.debug("Updating Help Center topic ID: {}. New Question: '{}', New Category: '{}'",
                topicId, helpCenterWithUpdates.getContent(), helpCenterWithUpdates.getCategory());
        // 'helpCenterWithUpdates' IS the new state. Controller should have built it.

        HelpCenter savedHelpCenter = helpCenterRepository.save(helpCenterWithUpdates);
        log.info("Successfully updated Help Center topic. ID: {}, UUID: '{}'",
                savedHelpCenter.getId(), savedHelpCenter.getUuid());
        return savedHelpCenter;
    }

    /**
     * {@inheritDoc}
     * Soft-deletes a Help Center topic/article by its internal integer ID.
     */
    @Override
    @Transactional // Spring's Transactional
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete Help Center topic with ID: {}", id);
        Optional<HelpCenter> helpCenterOpt = helpCenterRepository.findByIdAndDeletedFalse(id);
        if (helpCenterOpt.isPresent()) {
            HelpCenter helpCenter = helpCenterOpt.get();
            HelpCenter deletedHelpCenter = new HelpCenter.Builder().copy(helpCenter).setDeleted(true).build();
            helpCenterRepository.save(deletedHelpCenter);
            log.info("Successfully soft-deleted Help Center topic with ID: {}", id);
            return true;
        }
        log.warn("Soft-delete failed: Help Center topic not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     * Retrieves all non-deleted Help Center topics/articles.
     */
    @Override
    public List<HelpCenter> getAll() {
        log.debug("Fetching all non-deleted Help Center topics.");
        List<HelpCenter> allHelpCenters = helpCenterRepository.findByDeletedFalse();
        log.debug("Found {} non-deleted Help Center topics.", allHelpCenters.size());
        return allHelpCenters;
    }

    /**
     * {@inheritDoc}
     * Retrieves all non-deleted Help Center topics/articles filtered by category.
     */
    @Override
    public List<HelpCenter> findByCategory(String category) {
        log.debug("Fetching non-deleted Help Center topics by category: '{}'", category);
        if (category == null || category.trim().isEmpty()) {
            log.warn("Category for findByCategory is null or empty. Returning empty list.");
            return Collections.emptyList();
        }
        List<HelpCenter> topics = helpCenterRepository.findByCategoryAndDeletedFalse(category);
        log.debug("Found {} non-deleted Help Center topics for category: '{}'", topics.size(), category);
        return topics;
    }

    /**
     * Retrieves Help Center topics/articles by category (duplicate of findByCategory, kept for compatibility if used).
     *
     * @param category The category to filter by.
     * @return A list of matching {@link HelpCenter} entities.
     * @deprecated Prefer {@link #findByCategory(String)} for clarity.
     */
    @Override
    @Deprecated
    public List<HelpCenter> read(String category) {
        log.warn("Deprecated read(String category) method called. Use findByCategory(String) instead. Category: '{}'", category);
        return findByCategory(category); // Delegate to the preferred method
    }

    /**
     * Retrieves all Help Center topics/articles by category (seems like a duplicate of findByCategory, using specific repo method).
     *
     * @param category The category to filter by.
     * @return An ArrayList of matching {@link HelpCenter} entities.
     * @deprecated Prefer {@link #findByCategory(String)} using the more standard repository method name.
     * If `findAllByCategoryAndDeletedFalse` has different semantics, this should be clarified.
     */
    @Deprecated
    public ArrayList<HelpCenter> getAllByCategory(String category) {
        log.warn("Deprecated getAllByCategory(String) method called. Check if repository.findAllByCategoryAndDeletedFalse is different from findByCategoryAndDeletedFalse. Category: '{}'", category);
        if (category == null || category.trim().isEmpty()) {
            log.warn("Category for getAllByCategory is null or empty. Returning empty list.");
            return new ArrayList<>();
        }
        // Assuming this method exists and might have different semantics, otherwise it's redundant.
        ArrayList<HelpCenter> topics = helpCenterRepository.findAllByCategoryAndDeletedFalse(category);
        log.debug("Found {} non-deleted Help Center topics for category '{}' using getAllByCategory.", topics.size(), category);
        return topics;
    }
}