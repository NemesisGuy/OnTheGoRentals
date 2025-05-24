// CarResponseDTO.java
package za.ac.cput.domain.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.PriceGroup;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarResponseDTO {
    private UUID uuid;
    private String make;
    private String model;
    private int year;
    private String category;
    private PriceGroup priceGroup;
    private String licensePlate;
    private boolean available;
}