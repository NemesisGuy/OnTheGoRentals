package za.ac.cput.domain.entity;

/**
 * DamageReport.java
 * Domain Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 */

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
@Getter
@Entity
public class DamageReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
   /* @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "rental", referencedColumnName = "id", unique = true)
    private Rental rental;*/
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "rental", nullable = false)
   private Rental rental;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(updatable = false)
    private LocalDateTime dateAndTime;
    private String location;
    private double repairCost;
    @Column(updatable = false)
    private LocalDateTime createdAt; // When the report was created in the system
    @Column(nullable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() { // Renamed to avoid clash with entity field if any
        if (this.uuid == null) this.uuid = UUID.randomUUID();
        this.createdAt = LocalDateTime.now(); // Set report creation time
        this.deleted = false;
        if (this.dateAndTime == null) this.dateAndTime = this.createdAt; // Default damage time to report time if not specified
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
        this.createdAt = builder.createdAt;
        this.deleted = builder.deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DamageReport that = (DamageReport) o;
        return id == that.id && Double.compare(repairCost, that.repairCost) == 0 && deleted == that.deleted && Objects.equals(uuid, that.uuid) && Objects.equals(rental, that.rental) && Objects.equals(description, that.description) && Objects.equals(dateAndTime, that.dateAndTime) && Objects.equals(location, that.location) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, rental, description, dateAndTime, location, repairCost, createdAt, deleted);
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
                ", createdAt=" + createdAt +
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
        private LocalDateTime createdAt;
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
        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
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
            this.createdAt = report.createdAt;
            this.deleted = report.deleted;
            return this;
        }

        public DamageReport build() {
            return new DamageReport(this);
        }
    }
}


