package za.ac.cput.domain.entity;


import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.UUID;

/**
 * Car.java
 * Entity for the Car
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */


@Entity

public class Car {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid ;
  /*  @JsonIgnore
    @OneToMany(mappedBy = "car") //one car to many rentals
    private final List<Rental> rentals = new ArrayList<>();*/
    private String make;
    private String model;
    private int year;
    private String category;
    @Enumerated(EnumType.STRING)
    private PriceGroup priceGroup;
    private String licensePlate;
    private boolean available;
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
            System.out.println("Car Entity @PrePersist: Generated UUID: " + this.uuid); // For debugging
        }
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
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

  /*  public List<Rental> getRentals() {
        return rentals;
    }
*/
    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getCategory() {
        return category;
    }

    public PriceGroup getPriceGroup() {
        return priceGroup;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getPriceGroupString() {
        return priceGroup != null ? priceGroup.toString() : "NONE";
    }



    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", uuid=" + uuid +
/*
                ", rentals=" + rentals +
*/
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", category='" + category + '\'' +
                ", priceGroup=" + priceGroup +
                ", licensePlate='" + licensePlate + '\'' +
                ", available=" + available +
                ", deleted=" + deleted +
                '}';
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
        private boolean available;
        private boolean deleted = false;

        public Builder id(int id) {
            this.id = id;
            return this;
        }
        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder make(String make) {
            this.make = make;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder priceGroup(PriceGroup priceGroup) {
            this.priceGroup = priceGroup;
            return this;
        }

        public Builder licensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
            return this;

        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }
        public Builder deleted(boolean deleted) {
            this.deleted = deleted;
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
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }
}