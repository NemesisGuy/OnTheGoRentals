// CarRequestDTO.java
package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import za.ac.cput.domain.enums.PriceGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarRequestDTO {
    @NotBlank
    private String make;
    @NotBlank
    private String model;
    private int year;
    private String category;
    private PriceGroup priceGroup;
    private String licensePlate;
    private boolean available;
}
