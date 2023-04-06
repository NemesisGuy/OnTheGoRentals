package za.ac.cput.domain;

import za.ac.cput.scratch.Customer;
import za.ac.cput.scratch.Rental;

import java.time.LocalDate;
/**
 * DamageReport.java
 * Class for the Damage report
 * Author: Cwenga Dlova (214310671)
 * Date:  01 April 2023
 */
public class DamageReport implements IDamageReport{
    private String id;
    private Rental rentalId;
    private Customer customerId;
    private Car carId;
    private LocalDate reportDate;
    private String damageLocation;
    private String description;

    public DamageReport(Builder builder) {
        this.id = builder.id;
        this.rentalId = builder.rentalId;
        this.customerId = builder.customerId;
        this.carId = builder.carId;
        this.reportDate = builder.reportDate;
        this.damageLocation = builder.damageLocation;
        this.description = builder.description;
    }

    public String getId() {
        return id;
    }

    public Rental getRentalId() {
        return rentalId;
    }

    public Customer getCustomerId() {
        return customerId;
    }

    public Car getCarId() {
        return carId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public String getDamageLocation() {
        return damageLocation;
    }

    public String getDescription() {
        return description;

    }

    @Override
    public String toString() {
        return "DamageReport{" +
                "id='" + id + '\'' +
                ", rentalId=" + rentalId +
                ", customerId=" + customerId +
                ", carId=" + carId +
                ", reportDate=" + reportDate +
                ", damageLocation='" + damageLocation + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    public static class Builder {
        private String id;
        private Rental rentalId;
        private Customer customerId;
        private Car carId;
        private LocalDate reportDate;
        private String damageLocation;
        private String description;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setRentalId(Rental rentalId) {
            this.rentalId = rentalId;
            return this;
        }

        public Builder setCustomerId(Customer customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder setCarId(Car carId) {
            this.carId = carId;
            return this;
        }

        public Builder setReportDate(LocalDate reportDate) {
            this.reportDate = reportDate;
            return this;
        }

        public Builder setDamageLocation(String damageLocation) {
            this.damageLocation = damageLocation;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        public Builder copy(DamageReport report ){
            this.id = report.id;
            this.rentalId = report.rentalId;
            this.customerId = report.customerId;
            this.carId = report.carId;
            this.reportDate = report.reportDate;
            this.damageLocation = report.damageLocation;
            this.description = report.description;
            return this;

        }
        public DamageReport build(){
            return  new DamageReport(this);
        }
    }
}
