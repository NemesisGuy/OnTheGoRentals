package za.ac.cput.domain;

/**
 * DamageReport.java
 * Domain Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 */

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
public class DamageReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "rental", referencedColumnName = "id", unique = true)
    private Rental rental;
    private String description;
    private LocalDateTime dateAndTime;
    private String location;
    private double repairCost;
    private boolean deleted = false;
    @PrePersist
    protected  void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }

    public DamageReport() {
    }

    public DamageReport(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.rental = builder.rental;
        this.description = builder.description;
        this.dateAndTime = builder.dateAndTime;
        this.location = builder.location;
        this.repairCost = builder.repairCost;
        this.deleted = builder.deleted;
    }

    public int getId() {
        return id;
    }
    public UUID getUuid() {
        return uuid;
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
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DamageReport that = (DamageReport) o;
        return id == that.id && Double.compare(repairCost, that.repairCost) == 0 && deleted == that.deleted && Objects.equals(uuid, that.uuid) && Objects.equals(rental, that.rental) && Objects.equals(description, that.description) && Objects.equals(dateAndTime, that.dateAndTime) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, rental, description, dateAndTime, location, repairCost, deleted);
    }

    @Override
    public String toString() {
        return "DamageReport{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", rental=" + rental +
                ", description='" + description + '\'' +
                ", dateAndTime=" + dateAndTime +
                ", location='" + location + '\'' +
                ", repairCost=" + repairCost +
                ", deleted=" + deleted +
                '}';
    }

    public static class Builder {
        private int id;
        private UUID uuid;
        private Rental rental;
        private String description;
        private LocalDateTime dateAndTime;
        private String location;
        private double repairCost;
        private boolean deleted = false;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }
        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
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
        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder copy(DamageReport report) {
            this.id = report.id;
            this.uuid = report.uuid;
            this.rental = report.rental;
            this.description = report.description;
            this.dateAndTime = report.dateAndTime;
            this.location = report.location;
            this.repairCost = report.repairCost;
            this.deleted = report.deleted;
            return this;
        }

        public DamageReport build() {
            return new DamageReport(this);
        }
    }
}


