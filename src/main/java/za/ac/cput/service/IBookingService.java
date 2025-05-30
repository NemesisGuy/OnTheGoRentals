package za.ac.cput.service;

import za.ac.cput.domain.entity.Booking;

import java.util.List;
import java.util.UUID;

/**
 * IBookingService.java
 * Interface defining the contract for booking-related services.
 * This includes creating bookings, managing their lifecycle (confirm, cancel),
 * and retrieving booking information.
 *
 * Author: [Original Author - Please specify if known, or Peter Buckingham if you created/refactored it]
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
public interface IBookingService {

    /**
     * Creates a new booking.
     *
     * @param booking The {@link Booking} entity to create. This entity should have
     *                its user, car, and date details pre-populated.
     * @return The persisted {@link Booking} entity, typically with its generated ID and UUID.
     */
    Booking create(Booking booking);

    /**
     * Confirms an existing booking.
     * This typically changes the booking's status and may trigger other business logic
     * (e.g., marking the associated car as definitively unavailable for the period).
     *
     * @param bookingId The internal integer ID of the booking to confirm.
     * @return The updated {@link Booking} entity with its new (e.g., CONFIRMED) status.
     * @throws za.ac.cput.exception.ResourceNotFoundException if the booking with the given ID is not found.
     * @throws IllegalStateException if the booking cannot be confirmed from its current state.
     */
    Booking confirmBooking(int bookingId);

    /**
     * Cancels an existing booking.
     * This typically changes the booking's status and may make the associated car available again.
     *
     * @param bookingId The internal integer ID of the booking to cancel.
     * @return The updated {@link Booking} entity with its new (e.g., CANCELLED) status.
     * @throws za.ac.cput.exception.ResourceNotFoundException if the booking with the given ID is not found.
     * @throws IllegalStateException if the booking cannot be cancelled from its current state.
     */
    Booking cancelBooking(int bookingId);

    /**
     * Retrieves all bookings associated with a specific user.
     *
     * @param userId The internal integer ID of the user whose bookings are to be retrieved.
     * @return A list of {@link Booking} entities for the specified user. Can be empty.
     */
    List<Booking> getUserBookings(int userId);

    /**
     * Retrieves a booking by its internal integer ID.
     *
     * @param bookingId The internal integer ID of the booking.
     * @return The {@link Booking} entity, or {@code null} if not found or soft-deleted.
     */
    Booking read(int bookingId);

    /**
     * Retrieves a booking by its UUID.
     *
     * @param uuid The UUID of the booking.
     * @return The {@link Booking} entity, or {@code null} if not found or soft-deleted.
     */
    Booking read(UUID uuid);

    /**
     * Updates an existing booking.
     * The provided {@code booking} entity should contain the new state and its ID
     * should identify the booking to update.
     *
     * @param booking The {@link Booking} entity with updated information.
     * @return The updated and persisted {@link Booking} entity, or {@code null} if not found.
     */
    Booking update(Booking booking);

    /**
     * Soft-deletes a booking by its internal integer ID.
     *
     * @param bookingId The internal integer ID of the booking to delete.
     * @return {@code true} if the booking was found and soft-deleted, {@code false} otherwise.
     */
    boolean delete(int bookingId);

    /**
     * Retrieves all non-deleted bookings in the system.
     * This is typically an administrative operation.
     *
     * @return A list of all non-deleted {@link Booking} entities. Can be empty.
     */
    List<Booking> getAll();
}