// RentalResponseDTO.java
package za.ac.cput.domain.dto.response;

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
public class RentalResponseDTO {
    private UUID uuid;
    private UserResponseDTO user;
    private CarResponseDTO car;
    private DriverResponseDTO driver; // Assuming you have DriverResponseDTO
    private UUID issuer;
    private UUID receiver;
    private double fine;
    private LocalDateTime issuedDate;
    private LocalDateTime expectedReturnDate; // Optional, can be null if not set
    private LocalDateTime returnedDate; // Will be null until car is returned
    private String status;
}
