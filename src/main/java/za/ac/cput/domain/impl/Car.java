package za.ac.cput.domain.impl;


import za.ac.cput.domain.IVehicle;

import java.util.Objects;
/**
 * Car.java
 * Entity for the Car
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

public class Car implements IVehicle {

    private int id;

    private  String make;
    private  String model;
    private  int year;
    private  String category;
    private PriceGroup priceGroup;
    private  String licensePlate;
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

    public String getLicensePlate() {
        return licensePlate;
    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return getId() == car.getId() && getYear() == car.getYear() && Objects.equals(getMake(), car.getMake()) && Objects.equals(getModel(), car.getModel()) && Objects.equals(getCategory(), car.getCategory()) && Objects.equals(getLicensePlate(), car.getLicensePlate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMake(), getModel(), getYear(), getCategory(), getLicensePlate());
    }

    public void setPriceGroup(PriceGroup priceGroupEnum) {
                   this.priceGroup = priceGroupEnum;
    }

    public String getPriceGroupString() {
        return priceGroup.toString();
    }



    public static class Builder {
        private int id;
        private String make;
        private String model;
        private int year;
        private String category;

        private PriceGroup priceGroup;

        private String licensePlate;

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

        public Car build() {
            return new Car(this);
        }
    }
}