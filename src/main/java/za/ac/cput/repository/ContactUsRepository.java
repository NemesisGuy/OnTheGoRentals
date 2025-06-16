package za.ac.cput.repository;
/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.ContactUs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {
    /**
     * Finds all ContactUs entries that are not marked as deleted.
     *
     * @return A list of non-deleted ContactUs entries.
     */
    List<ContactUs> findByDeletedFalse();

    /**
     * Finds a ContactUs entry by its internal ID if it is not marked as deleted.
     *
     * @param id The internal integer ID of the ContactUs entry.
     * @return An Optional containing the ContactUs entry if found and not deleted, otherwise empty.
     */
    Optional<ContactUs> findByIdAndDeletedFalse(int id);

    /**
     * Finds a ContactUs entry by its UUID if it is not marked as deleted.
     *
     * @param uuid The UUID of the ContactUs entry.
     * @return An Optional containing the ContactUs entry if found and not deleted, otherwise empty.
     */
    Optional<ContactUs> findByUuidAndDeletedFalse(UUID uuid);

    /**
     * Soft-deletes a ContactUs entry by its internal ID.
     *
     * @param id The internal integer ID of the ContactUs entry to delete.
     * @return true if the entry was found and soft-deleted, false otherwise.
     */
    boolean existsByIdAndDeletedFalse(Integer id);
    //Optional<Object> findById();
}
