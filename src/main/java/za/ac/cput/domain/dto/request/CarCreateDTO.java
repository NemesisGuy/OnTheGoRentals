package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.PriceGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarCreateDTO {

    @NotBlank(message = "Make cannot be blank")
    @Size(max = 50, message = "Make cannot exceed 50 characters")
    private String make;

    @NotBlank(message = "Model cannot be blank")
    @Size(max = 50, message = "Model cannot exceed 50 characters")
    private String model;

    @NotNull(message = "Year cannot be null")
    @Min(value = 1900, message = "Year must be a valid year (e.g., 1900 or later)")
    private Integer year;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    @NotNull(message = "Price group cannot be null")
    private PriceGroup priceGroup;

    @NotBlank(message = "License plate cannot be blank")
    @Size(max = 15, message = "License plate cannot exceed 15 characters")
    private String licensePlate;

    @Size(min = 11, max = 17, message = "VIN must be between 11 and 17 characters")
    private String vin;

    private Boolean available = true; // Default to true if not provided

    // REMOVED imageFileName and imageType - these are handled by the server on upload.
}