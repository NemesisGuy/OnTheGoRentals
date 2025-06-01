package za.ac.cput.domain.enums;

/**
 * BookingStatus.java
 * Represents the lifecycle status of a car booking request made by a user.
 *
 * Author: Peter Buckingham (220165289)
 * Date: 2025-05-30
 */
public enum BookingStatus {
    /**
     * The booking has been successfully created by the user and the car is reserved
     * for the specified period. This is the default active state for a new booking.
     * The system might perform automated checks (e.g., car availability at the exact moment of booking).
     */
    CONFIRMED,

    /**
     * The booking was cancelled by the user before the scheduled pickup time.
     * The car reservation is released.
     */
    USER_CANCELLED,

    /**
     * The booking was cancelled by an administrator or system process (e.g., due to car unavailability
     * discovered after booking, or other administrative reasons).
     * The car reservation is released.
     */
    ADMIN_CANCELLED,

    /**
     * The booking was confirmed, but the customer did not arrive to pick up the car
     * at the scheduled time or within a grace period.
     * The car reservation is released after this status is set.
     */
    NO_SHOW,

    /**
     * The booking has been successfully converted into an active rental;
     * the customer has picked up the car. This is a terminal state for a booking
     * that proceeds to the rental phase.
     */
    RENTAL_INITIATED, // Or FULFILLED, COMPLETED_BY_RENTAL

    /**
     * A general completed status if RENTAL_INITIATED isn't granular enough,
     * or if a booking could be "completed" without turning into a rental in some edge cases
     * (though RENTAL_INITIATED is usually better for fulfilled bookings).
     * For now, RENTAL_INITIATED seems sufficient as the "good" terminal state.
     */
    // COMPLETED
}