package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.AboutUs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AboutUsRepository extends JpaRepository<AboutUs, Integer> {
    /**
     * checks if an AboutUs entry exists by its internal integer ID and is not marked as deleted.
     *
     * @param id The internal integer ID of the AboutUs entry.
     * @return {@code true} if the entry exists and is not deleted, {@code false} otherwise.
     */
    boolean existsByIdAndDeletedFalse(int id);

    /**
     * Finds all AboutUs entries that are not marked as deleted.
     *
     * @return A list of {@link AboutUs} entities that are not deleted.
     * Can be empty if no entries exist.
     * This method is used to retrieve all active entries
     */
    List<AboutUs> findAllByDeletedFalse();

    /**
     * Finds an AboutUs entry by its internal integer ID, if it is not marked as deleted.
     *
     * @param id The internal integer ID of the AboutUs entry.
     * @return An {@link Optional} containing the {@link AboutUs} entity if found and not deleted,
     * otherwise an empty Optional.
     */
    Optional<AboutUs> findByIdAndDeletedFalse(int id);

    /**
     * Finds an AboutUs entry by its UUID, if it is not marked as deleted.
     *
     * @param uuid The UUID of the AboutUs entry.
     * @return An {@link Optional} containing the {@link AboutUs} entity if found and not deleted,
     * otherwise an empty Optional.
     */
    Optional<AboutUs> findByUuidAndDeletedFalse(UUID uuid);


}

