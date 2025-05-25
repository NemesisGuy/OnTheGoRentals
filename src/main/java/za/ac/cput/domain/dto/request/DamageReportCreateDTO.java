package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DamageReportCreateDTO {
    @NotNull(message = "Rental UUID cannot be null")
    private UUID rentalUuid; // To link to an existing Rental

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Date and time of damage cannot be null")
    @PastOrPresent(message = "Date and time must be in the past or present")
    private LocalDateTime dateAndTime; // When the damage was observed/occurred

    private String location;

    // @Min(0) // If cost cannot be negative
    private double repairCost; // Can be 0 if not yet assessed
}