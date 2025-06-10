package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DamageReportUpdateDTO {
    // Fields an admin might update on an existing damage report
    private String description;

    @PastOrPresent(message = "Date and time must be in the past or present if provided")
    private LocalDateTime dateAndTime;

    private String location;
    private Double repairCost; // Use Double to allow null (no change)
    // The associated rental (rentalUuid) is usually not changed once a report is filed.
}