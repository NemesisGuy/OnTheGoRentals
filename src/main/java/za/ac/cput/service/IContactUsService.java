package za.ac.cput.service;

import za.ac.cput.domain.entity.ContactUs;

import java.util.List; // Use List interface for getAll
import java.util.UUID;

/**
 * IContactUsService.java
 * Interface defining the contract for "Contact Us" submission services.
 * This includes creating new submissions and managing existing ones (CRUD operations).
 *
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
public interface IContactUsService {

    /**
     * Creates a new "Contact Us" submission.
     *
     * @param contactUs The {@link ContactUs} entity to create.
     * @return The persisted {@link ContactUs} entity.
     */
    ContactUs create(ContactUs contactUs);

    /**
     * Retrieves a "Contact Us" submission by its internal integer ID,
     * if it's not marked as deleted.
     *
     * @param id The internal integer ID of the submission.
     * @return The {@link ContactUs} entity, or {@code null} if not found or deleted.
     */
    ContactUs read(Integer id); // Changed from int to Integer for consistency

    /**
     * Retrieves a "Contact Us" submission by its UUID,
     * if it's not marked as deleted.
     *
     * @param uuid The UUID of the submission.
     * @return The {@link ContactUs} entity, or {@code null} if not found or deleted.
     */
    ContactUs read(UUID uuid);

    /**
     * Updates an existing "Contact Us" submission.
     * Typically, feedback/contact submissions are not updated by users.
     * This method might be used by administrators for corrections or status updates.
     *
     * @param contactUs The {@link ContactUs} entity with updated information.
     *                  The entity should have its ID set to identify the record to update.
     * @return The updated and persisted {@link ContactUs} entity, or {@code null} if not found.
     */
    ContactUs update(ContactUs contactUs);

    /**
     * Soft-deletes a "Contact Us" submission by its internal integer ID.
     *
     * @param id The internal integer ID of the submission to delete.
     * @return {@code true} if the submission was found and soft-deleted, {@code false} otherwise.
     */
    boolean delete(Integer id); // Changed from int to Integer

    /**
     * Retrieves all "Contact Us" submissions that are not marked as deleted.
     *
     * @return A list of non-deleted {@link ContactUs} entities.
     */
    List<ContactUs> getAll(); // Changed return type to List
}