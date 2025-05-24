package za.ac.cput.domain.dto.request; // Or a sub-package like .update

import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.RentalStatus; // If status can be updated via DTO

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalUpdateDTO {
    // Fields a user might be allowed to update for THEIR OWN rental
    // (e.g., extend return date, if business rules allow)
    // Admin might update more fields.

    @FutureOrPresent(message = "New expected return date must be in the present or future")
    private LocalDateTime expectedReturnedDate;

    // Potentially, if user can change car before pickup (complex logic)
    // private UUID carUuid;

    // Status updates are usually specific actions (confirm, cancel, pickup, return)
    // rather than a direct field update by the user.
    // private RentalStatus status;
}