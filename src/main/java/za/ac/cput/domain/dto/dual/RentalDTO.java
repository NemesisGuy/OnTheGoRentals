package za.ac.cput.domain.dto.dual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.RentalStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentalDTO {
    private UUID uuid;
    private UserDTO user;      // Already excludes password
    private CarDTO  car;     // or a CarDTO if you want car details
    private Integer issuer;
    private Integer receiver;
    private double fine;
    private LocalDateTime issuedDate;
    private LocalDateTime returnedDate;
    private RentalStatus status;
}
