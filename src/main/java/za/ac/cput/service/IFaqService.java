package za.ac.cput.service;

import za.ac.cput.domain.entity.Faq;

import java.util.List;
import java.util.UUID;

/**
 * IFaqService.java
 * Interface defining the contract for FAQ (Frequently Asked Questions) services.
 * Extends the generic {@link IService} for basic CRUD operations and adds
 * specific methods for retrieving FAQs.
 * <p>
 * Author: Aqeel Hanslo (219374422) // Assuming from previous context
 * Date: [Original Date - e.g., 29 August 2023 from previous context]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface IFaqService extends IService<Faq, Integer> {

    // create(Faq) is inherited from IService
    // read(Integer) is inherited from IService
    // update(Faq) is inherited from IService
    // delete(Integer) is inherited from IService

    /**
     * Retrieves an FAQ entry by its UUID.
     *
     * @param uuid The UUID of the FAQ.
     * @return The {@link Faq} entity, or {@code null} if not found or soft-deleted.
     */
    Faq read(UUID uuid);

    /**
     * Retrieves all non-deleted FAQ entries.
     *
     * @return A list of all non-deleted {@link Faq} entities. Can be empty.
     */
    List<Faq> getAll();
}