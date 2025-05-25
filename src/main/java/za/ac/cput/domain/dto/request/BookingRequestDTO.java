package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    @NotNull(message = "User UUID cannot be null")
    private UUID userUuid; // Client sends the UUID of the user making the booking

    @NotNull(message = "Car UUID cannot be null")
    private UUID carUuid;  // Client sends the UUID of the car to be booked

    @NotNull(message = "Booking start date cannot be null")
    @FutureOrPresent(message = "Booking start date must be in the present or future")
    private LocalDateTime bookingStartDate;

    @NotNull(message = "Booking end date cannot be null")
    @FutureOrPresent(message = "Booking end date must be in the present or future")
    private LocalDateTime bookingEndDate;
    // Optional fields can be included if the client can specify them
    private UUID driverUuid; // Optional: Client can specify a driver for the booking

    // Status for a new booking is typically set by the backend (e.g., "PENDING" or "CONFIRMED")
    // If client can suggest a status, include it. Otherwise, it's backend-determined.
    // For this example, let's assume status is set by backend during creation.
    // private String status;
}