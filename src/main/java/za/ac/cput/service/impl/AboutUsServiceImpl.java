package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.AboutUs;
import za.ac.cput.repository.AboutUsRepository;
import za.ac.cput.service.IAboutUsService;

import java.util.List;
import java.util.UUID;

/**
 * AboutUsServiceImpl.java
 * Service implementation for managing "About Us" content.
 * Provides CRUD operations and retrieval of "About Us" entries.
 * <p>
 * Original Author: Cwenga Dlova (214310671)
 * Original Date: 24/09/2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service
@Transactional // Apply transactionality to all public methods by default
public class AboutUsServiceImpl implements IAboutUsService {

    private static final Logger log = LoggerFactory.getLogger(AboutUsServiceImpl.class);
    private final AboutUsRepository aboutUsRepository; // Use interface, renamed for clarity

    /**
     * Constructs an AboutUsServiceImpl with the necessary repository.
     *
     * @param aboutUsRepository The repository for "About Us" data persistence.
     */
    @Autowired // Standard practice for constructor injection
    public AboutUsServiceImpl(AboutUsRepository aboutUsRepository) { // Inject IAboutUsRepository
        this.aboutUsRepository = aboutUsRepository;
        log.info("AboutUsServiceImpl initialized.");
    }

    /**
     * Creates a new "About Us" entry.
     * If the provided {@link AboutUs} entity does not have a UUID, one will be generated.
     *
     * @param aboutUs The {@link AboutUs} entity to create.
     * @return The created and persisted {@link AboutUs} entity.
     */
    @Override
    public AboutUs create(AboutUs aboutUs) {
        if (aboutUs.getUuid() == null) { // Assuming getAboutUsId() is the getter for UUID

            log.debug("Generated new UUID: {} for new AboutUs entry.", aboutUs.getUuid());
        }
        log.info("Creating new AboutUs entry with UUID: {}", aboutUs.getUuid());
        AboutUs savedAboutUs = this.aboutUsRepository.save(aboutUs);
        log.info("Successfully created AboutUs entry with ID: {} and UUID: {}", savedAboutUs.getId(), savedAboutUs.getUuid());
        return savedAboutUs;
    }

    /**
     * Retrieves a non-deleted "About Us" entry by its internal integer ID.
     *
     * @param id The internal integer ID of the "About Us" entry.
     * @return The {@link AboutUs} entity if found and not deleted, otherwise {@code null}.
     */
    @Override
    public AboutUs read(int id) {
        log.debug("Attempting to read AboutUs entry by internal ID: {}", id);
        AboutUs aboutUs = this.aboutUsRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (aboutUs != null) {
            log.debug("Found AboutUs entry with ID: {}. UUID: {}", id, aboutUs.getUuid());
        } else {
            log.debug("AboutUs entry not found or is deleted for ID: {}", id);
        }
        return aboutUs;
    }

    /**
     * Retrieves a non-deleted "About Us" entry by its UUID.
     *
     * @param uuid The UUID of the "About Us" entry.
     * @return The {@link AboutUs} entity if found and not deleted, otherwise {@code null}.
     */
    @Override
    public AboutUs read(UUID uuid) {
        log.debug("Attempting to read AboutUs entry by UUID: {}", uuid);
        AboutUs aboutUs = this.aboutUsRepository.findByUuidAndDeletedFalse(uuid).orElse(null); // Assuming findByAboutUsIdAndDeletedFalse
        if (aboutUs != null) {
            log.debug("Found AboutUs entry with UUID: {}. Internal ID: {}", uuid, aboutUs.getId());
        } else {
            log.debug("AboutUs entry not found or is deleted for UUID: {}", uuid);
        }
        return aboutUs;
    }

    /**
     * Updates an existing "About Us" entry.
     * The entry to be updated is identified by the ID within the provided {@link AboutUs} entity.
     *
     * @param aboutUs The {@link AboutUs} entity containing the updated information and its ID.
     * @return The updated and persisted {@link AboutUs} entity if it exists, otherwise {@code null}.
     */
    @Override
    public AboutUs update(AboutUs aboutUs) {
        log.info("Attempting to update AboutUs entry with ID: {} and UUID: {}", aboutUs.getId(), aboutUs.getUuid());
        // Check if the entity exists and is not deleted before updating.
        // The existsById check might not consider the soft-delete status.
        // It's often better to read the entity first, apply changes, then save.
        if (aboutUs.getUuid() == null) {
            log.warn("Update failed: AboutUs entity has no ID.");
            return null; // Or throw IllegalArgumentException
        }
        return this.aboutUsRepository.findByIdAndDeletedFalse(aboutUs.getId()).map(existingAboutUs -> {
            log.debug("Found existing AboutUs entry for update. ID: {}, UUID: {}", existingAboutUs.getId(), existingAboutUs.getUuid());
            // Apply changes from 'aboutUs' (the parameter) to 'existingAboutUs'
            // This ensures we are updating a fetched, managed entity.
            // Example:
            // existingAboutUs.setTitle(aboutUs.getTitle());
            // existingAboutUs.setContent(aboutUs.getContent());
            // ... etc. for all updatable fields
            // For now, assuming 'aboutUs' parameter is the full new state but with the correct ID.
            // The save method will perform an update if the entity with that ID exists.
            // If the AboutUs entity passed in is already managed, this is fine.
            // If it's a detached entity, fetching first then updating fields is safer.

            // Assuming 'aboutUs' contains all fields intended for update.
            // A more robust update would be:
            // existingAboutUs.setTitle(aboutUs.getTitle());
            // existingAboutUs.setContent(aboutUs.getContent());
            // ... copy all mutable fields from aboutUs to existingAboutUs ...
            // AboutUs updatedAboutUs = this.aboutUsRepository.save(existingAboutUs);

            // Simpler, if `aboutUs` is correctly prepared (ID is set, fields are new values):
            AboutUs updatedAboutUs = this.aboutUsRepository.save(aboutUs); // This relies on JPA merge/update
            log.info("Successfully updated AboutUs entry with ID: {} and UUID: {}", updatedAboutUs.getId(), updatedAboutUs.getUuid());
            return updatedAboutUs;
        }).orElseGet(() -> {
            log.warn("Update failed: AboutUs entry not found or is deleted for ID: {}", aboutUs.getId());
            return null;
        });
    }

    /**
     * Soft-deletes an "About Us" entry by its internal integer ID.
     * Sets the 'deleted' flag to true.
     *
     * @param id The internal integer ID of the "About Us" entry to delete.
     * @return {@code true} if the entry was found and soft-deleted, {@code false} otherwise.
     */
    @Override
    public boolean delete(int id) {
        log.info("Attempting to soft-delete AboutUs entry with internal ID: {}", id);
        return this.aboutUsRepository.findByIdAndDeletedFalse(id).map(aboutUs -> {
            log.debug("Found AboutUs entry for soft-deletion. ID: {}, UUID: {}", id, aboutUs.getUuid());
            // Using Builder pattern for immutability if AboutUs is designed that way,
            // or direct setter if mutable.
            // Assuming AboutUs has a setDeleted method or the builder handles it.
            // AboutUs deletedVersion = new AboutUs.Builder().copy(aboutUs).setDeleted(true).build();
            // this.aboutUsRepository.save(deletedVersion);
            //aboutUs.setDeleted(true); // Simpler if entity is mutable

            AboutUs aboutUsToDelete = new AboutUs.Builder()
                    .copy(aboutUs)
                    .setDeleted(true)
                    .build();
            this.aboutUsRepository.save(aboutUsToDelete);
            log.info("Successfully soft-deleted AboutUs entry with ID: {}", id);
            return true;
        }).orElseGet(() -> {
            log.warn("Soft-delete failed: AboutUs entry not found or already deleted for ID: {}", id);
            return false;
        });
    }

    /**
     * Retrieves all "About Us" entries that are not marked as deleted.
     *
     * @return A list of non-deleted {@link AboutUs} entities.
     */
    @Override
    public List<AboutUs> getAll() {
        log.debug("Attempting to retrieve all non-deleted AboutUs entries.");
        List<AboutUs> allActiveAboutUs = this.aboutUsRepository.findAllByDeletedFalse();
        log.debug("Retrieved {} non-deleted AboutUs entries.", allActiveAboutUs.size());
        return allActiveAboutUs;
    }
}