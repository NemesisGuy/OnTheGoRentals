package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero; // Assuming fine should be non-negative
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RentalRequestDTO.java
 * Data Transfer Object for requesting the creation or representation of a rental.
 * This DTO is used to convey rental details, typically when an administrator
 * is creating or managing a rental record, as it allows specifying comprehensive details
 * including user, car, driver, issuer/receiver staff, fines, dates, and status.
 *
 * Author: Peter Buckingham (220165289) // Assuming based on consistent authorship, please confirm/correct
 * Date: [Original Date of DTO creation - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalRequestDTO {

    /**
     * The UUID of the User associated with this rental.
     * This field is mandatory.
     */
    @NotNull(message = "User UUID cannot be null")
    private UUID userUuid;

    /**
     * The UUID of the Car associated with this rental.
     * This field is mandatory.
     */
    @NotNull(message = "Car UUID cannot be null")
    private UUID carUuid;

    /**
     * The UUID of the Driver assigned to this rental.
     * This field is optional.
     */
    private UUID driverUuid; // Optional

    /**
     * The identifier (e.g., staff ID) of the person or system component that issued the rental.
     * This field is optional.
     */
    private Integer issuer; // Consider renaming to issuerId if it's an ID

    /**
     * The identifier (e.g., staff ID) of the person or system component that received the car upon return.
     * This field is optional, especially at creation.
     */
    private Integer receiver; // Consider renaming to receiverId if it's an ID

    /**
     * The amount of any fine associated with the rental.
     * Assumed to be non-negative.
     */
    @PositiveOrZero(message = "Fine amount must be zero or positive") // Added validation
    private double fine;

    /**
     * The date and time when the rental was issued or started.
     * This field is mandatory and must be in the present or future.
     */
    @NotNull(message = "Issue date (booking start date) cannot be null")
    @FutureOrPresent(message = "Issue date must be in the present or future")
    private LocalDateTime issuedDate;

    /**
     * The expected date and time when the rental car should be returned.
     * This is typically used to set a due date for the rental.
     * This field is mandatory and must be in the present or future.
     */
    @NotNull(message = "Expected return date cannot be null")
    @FutureOrPresent(message = "Expected return date must be in the present or future")
    private LocalDateTime expectedReturnedDate; // WHEN IT'S DUE

    /**
        * The actual date and time when the rental car was returned.
        * This field is mandatory and must be in the present or future.
        * It is typically set when the rental is completed.
        */

    @FutureOrPresent(message = "Return date must be in the present or future")
    private LocalDateTime returnedDate;



    /**
     * The current status of the rental (e.g., "ACTIVE", "PENDING", "COMPLETED", "CANCELED").
     * This field is optional; the system might assign a default status if not provided at creation.
     */

    private String status;
}