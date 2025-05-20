package za.ac.cput.domain;


import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @OneToMany(mappedBy = "car") //one car to many rentals
    private final List<Rental> rentals = new ArrayList<>();
    private String make;
    private String model;
    private int year;
    private String category;
    @Enumerated(EnumType.STRING)
    private PriceGroup priceGroup;
    private String licensePlate;
    private boolean available;
    private boolean deleted = false;

    public Car() {
        // Default constructor
    }

    private Car(Builder builder) {
        this.id = builder.id;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setPriceGroup(PriceGroup priceGroupEnum) {
        this.priceGroup = priceGroupEnum;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car car)) return false;
        return getId() == car.getId() && getYear() == car.getYear() && Objects.equals(getMake(), car.getMake()) && Objects.equals(getModel(), car.getModel()) && Objects.equals(getCategory(), car.getCategory()) && Objects.equals(getLicensePlate(), car.getLicensePlate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMake(), getModel(), getYear(), getCategory(), getLicensePlate());
    }


    public String getPriceGroupString() {
        return priceGroup != null ? priceGroup.toString() : "NONE";
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
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
        public Builder isDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder copy(Car car) {
            this.id = car.id;
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