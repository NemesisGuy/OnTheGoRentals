package za.ac.cput.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.List;
import java.util.UUID;

/**
 * CarResponseDTO.java
 * This class represents the data transfer object for sending Car information to the client.
 * It includes all publicly accessible details of a car, as well as a list of fully-formed
 * URLs for any associated images.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: 2024-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CarResponseDTO {
    /**
     * The unique identifier for the car.
     */
    private UUID uuid;

    /**
     * The manufacturer of the car (e.g., "Toyota").
     */
    private String make;

    /**
     * The specific model of the car (e.g., "Corolla").
     */
    private String model;

    /**
     * The manufacturing year of the car.
     */
    private int year;

    /**
     * The category of the car (e.g., "Sedan", "SUV").
     */
    private String category;

    /**
     * The pricing group the car belongs to (e.g., ECONOMY, LUXURY).
     */
    private PriceGroup priceGroup;

    /**
     * The license plate number of the car.
     */
    private String licensePlate;

    /**
     * The availability status of the car for rental.
     */
    private boolean available;

    /**
     * The server-generated filename for the car's image (e.g., "uuid.jpg").
     * This is primarily for internal use or debugging.
     */
    private String imageFileName;

    /**
     * The type or folder where the image is stored (e.g., "cars").
     * This is primarily for internal use or debugging.
     */
    private String imageType;

    /**
     * The Vehicle Identification Number of the car.
     */
    private String vin;

    /**
     * A list of fully-formed, publicly accessible URLs for the car's images.
     * The frontend should use this list to display images.
     */
    private List<String> imageUrls;
}