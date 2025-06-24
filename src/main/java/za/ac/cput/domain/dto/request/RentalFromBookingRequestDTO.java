package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RentalFromBookingRequestDTO.java
 * Data Transfer Object used by staff/admin when creating a Rental from an existing Booking.
 * Contains details that might be confirmed or provided at the time of vehicle pickup.
 * <p>
 * Author: Peter Buckingham
 * Date: 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalFromBookingRequestDTO {

    /**
     * The ID of the staff member issuing the rental.
     * This should be the ID of the currently logged-in staff member.
     */
    @NotNull(message = "Issuer ID (staff ID) cannot be null")
    private UUID issuerId;

    /**
     * The UUID of the driver assigned at the time of pickup, if any.
     * This can be different from a driver pre-assigned on the booking or can be newly assigned.
     * Optional.
     */
    private UUID driverUuid;

    /**
     * The actual date and time the vehicle is picked up by the customer.
     * If not provided, the service might default to the current server time.
     * Optional.
     */
    private LocalDateTime actualPickupTime;

    // Other potential fields staff might input/confirm at pickup:
    // private String vehicleConditionNotes;
    // private Integer mileageOut;

    private LocalDateTime expectedReturnDate; // Optional, can be set by staff at pickup
}