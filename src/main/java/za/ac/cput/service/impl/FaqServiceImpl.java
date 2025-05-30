package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Spring's Transactional
import za.ac.cput.domain.entity.Faq;
import za.ac.cput.repository.IFaqRepository;
import za.ac.cput.exception.ResourceNotFoundException; // For consistency
import za.ac.cput.service.IFaqService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * FaqServiceImpl.java
 * Implementation of the {@link IFaqService} interface.
 * Manages FAQ (Frequently Asked Questions) entries, including CRUD operations.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 *
 * Author: Aqeel Hanslo (219374422) // Assuming from previous context
 * Date: [Original Date - Please specify if known, e.g., 29 August 2023]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service("faqServiceImpl") // Explicit bean name, class name changed
public class FaqServiceImpl implements IFaqService {

    private static final Logger log = LoggerFactory.getLogger(FaqServiceImpl.class);
    private final IFaqRepository faqRepository; // Renamed from repository

    /**
     * Constructs the FaqServiceImpl.
     *
     * @param faqRepository The repository for FAQ data persistence.
     */
    @Autowired
    public FaqServiceImpl(IFaqRepository faqRepository) {
        this.faqRepository = faqRepository;
        log.info("FaqServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     * Creates a new FAQ entry.
     * Ensures the entity is not marked as deleted and assigns a UUID if not present.
     */
    @Override
    @Transactional // Spring's Transactional
    public Faq create(Faq faq) {
        log.info("Attempting to create new FAQ. Question: '{}'", faq.getQuestion());
        Faq entityToSave;
        if (faq.getUuid() == null) {
            entityToSave = new Faq.Builder().copy(faq)
                    .setUuid(UUID.randomUUID())
                    .setDeleted(false)
                    .build();
            log.debug("Generated UUID '{}' for new FAQ.", entityToSave.getUuid());
        } else {
            entityToSave = new Faq.Builder().copy(faq)
                    .setDeleted(false)
                    .build();
        }

        Faq savedFaq = faqRepository.save(entityToSave);
        log.info("Successfully created FAQ. ID: {}, UUID: '{}', Question: '{}'",
                savedFaq.getId(), savedFaq.getUuid(), savedFaq.getQuestion());
        return savedFaq;
    }

    /**
     * {@inheritDoc}
     * Reads an FAQ entry by its internal integer ID.
     */
    @Override
    public Faq read(Integer id) { // Parameter name changed from integer for clarity
        log.debug("Attempting to read FAQ by ID: {}", id);
        Faq faq = faqRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (faq == null) {
            log.warn("FAQ not found or is deleted for ID: {}", id);
        } else {
            log.debug("FAQ found for ID: {}. Question: '{}'", id, faq.getQuestion());
        }
        return faq;
    }

    /**
     * {@inheritDoc}
     * Reads an FAQ entry by its UUID.
     */
    @Override
    public Faq read(UUID uuid) {
        log.debug("Attempting to read FAQ by UUID: '{}'", uuid);
        Faq faq = faqRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (faq == null) {
            log.warn("FAQ not found or is deleted for UUID: '{}'", uuid);
        } else {
            log.debug("FAQ found for UUID: '{}'. ID: {}", uuid, faq.getId());
        }
        return faq;
    }

    /**
     * {@inheritDoc}
     * Updates an existing FAQ entry.
     * The input {@code faqWithUpdates} should be the complete new state.
     * @throws ResourceNotFoundException if the FAQ with the given ID does not exist.
     */
    @Override
    @Transactional // Spring's Transactional
    public Faq update(Faq faqWithUpdates) { // Parameter name implies it's the desired new state
        Integer faqId = faqWithUpdates.getId();
        log.info("Attempting to update FAQ with ID: {}", faqId);
        if (faqId == null) {
            log.error("Update failed: FAQ ID cannot be null.");
            throw new IllegalArgumentException("FAQ ID cannot be null for update.");
        }
        if (!faqRepository.existsByIdAndDeletedFalse(faqId)) {
            log.warn("Update failed: FAQ not found or is deleted for ID: {}", faqId);
            throw new ResourceNotFoundException("FAQ not found with ID: " + faqId + " for update.");
        }

        log.debug("Updating FAQ ID: {}. New Question: '{}'", faqId, faqWithUpdates.getQuestion());
        // 'faqWithUpdates' IS the new state.

        Faq savedFaq = faqRepository.save(faqWithUpdates);
        log.info("Successfully updated FAQ. ID: {}, UUID: '{}'", savedFaq.getId(), savedFaq.getUuid());
        return savedFaq;
    }

    /**
     * {@inheritDoc}
     * Soft-deletes an FAQ entry by its internal integer ID.
     */
    @Override
    @Transactional // Spring's Transactional
    public boolean delete(Integer id) { // Parameter name changed from integer
        log.info("Attempting to soft-delete FAQ with ID: {}", id);
        Optional<Faq> faqOpt = faqRepository.findByIdAndDeletedFalse(id);
        if (faqOpt.isPresent()) {
            Faq faq = faqOpt.get();
            Faq deletedFaq = new Faq.Builder().copy(faq).setDeleted(true).build();
            faqRepository.save(deletedFaq);
            log.info("Successfully soft-deleted FAQ with ID: {}", id);
            return true;
        }
        log.warn("Soft-delete failed: FAQ not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     * Retrieves all non-deleted FAQ entries.
     */
    @Override
    public List<Faq> getAll() {
        log.debug("Fetching all non-deleted FAQs.");
        List<Faq> allFaqs = this.faqRepository.findByDeletedFalse();
        log.debug("Found {} non-deleted FAQs.", allFaqs.size());
        return allFaqs;
    }
}