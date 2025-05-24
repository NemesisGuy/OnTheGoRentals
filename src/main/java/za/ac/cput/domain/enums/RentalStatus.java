package za.ac.cput.domain.enums;

// RentalStatus.java


public enum RentalStatus {
    ACTIVE,    // car is currently out on hire
    COMPLETED,   // car has been returned and rental closed
    PENDING_CONFIRMATION, BOOKED, CONFIRMED, IN_PROGRESS, CANCELLED, PENDING
}
