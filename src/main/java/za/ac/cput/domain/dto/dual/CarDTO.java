package za.ac.cput.domain.dto.dual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDTO {
    private UUID uuid; // This will be the identifier for the frontend
    private String make;
    private String model;
    private int year;
    private String category;
    private PriceGroup priceGroup;
    private String licensePlate;
    private boolean available;
    // 'rentals' list is usually not included in a CarDTO to avoid circular dependencies or overly large payloads.
    // If needed, specific rental info would be fetched via separate endpoints.
    // 'deleted' flag is typically not exposed to the frontend unless for specific admin views.
}