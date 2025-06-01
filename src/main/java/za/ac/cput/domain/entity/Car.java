package za.ac.cput.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.Id;
import za.ac.cput.domain.enums.PriceGroup;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Car.java
 * Entity for the Car
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

@Getter
@Entity

public class Car {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid ;

    private String make;
    private String model;
    private int year;
    private String category;
    @Enumerated(EnumType.STRING)
    private PriceGroup priceGroup;
    private String licensePlate;

    @Column(name = "available", nullable = false)
    @ColumnDefault("true")
    // Explicitly mark as NOT NULL in JPA if DB requires it
    private boolean available = true; // <<< SET A DEFAULT VALUE HERE

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean deleted ;

   /* @Column(updatable = false)*/
    private LocalDateTime createdAt;
    /*@Column(updatable = false)*/
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
            this.available = true; // Default to available if not set
            this.deleted = false; // Default to not deleted

    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Car() {
        // Default constructor
    }

    private Car(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.make = builder.make;
        this.model = builder.model;
        this.year = builder.year;
        this.category = builder.category;
        this.priceGroup = builder.priceGroup;
        this.licensePlate = builder.licensePlate;
        this.available = builder.available;
        this.deleted= builder.deleted;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", category='" + category + '\'' +
                ", priceGroup=" + priceGroup +
                ", licensePlate='" + licensePlate + '\'' +
                ", available=" + available +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private UUID uuid;
        private String make;
        private String model;
        private int year;
        private String category;
        private PriceGroup priceGroup;
        private String licensePlate;
        private boolean available; // Default to available
        private boolean deleted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

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

        public Builder copy(Car car) {
            this.id = car.id;
            this.uuid = car.uuid;
            this.make = car.make;
            this.model = car.model;
            this.year = car.year;
            this.category = car.category;
            this.priceGroup = car.priceGroup;
            this.licensePlate = car.licensePlate;
            this.available = car.available;
            this.deleted = car.deleted;
            this.createdAt = car.createdAt;
            this.updatedAt = car.updatedAt;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }
}