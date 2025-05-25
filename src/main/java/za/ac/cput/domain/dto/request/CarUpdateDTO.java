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
    // All fields are optional for an update.
    // The client sends only the fields they want to change.
    // Validation applies if a field IS provided.

    @Size(max = 50, message = "Make cannot exceed 50 characters if provided")
    private String make;

    @Size(max = 50, message = "Model cannot exceed 50 characters if provided")
    private String model;

    @Min(value = 1900, message = "Year must be a valid year (e.g., 1900 or later) if provided")
    private Integer year; // Use Integer to allow it to be omitted (null)

    @Size(max = 50, message = "Category cannot exceed 50 characters if provided")
    private String category;

    private PriceGroup priceGroup;

    @Size(max = 15, message = "License plate cannot exceed 15 characters if provided")
    private String licensePlate;

    private Boolean available; // Allow admin to change availability
}