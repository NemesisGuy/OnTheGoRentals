package za.ac.cput.domain;

/**DamageReport.java
 * Domain Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 * */
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
@Entity
public class DamageReport {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "rental", referencedColumnName = "id", unique = true)
    private Rental rental;
    private String description;
    private LocalDateTime dateAndTime;
    private String location;
    private double repairCost;


    public DamageReport() {
    }

    public DamageReport(Builder builder) {
        this.id = builder.id;
        this.rental = builder.rental;
        this.description = builder.description;
        this.dateAndTime = builder.dateAndTime;
        this.location = builder.location;
        this.repairCost = builder.repairCost;
    }

    public int getId() {
        return id;
    }

    public Rental getRental() {
        return rental;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateAndTime() {
        return dateAndTime;
    }

    public String getLocation() {
        return location;
    }

    public double getRepairCost() {
        return repairCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DamageReport that = (DamageReport) o;
        return id == that.id && Double.compare(that.repairCost, repairCost) == 0 && Objects.equals(rental, that.rental) && Objects.equals(description, that.description) && Objects.equals(dateAndTime, that.dateAndTime) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rental, description, dateAndTime, location, repairCost);
    }

    @Override
    public String toString() {
        return "DamageReport{" +
                "id=" + id +
                ", rental=" + rental +
                ", description='" + description + '\'' +
                ", dateAndTime=" + dateAndTime +
                ", location='" + location + '\'' +
                ", repairCost=" + repairCost +
                '}';
    }

    public static class Builder {
        private int id;
        private Rental rental;
        private String description;
        private LocalDateTime dateAndTime;
        private String location;
        private double repairCost;

        public Builder setId(int id) {
            this.id = id;
            return  this;
        }

        public Builder setRental(Rental rental) {
            this.rental = rental;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDateAndTime(LocalDateTime dateAndTime) {
            this.dateAndTime = dateAndTime;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setRepairCost(double repairCost) {
            this.repairCost = repairCost;
            return this;
        }

        public Builder copy(DamageReport report) {
            this.id = report.id;
            this.rental = report.rental;
            this.description = report.description;
            this.dateAndTime = report.dateAndTime;
            this.location = report.location;
            this.repairCost = report.repairCost;
            return this;
        }

        public DamageReport build() {
            return new DamageReport(this);
        }
    }
}


