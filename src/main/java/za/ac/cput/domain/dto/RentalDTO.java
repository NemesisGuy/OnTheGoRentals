package za.ac.cput.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.enums.RentalStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentalDTO {
    private Integer id;
    private UserDTO user;      // Already excludes password
    private Car car;     // or a CarDTO if you want car details
    private Integer issuer;
    private Integer receiver;
    private double fine;
    private LocalDateTime issuedDate;
    private LocalDateTime returnedDate;
    private RentalStatus status;
}
