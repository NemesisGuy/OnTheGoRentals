package za.ac.cput.domain.enums;

// RentalStatus.java


/**
 * Represents the lifecycle status of an active car rental.
 */
public enum RentalStatus {
    /**
     * The rental is currently in progress; the customer has possession of the car.
     */
    ACTIVE,

    /**
     * The rental period has ended, the car has been returned, and all post-rental
     * processes (inspection, final payments, etc.) are finalized. This is a terminal state.
     */
    COMPLETED,


    /**
     * The rental was cancelled before it started or during the rental period.
     * This is a terminal state.
     */
    CANCELLED
}