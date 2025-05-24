package za.ac.cput.domain.dto.dual;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DamageReportDTO {
    private int id;
    private RentalDTO rental; // safe wrapped DTO
    private String description;
    private LocalDateTime dateAndTime;
    private String location;
    private double repairCost;
}
