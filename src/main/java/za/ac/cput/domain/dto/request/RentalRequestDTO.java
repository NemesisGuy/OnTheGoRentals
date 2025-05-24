// RentalRequestDTO.java
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
public class RentalRequestDTO {
    @NotNull
    private UUID userUuid;

    @NotNull
    private UUID carUuid;
    private UUID driverUuid; // Optional
    private Integer issuer;
    private Integer receiver;
    private double fine;
    @NotNull(message = "Issue date (booking start date) cannot be null")
    @FutureOrPresent(message = "Issue date must be in the present or future")
    private LocalDateTime issuedDate;
    // For creating a rental, expectedReturnedDate is more appropriate than returnedDate
    @NotNull(message = "Expected return date cannot be null")
    @FutureOrPresent(message = "Expected return date must be in the present or future")
    private LocalDateTime returnedDate;
    private String status;
}