package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Spring's Transactional
import za.ac.cput.domain.entity.ContactUs;
import za.ac.cput.repository.ContactUsRepository; // Assuming interface name IContactUsRepository
import za.ac.cput.exception.ResourceNotFoundException; // For consistency
import za.ac.cput.service.IContactUsService;

import java.time.LocalDateTime; // For submission date if not set by @PrePersist
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ContactUsServiceImpl.java
 * Implementation of the {@link IContactUsService} interface.
 * Manages "Contact Us" submissions, including CRUD operations.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 *
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service("contactUsServiceImpl") // Explicit bean name, class name adjusted for consistency
public class ContactUsServiceImpl implements IContactUsService {

    private static final Logger log = LoggerFactory.getLogger(ContactUsServiceImpl.class);
    private final ContactUsRepository contactUsRepository; // Renamed from repository, using interface

    /**
     * Constructs the ContactUsServiceImpl.
     *
     * @param contactUsRepository The repository for "Contact Us" data persistence.
     */
    @Autowired
    public ContactUsServiceImpl(ContactUsRepository contactUsRepository) { // Changed to IContactUsRepository
        this.contactUsRepository = contactUsRepository;
        log.info("ContactUsServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     * Creates a new "Contact Us" submission.
     * Ensures the entity is not marked as deleted, assigns a UUID if not present,
     * and sets the submission date if not already set.
     */
    @Override
    @Transactional // Spring's Transactional
    public ContactUs create(ContactUs contactUs) {
        log.info("Attempting to create new Contact Us submission. Name: '{}', Email: '{}'",
                contactUs.getFirstName() + contactUs.getLastName(), contactUs.getEmail()); // Assuming ContactUs has getName and getEmail

        ContactUs entityToSave;
        ContactUs.Builder builder = new ContactUs.Builder().copy(contactUs);

        if (contactUs.getUuid() == null) {
            builder.setUuid(UUID.randomUUID());
            log.debug("Generated UUID '{}' for new Contact Us submission.", builder.build().getUuid()); // Log after potential build
        }
        builder.setDeleted(false); // Ensure not deleted on creation

        if (contactUs.getCreatedAt() == null) { // Assuming a submissionDate field
            builder.setCreatedAt(LocalDateTime.now());
            log.debug("Set current date and time as submission date for new Contact Us entry.");
        }
        entityToSave = builder.build();

        ContactUs savedContactUs = contactUsRepository.save(entityToSave);
        log.info("Successfully created Contact Us submission. ID: {}, UUID: '{}', Email: '{}'",
                savedContactUs.getId(), savedContactUs.getUuid(), savedContactUs.getEmail());
        return savedContactUs;
    }

    /**
     * {@inheritDoc}
     * Reads a "Contact Us" submission by its internal integer ID.
     */
    @Override
    public ContactUs read(Integer id) { // Changed parameter type to Integer
        log.debug("Attempting to read Contact Us submission by ID: {}", id);
        ContactUs contactUs = contactUsRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (contactUs == null) {
            log.warn("Contact Us submission not found or is deleted for ID: {}", id);
        } else {
            log.debug("Contact Us submission found for ID: {}. Email: '{}'", id, contactUs.getEmail());
        }
        return contactUs;
    }

    /**
     * {@inheritDoc}
     * Reads a "Contact Us" submission by its UUID.
     */
    @Override
    public ContactUs read(UUID uuid) {
        log.debug("Attempting to read Contact Us submission by UUID: '{}'", uuid);
        ContactUs contactUs = contactUsRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (contactUs == null) {
            log.warn("Contact Us submission not found or is deleted for UUID: '{}'", uuid);
        } else {
            log.debug("Contact Us submission found for UUID: '{}'. ID: {}", uuid, contactUs.getId());
        }
        return contactUs;
    }

    /**
     * {@inheritDoc}
     * Updates an existing "Contact Us" submission. This is typically an admin action
     * (e.g., to mark as responded, add internal notes).
     * The input {@code contactUsWithUpdates} should be the complete new state.
     * @throws ResourceNotFoundException if the submission with the given ID does not exist or is deleted.
     */
    @Override
    @Transactional // Spring's Transactional
    public ContactUs update(ContactUs contactUsWithUpdates) { // Parameter name implies it's the desired new state
        Integer submissionId = contactUsWithUpdates.getId();
        log.info("Attempting to update Contact Us submission with ID: {}", submissionId);

        if (submissionId == null) {
            log.error("Update failed: ContactUs ID cannot be null.");
            throw new IllegalArgumentException("ContactUs ID cannot be null for update.");
        }
        // Ensure the entity exists and is not deleted before updating
        if (!contactUsRepository.existsByIdAndDeletedFalse(submissionId)) {
            log.warn("Update failed: Contact Us submission not found or is deleted for ID: {}", submissionId);
            throw new ResourceNotFoundException("Contact Us submission not found with ID: " + submissionId + " for update.");
        }

        log.debug("Updating Contact Us submission ID: {}. New Message (first 50 chars): '{}'",
                submissionId,
                contactUsWithUpdates.getMessage() != null && contactUsWithUpdates.getMessage().length() > 50 ?
                        contactUsWithUpdates.getMessage().substring(0, 50) + "..." : contactUsWithUpdates.getMessage());
        // 'contactUsWithUpdates' IS the new state, built by the controller using the builder.

        ContactUs savedContactUs = contactUsRepository.save(contactUsWithUpdates);
        log.info("Successfully updated Contact Us submission. ID: {}, UUID: '{}'",
                savedContactUs.getId(), savedContactUs.getUuid());
        return savedContactUs;
    }

    /**
     * {@inheritDoc}
     * Soft-deletes a "Contact Us" submission by its internal integer ID.
     */
    @Transactional
    @Override
    public boolean delete(Integer id) { // Changed parameter type to Integer
        log.info("Attempting to soft-delete Contact Us submission with ID: {}", id);
        Optional<ContactUs> contactUsOpt = contactUsRepository.findByIdAndDeletedFalse(id); // Fetch non-deleted first
        if (contactUsOpt.isPresent()) {
            ContactUs contactUs = contactUsOpt.get();
            ContactUs deletedContactUs = new ContactUs.Builder().copy(contactUs).setDeleted(true).build();
            contactUsRepository.save(deletedContactUs);
            log.info("Successfully soft-deleted Contact Us submission with ID: {}", id);
            return true;
        }
        log.warn("Soft-delete failed: Contact Us submission not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     * Retrieves all non-deleted "Contact Us" submissions.
     */
    @Override
    public List<ContactUs> getAll() { // Changed return type to List<ContactUs>
        log.debug("Fetching all non-deleted Contact Us submissions.");
        List<ContactUs> feedbackList = contactUsRepository.findByDeletedFalse();
        log.debug("Found {} non-deleted Contact Us submissions.", feedbackList.size());
        return feedbackList;
    }
}