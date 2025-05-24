// RentalResponseDTO.java
package za.ac.cput.domain.dto.response;

import lombok.*;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;

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
    private Integer issuer;
    private Integer receiver;
    private double fine;
    private LocalDateTime issuedDate;
    private LocalDateTime returnedDate; // Will be null until car is returned
    private String status;
}
