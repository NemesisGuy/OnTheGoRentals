package za.ac.cput.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CarImage.java
 * Represents a single image file associated with a Car entity.
 * This entity allows a car to have a one-to-many relationship with its images,
 * enabling features like an image carousel.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: 2024-06-07
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarImage {

    /**
     * The primary key for the CarImage record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A unique, unguessable identifier for API interactions.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * The name of the file as it is stored on the server's file system.
     * e.g., "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6.jpg"
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * The type (or folder) where the image is stored.
     * e.g., "cars". This helps in constructing the full URL for retrieval.
     */
    @Column(nullable = false)
    private String imageType;

    /**
     * The timestamp when this image was uploaded.
     */
    private LocalDateTime uploadedAt;

    /**
     * The relationship back to the Car this image belongs to.
     * This is the "many" side of a one-to-many relationship (many images can belong to one car).
     * It creates a 'car_id' foreign key column in the 'car_image' table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    @JsonIgnore // IMPORTANT: Prevents infinite loops when serializing JSON.
    private Car car;

    /**
     * Automatically sets the UUID and upload timestamp before the entity is first saved.
     */
    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) this.uuid = UUID.randomUUID();
        this.uploadedAt = LocalDateTime.now();
    }
}