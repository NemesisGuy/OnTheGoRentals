// RentalRequestDTO.java
package za.ac.cput.domain.dto.request;

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

    private Integer issuer;
    private Integer receiver;
    private double fine;
    private LocalDateTime issuedDate;
    private LocalDateTime returnedDate;
    private String status;
}