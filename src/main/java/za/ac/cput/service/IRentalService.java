package za.ac.cput.service;

import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * IRentalService.java
 * Interface defining the contract for rental related services.
 * Extends the generic {@link IService} for basic CRUD and adds rental-specific
 * operations like confirming, canceling, completing rentals, and querying rental history,
 * as well as finding rentals due or overdue.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
public interface IRentalService extends IService<Rental, Integer> {

    // create(Rental), read(Integer), update(Rental), delete(Integer) are inherited from IService

    @Transactional
        // CRITICAL: This whole operation must be one transaction
    Rental createRentalFromBooking(UUID bookingUuid, UUID issuerId, UUID driverUuid, LocalDateTime actualIssuedDate);

    /**
     * Retrieves a rental by its UUID, if not soft-deleted.
     *
     * @param uuid The UUID of the rental.
     * @return The {@link Rental} entity, or {@code null} if not found or soft-deleted.
     */
    Rental read(UUID uuid);

    /**
     * Updates an existing rental identified by its integer ID, applying changes from the provided rental entity.
     * This is an overloaded method. It's generally recommended for the controller to fetch the existing entity,
     * build the updated state using the Builder pattern, and call {@link IService#update(Object)}.
     *
     * @param id     The internal integer ID of the rental to update.
     * @param rental The {@link Rental} entity containing the new data. Its ID should match {@code id}.
     * @return The updated and persisted {@link Rental} entity, or {@code null} if not found.
     * @deprecated Prefer {@link IService#update(Object)} where the new state is passed directly.
     */
    @Deprecated
    @Transactional
    // org.springframework.transaction.annotation.Transactional
    Rental update(int id, Rental rental);

    /**
     * Retrieves all non-deleted rentals.
     *
     * @return A list of all non-deleted {@link Rental} entities. Can be empty.
     */
    List<Rental> getAll();

    /**
     * Checks if a user is currently renting any car (i.e., has an active rental with no return date).
     *
     * @param user The {@link User} to check.
     * @return {@code true} if the user has an active rental, {@code false} otherwise.
     */
    boolean isCurrentlyRenting(User user);

    /**
     * Retrieves the current active rental for a given user, if any.
     * An active rental is one not deleted, with status ACTIVE, and no actual return date.
     *
     * @param user The {@link User} whose active rental is to be found.
     * @return The active {@link Rental} if one exists, otherwise {@code null}.
     */
    Rental getCurrentRental(User user);

    /**
     * Checks if a rental exists with the given internal integer ID and is not soft-deleted.
     *
     * @param id The internal integer ID to check.
     * @return {@code true} if such a rental exists, {@code false} otherwise.
     */
    boolean existsById(Integer id);

    /**
     * Retrieves the rental history for a specific user (all non-deleted rentals).
     *
     * @param user The {@link User} entity for whom to fetch rental history.
     * @return A list of {@link Rental} entities associated with the user. Can be empty.
     */
    List<Rental> getRentalHistoryByUser(User user);

    /**
     * Confirms a rental identified by its UUID.
     * Typically changes status from a pending/booked state to an active/confirmed state.
     *
     * @param rentalUuid The UUID of the rental to confirm.
     * @return The updated (confirmed) {@link Rental} entity.
     */
    Rental confirmRentalByUuid(UUID rentalUuid);

    /**
     * Cancels a rental identified by its UUID.
     * Typically changes status to CANCELLED and makes the associated car available.
     *
     * @param rentalUuid The UUID of the rental to cancel.
     * @return The updated (cancelled) {@link Rental} entity.
     */
    Rental cancelRentalByUuid(UUID rentalUuid);

    /**
     * Completes a rental identified by its UUID.
     * Sets status to COMPLETED, records actual return date, applies fines, and makes car available.
     *
     * @param rentalUuid The UUID of the rental to complete.
     * @param fineAmount The amount of any fine to be applied.
     * @return The updated (completed) {@link Rental} entity.
     */
    Rental completeRentalByUuid(UUID rentalUuid, double fineAmount);

    /**
     * Finds all active rentals for a given user ID where the car has not yet been returned
     * and the rental is not soft-deleted.
     *
     * @param userId The internal integer ID of the user.
     * @return A list of matching active {@link Rental} entities.
     */
    List<Rental> findByUserIdAndReturnedDateIsNullAndDeletedFalse(Integer userId);

    /**
     * Retrieves a list of all active rentals that are expected to be returned today
     * and have not yet been returned.
     *
     * @return A list of {@link Rental} entities due today.
     */
    List<Rental> findRentalsDueToday();

    /**
     * Retrieves a list of all active rentals that are past their expected return date
     * and have not yet been returned.
     *
     * @return A list of overdue {@link Rental} entities.
     */
    List<Rental> findOverdueRentals();

    /**
     * Retrieves a list of all active rentals that are expected to be returned on a specific date
     * and have not yet been returned.
     *
     * @param specificDate The date to check for expected returns.
     * @return A list of {@link Rental} entities due on the specific date.
     */
    List<Rental> findRentalsDueOnDate(LocalDate specificDate);

    List<Rental> findActiveRentals();
}