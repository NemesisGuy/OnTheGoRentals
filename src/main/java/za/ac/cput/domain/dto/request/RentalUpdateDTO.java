package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
// import za.ac.cput.domain.enums.RentalStatus; // Uncomment if status is an enum and updatable

/**
 * RentalUpdateDTO.java
 * Data Transfer Object for updating an existing rental record.
 * This DTO is typically used by an administrator, allowing modification of various rental attributes.
 * All fields are optional; only the fields provided in the request will be considered for update.
 * The rental to be updated is identified by its UUID in the URL path, not within this DTO.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [ 2025-05-28]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Added for flexibility in constructing update DTOs, though not strictly necessary
public class RentalUpdateDTO {

    /**
     * The new UUID of the User to be associated with this rental.
     * Use with caution, as reassigning a rental to a different user is a significant change.
     */

    private UUID userUuid;

    /**
     * The new UUID of the Car to be associated with this rental.
     * If provided, the system should check the availability of the new car.
     */
    private UUID carUuid;

    /**
     * The new UUID of the Driver to be assigned to this rental.
     * Can be null to remove an existing driver.
     */
    private UUID driverUuid;

    /**
     * The updated identifier (e.g., staff ID) of the person or system component that issued the rental.
     */
    @NotNull(message = "Issuer ID (staff ID) cannot be null")
    private UUID issuer; // Consider renaming to issuerId

    /**
     * The updated identifier (e.g., staff ID) of the person or system component that received/will receive the car upon return.
     */
    private UUID receiver; // Consider renaming to receiverId

    /**
     * The updated fine amount associated with the rental.
     * Must be zero or positive if provided.
     */
    @PositiveOrZero(message = "Fine amount must be zero or positive")
    private Double fine;

    /**
     * The updated date and time when the rental was issued or started.
     * Must be in the present or future if provided.
     * Updating this for an active rental can have significant implications.
     */
/*
    @FutureOrPresent(message = "Issue date must be in the present or future")
*/
    private LocalDateTime issuedDate;
    /**
     * The updated date and time when the rental is expected to be returned.
     * This is typically used for setting or updating the due date.
     * Must be in the present or future if provided.
     */
    @FutureOrPresent(message = "Expected return date must be in the present or future if provided")
    private LocalDateTime expectedReturnDate; // When it's due back

    /**
     * The updated date and time when the rental car is expected to be returned.
     * Must be in the present or future if provided.
     */
    @FutureOrPresent(message = "Expected return date must be in the present or future")
    private LocalDateTime returnedDate; // Corresponds to expectedReturnedDate for consistency with RentalRequestDTO for updates

    /**
     * The new status of the rental (e.g., "ACTIVE", "COMPLETED", "CANCELED").
     * Note: Status changes are often better handled by specific action endpoints
     * (e.g., /confirm, /cancel, /complete) rather than direct field updates,
     * especially if they involve complex business logic or state transitions.
     */
    private String status;
    // If using an enum for status:
    // private RentalStatus status;
}