package za.ac.cput.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.PriceGroup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Car.java
 * Entity for the Car. This version is corrected to handle updates to collections
 * with orphanRemoval=true by clearing and re-populating the managed list,
 * rather than replacing it.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@Getter
@NoArgsConstructor
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    private String make;
    private String model;
    private int year;
    private String category;

    @Enumerated(EnumType.STRING)
    private PriceGroup priceGroup;

    private String licensePlate;

    @Size(min = 11, max = 17)
    private String vin;

    private boolean available = true;
    private boolean deleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "car",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<CarImage> images = new ArrayList<>();

    private Car(Builder builder) {
        builder.applyTo(this);
    }

    // --- PRIVATE SETTERS ---
    private void setId(int id) {
        this.id = id;
    }

    private void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    private void setMake(String make) {
        this.make = make;
    }

    private void setModel(String model) {
        this.model = model;
    }

    private void setYear(int year) {
        this.year = year;
    }

    private void setCategory(String category) {
        this.category = category;
    }

    private void setPriceGroup(PriceGroup priceGroup) {
        this.priceGroup = priceGroup;
    }

    private void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    private void setVin(String vin) {
        this.vin = vin;
    }

    private void setAvailable(boolean available) {
        this.available = available;
    }

    private void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    private void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    private void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    // The images list is modified directly, so no public setter is needed.


    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) this.uuid = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", imagesCount=" + (images != null ? images.size() : 0) +
                '}';
    }

    // --- BUILDER ---
    public static class Builder {
        private int id;
        private UUID uuid;
        private String make;
        private String model;
        // ... all other fields
        private int year;
        private String category;
        private PriceGroup priceGroup;
        private String licensePlate;
        private String vin;
        private boolean available;
        private boolean deleted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<CarImage> images = new ArrayList<>();

        // --- Builder Setters ---
        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setMake(String make) {
            this.make = make;
            return this;
        }

        public Builder setModel(String model) {
            this.model = model;
            return this;
        }

        public Builder setYear(int year) {
            this.year = year;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setPriceGroup(PriceGroup priceGroup) {
            this.priceGroup = priceGroup;
            return this;
        }

        public Builder setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
            return this;
        }

        public Builder setVin(String vin) {
            this.vin = vin;
            return this;
        }

        public Builder setAvailable(boolean available) {
            this.available = available;
            return this;
        }

        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setImages(List<CarImage> images) {
            this.images = images;
            return this;
        }


        public Builder copy(Car car) {
            if (car == null) return this;
            this.id = car.id;
            this.uuid = car.uuid;
            this.make = car.make;
            this.model = car.model;
            this.year = car.year;
            this.category = car.category;
            this.priceGroup = car.priceGroup;
            this.licensePlate = car.licensePlate;
            this.vin = car.vin;
            this.available = car.available;
            this.deleted = car.deleted;
            this.createdAt = car.createdAt;
            this.updatedAt = car.updatedAt;
            // The copy method can create a new list, that's fine.
            this.images = new ArrayList<>(car.getImages());
            return this;
        }

        public Car build() {
            return new Car(this);
        }

        /**
         * Applies the builder's state to an existing Car object.
         * For collections with orphanRemoval, it clears the managed collection
         * and adds all items from the builder's collection to it, preventing
         * the "collection was no longer referenced" error.
         *
         * @param car The target Car object to modify.
         * @return The modified Car object.
         */
        public Car applyTo(Car car) {
            // Apply simple fields directly
            car.setId(this.id);
            car.setUuid(this.uuid);
            car.setMake(this.make);
            car.setModel(this.model);
            car.setYear(this.year);
            car.setCategory(this.category);
            car.setPriceGroup(this.priceGroup);
            car.setLicensePlate(this.licensePlate);
            car.setVin(this.vin);
            car.setAvailable(this.available);
            car.setDeleted(this.deleted);
            car.setCreatedAt(this.createdAt);
            car.setUpdatedAt(this.updatedAt);

            // ** Modify the existing list, don't replace it. **
            if (car.getImages() != null) {
                car.getImages().clear(); // Clear the original, managed list
                if (this.images != null) {
                    // Add all items from the builder's list into the now-empty managed list
                    car.getImages().addAll(this.images);
                }
            } else {
                // If the managed list was null, we can assign the new one.
                car.images = this.images;
            }

            return car;
        }
    }
}