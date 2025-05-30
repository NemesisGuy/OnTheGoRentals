package za.ac.cput.service;

import za.ac.cput.domain.entity.HelpCenter;

import java.util.List;
import java.util.UUID;

/**
 * IHelpCenterService.java
 * Interface defining the contract for Help Center topic/article services.
 * Extends the generic {@link IService} for basic CRUD and adds methods for
 * category-based filtering and UUID-based retrieval.
 *
 * Author: Aqeel Hanslo (219374422) // Assuming from previous context
 * Date: [Original Date - e.g., 29 August 2023 from previous context]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface IHelpCenterService extends IService<HelpCenter, Integer> {

    // create(HelpCenter) is inherited from IService
    // read(Integer) is inherited from IService
    // update(HelpCenter) is inherited from IService
    // delete(Integer) is inherited from IService

    /**
     * Retrieves all non-deleted help topics/articles.
     *
     * @return A list of all non-deleted {@link HelpCenter} entities. Can be empty.
     */
    List<HelpCenter> getAll();

    /**
     * Retrieves non-deleted help topics/articles filtered by a specific category.
     *
     * @param category The category string to filter by (case-sensitive).
     * @return A list of {@link HelpCenter} entities matching the category. Can be empty.
     */
    List<HelpCenter> findByCategory(String category);

    /**
     * Retrieves help topics/articles by category.
     * This seems to be a duplicate or alternative naming for {@link #findByCategory(String)}.
     *
     * @param category The category string to filter by.
     * @return A list of {@link HelpCenter} entities matching the category.
     * @deprecated Prefer {@link #findByCategory(String)} for clarity and consistency.
     */
    @Deprecated
    List<HelpCenter> read(String category);

    /**
     * Retrieves a help topic/article by its UUID.
     *
     * @param uuid The UUID of the help topic/article.
     * @return The {@link HelpCenter} entity, or {@code null} if not found or soft-deleted.
     */
    HelpCenter read(UUID uuid);
}