package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.PriceGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarUpdateDTO {
    @Size(max = 50, message = "Make cannot exceed 50 characters if provided")
    private String make;

    @Size(max = 50, message = "Model cannot exceed 50 characters if provided")
    private String model;

    @Min(value = 1900, message = "Year must be a valid year (e.g., 1900 or later) if provided")
    private Integer year;

    @Size(max = 50, message = "Category cannot exceed 50 characters if provided")
    private String category;

    private PriceGroup priceGroup;

    @Size(max = 15, message = "License plate cannot exceed 15 characters if provided")
    private String licensePlate;

    @Size(min = 11, max = 17, message = "VIN must be between 11 and 17 characters if provided")
    private String vin;

    private Boolean available; // nullable

    // REMOVED imageFileName and imageType. Image updates will be handled via a dedicated endpoint.
}