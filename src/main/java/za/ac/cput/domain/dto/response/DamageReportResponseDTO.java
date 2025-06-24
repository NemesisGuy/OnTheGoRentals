// DamageReportResponseDTO.java
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
public class DamageReportResponseDTO {
    private UUID uuid;
    private RentalResponseDTO rental;
    private String description;
    private LocalDateTime dateAndTime;
    private String location;
    private double repairCost;
}