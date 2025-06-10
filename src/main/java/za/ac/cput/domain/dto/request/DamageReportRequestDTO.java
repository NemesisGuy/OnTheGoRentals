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
public class DamageReportRequestDTO {
    @NotNull(message = "Rental UUID cannot be null")
    private UUID rentalUuid; // Client sends the UUID of the related Rental

    @NotBlank(message = "Description cannot be blank") // Use NotBlank for Strings
    private String description;

    @NotNull(message = "Date and time cannot be null")
    @PastOrPresent(message = "Date and time must be in the past or present")
    private LocalDateTime dateAndTime;

    private String location; // Optional? If so, no NotBlank

    @NotNull(message = "Repair cost cannot be null")
    // @Min(0) // Add if repairCost cannot be negative
    private double repairCost;
}