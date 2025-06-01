package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.enums.RentalStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * RentalRepository.java
 * Spring Data JPA repository for {@link Rental} entities.
 * Provides standard CRUD operations and custom query methods for rentals.
 *
 * Author: Peter Buckingham (220165289)
 * Date: [15-05-2023]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    /**
     * Finds a non-deleted rental by its internal integer ID.
     * @param id The ID of the rental.
     * @return An {@link Optional} containing the rental if found and not deleted, otherwise empty.
     */
    Optional<Rental> findByIdAndDeletedFalse(Integer id);

    /**
     * Finds a non-deleted rental by its UUID.
     * @param uuid The UUID of the rental.
     * @return An {@link Optional} containing the rental if found and not deleted, otherwise empty.
     */
    Optional<Rental> findByUuidAndDeletedFalse(UUID uuid);

    /**
     * Finds all non-deleted rentals.
     * @return A list of non-deleted rentals.
     */
    List<Rental> findAllByDeletedFalse();

    /**
     * Checks if a non-deleted rental exists by its internal integer ID.
     * @param id The ID to check.
     * @return True if a non-deleted rental exists with the ID, false otherwise.
     */
    boolean existsByIdAndDeletedFalse(Integer id);

    /**
     * Finds all non-deleted rentals for a given user ID where the car has not yet been returned (returnedDate is null).
     * This is typically used to find currently active rentals for a user.
     *
     * @param userId The internal integer ID of the user.
     * @return A list of matching active {@link Rental} entities for the user.
     */
    List<Rental> findByUserIdAndReturnedDateIsNullAndDeletedFalse(Integer userId);

    /**
     * Finds all non-deleted rentals for a given user ID, regardless of their return status.
     * This is used for fetching a user's complete rental history.
     *
     * @param userId The internal integer ID of the user.
     * @return A list of all non-deleted {@link Rental} entities for the user.
     */
    List<Rental> findByUserIdAndDeletedFalse(Integer userId);


    // --- Methods for Due/Overdue Rentals ---

    /**
     * Finds rentals expected to be returned within a given date range (e.g., for a specific day),
     * that are currently in the specified status (typically ACTIVE), have not yet been returned (returnedDate is null),
     * and are not soft-deleted.
     *
     * @param startDateRange The start timestamp of the expected return date window.
     * @param endDateRange   The end timestamp of the expected return date window.
     * @param status         The required current status of the rental (e.g., {@link RentalStatus#ACTIVE}).
     * @return A list of matching rentals.
     */
    List<Rental> findByExpectedReturnDateBetweenAndStatusAndReturnedDateIsNullAndDeletedFalse(
            LocalDateTime startDateRange, LocalDateTime endDateRange, RentalStatus status
    );

    /**
     * Finds rentals whose expected return date is before a given date/time,
     * that are currently in the specified status (typically ACTIVE), have not yet been returned (returnedDate is null),
     * and are not soft-deleted. This is used for finding overdue rentals.
     *
     * @param dateTimeCutoff The date/time to compare the expected return date against (e.g., {@code LocalDateTime.now()}).
     *                       Rentals with expectedReturnDate before this are considered.
     * @param status         The required current status of the rental (e.g., {@link RentalStatus#ACTIVE}).
     * @return A list of matching overdue rentals.
     */
    List<Rental> findByExpectedReturnDateBeforeAndStatusAndReturnedDateIsNullAndDeletedFalse(
            LocalDateTime dateTimeCutoff, RentalStatus status
    );

    /**
     * Finds all non-deleted rentals for a given user ID, that are in a specific status,
     * and where the car has not yet been returned (returnedDate is null).
     *
     * @param userId The internal integer ID of the user.
     * @param status The required current status of the rental.
     * @return A list of matching {@link Rental} entities.
     */
    List<Rental> findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(Integer userId, RentalStatus status);

}