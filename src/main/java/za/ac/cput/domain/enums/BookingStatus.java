package za.ac.cput.domain.enums;

// BookingStatus.java


public enum BookingStatus {
    PENDING,    // before confirmation/payment
    CONFIRMED,  // reservation is locked in
    CANCELED, CANCELLED   // user or system cancelled the booking
}

