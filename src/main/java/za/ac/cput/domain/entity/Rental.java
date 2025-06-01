package za.ac.cput.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
// import org.springframework.data.annotation.Id; // Not needed if using jakarta.persistence.Id
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.domain.entity.security.User;

import java.io.Serializable;
import java.time.LocalDate; // For date-only comparisons
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Rental.java
 * Represents a rental agreement for a car by a user.
 * Tracks the state of the rental from active usage to completion or cancellation.
 *
 * Author: Peter Buckingham (220165289)
 * Date: April 2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-29
 */
@Getter
@Entity
public class Rental implements Serializable {
    @jakarta.persistence.Id // Explicitly using jakarta.persistence.Id
    // @Id // Redundant if @jakarta.persistence.Id is used
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver; // Optional

    private UUID issuer; // Staff ID of issuer
    private UUID receiver; // Staff ID of receiver
    private int fine; // Fine amount

    @Column(nullable = false)
    private LocalDateTime issuedDate; // When the rental started

    @Column(nullable = false) // Expected return date should usually be mandatory for an active rental
    private LocalDateTime expectedReturnDate;

    private LocalDateTime returnedDate; // Actual date car was returned

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30) // Ensure DB column can hold longest enum string
    private RentalStatus status;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) this.uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.deleted = false;
        if (this.status == null) this.status = RentalStatus.ACTIVE; // Default for a new Rental
        if (this.issuedDate == null) this.issuedDate = now; // Default issue date to now if not set
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Rental() {
    }

    // Full constructor (usually used by builder or testing)
    public Rental(int id, UUID uuid, User user, Car car, Driver driver, UUID issuer, UUID receiver, int fine, LocalDateTime issuedDate, LocalDateTime expectedReturnDate, LocalDateTime returnedDate, RentalStatus status, boolean deleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.uuid = uuid;
        this.user = user;
        this.car = car;
        this.driver = driver;
        this.issuer = issuer;
        this.receiver = receiver;
        this.fine = fine;
        this.issuedDate = issuedDate;
        this.expectedReturnDate = expectedReturnDate;
        this.returnedDate = returnedDate;
        this.status = status;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // Private constructor for the Builder
    private Rental(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.user = builder.user;
        this.car = builder.car;
        this.driver = builder.driver;
        this.issuer = builder.issuer;
        this.receiver = builder.receiver;
        this.fine = builder.fine;
        this.issuedDate = builder.issuedDate;
        this.expectedReturnDate = builder.expectedReturnDate;
        this.returnedDate = builder.returnedDate;
        this.status = builder.status;
        this.deleted = builder.deleted;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // --- Transient methods for derived states ---

    /**
     * Checks if the rental is currently considered overdue.
     * A rental is overdue if its status is ACTIVE, it has an expected return date,
     * and the current date/time is past that expected return date.
     *
     * @return {@code true} if the rental is overdue, {@code false} otherwise.
     */
    @Transient // JPA will ignore this method for persistence
    public boolean isOverdue() {
        return this.status == RentalStatus.ACTIVE && // Only active rentals can be overdue
                this.expectedReturnDate != null &&
                LocalDateTime.now().isAfter(this.expectedReturnDate);
    }

    /**
     * Checks if the rental is expected to be returned today.
     * A rental is due today if its status is ACTIVE, it has an expected return date,
     * and the date part of the expected return date is the same as today's date.
     *
     * @return {@code true} if the rental is due today, {@code false} otherwise.
     */
    @Transient // JPA will ignore this method for persistence
    public boolean isDueToday() {
        return this.status == RentalStatus.ACTIVE && // Only active rentals can be due
                this.expectedReturnDate != null &&
                this.expectedReturnDate.toLocalDate().isEqual(LocalDate.now());
    }

    /**
     * Checks if the rental is currently active.
     * A rental is considered active if its status is ACTIVE and it has not been deleted.
     *
     * @return {@code true} if the rental is active, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rental rental = (Rental) o;
        // For JPA entities, equality is often best based on ID if persisted, or UUID if unique.
        // Relying on all fields can be tricky, especially with LAZY loaded associations.
        if (id != 0 && rental.id != 0) { // If both have been persisted
            return id == rental.id;
        }
        return Objects.equals(uuid, rental.uuid); // Fallback to UUID if not persisted or IDs are 0
    }

    @Override
    public int hashCode() {
        // Consistent with equals: use ID if persisted, otherwise UUID.
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", uuid=" + uuid +
                // Avoid loading lazy associations in toString if not needed
                ", userId=" + (user != null ? user.getId() : "null") +
                ", carId=" + (car != null ? car.getId() : "null") +
                ", driverId=" + (driver != null ? driver.getId() : "null") +
                ", issuer=" + issuer +
                ", receiver=" + receiver +
                ", fine=" + fine +
                ", issuedDate=" + issuedDate +
                ", expectedReturnDate=" + expectedReturnDate +
                ", returnedDate=" + returnedDate +
                ", status=" + status +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Builder pattern
    public static class Builder {
        private int id;
        private UUID uuid;
        private User user;
        private Car car;
        private Driver driver;
        private UUID issuer;
        private UUID receiver;
        private int fine;
        private LocalDateTime issuedDate;
        private LocalDateTime expectedReturnDate;
        private LocalDateTime returnedDate;
        private RentalStatus status;
        private boolean deleted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder() {} // Default constructor for builder

        public Builder setId(int id) { this.id = id; return this; }
        public Builder setUuid(UUID uuid) { this.uuid = uuid; return this; }
        public Builder setUser(User user) { this.user = user; return this; }
        public Builder setCar(Car car) { this.car = car; return this; }
        public Builder setDriver(Driver driver) { this.driver = driver; return this; }
        public Builder setIssuer(UUID issuer) { this.issuer = issuer; return this; }
        public Builder setReceiver(UUID receiver) { this.receiver = receiver; return this; }
        public Builder setFine(int fine) { this.fine = fine; return this; }
        public Builder setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; return this; }
        public Builder setExpectedReturnDate(LocalDateTime expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; return this; }
        public Builder setReturnedDate(LocalDateTime returnedDate) { this.returnedDate = returnedDate; return this; }
        public Builder setStatus(RentalStatus status) { this.status = status; return this; }
        public Builder setDeleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Builder copy(Rental rental) {
            this.id = rental.id;
            this.uuid = rental.uuid; // Make sure UUID is copied
            this.user = rental.user;
            this.car = rental.car;
            this.driver = rental.driver;
            this.issuer = rental.issuer;
            this.receiver = rental.receiver;
            this.fine = rental.fine;
            this.issuedDate = rental.issuedDate;
            this.expectedReturnDate = rental.expectedReturnDate;
            this.returnedDate = rental.returnedDate;
            this.status = rental.status;
            this.deleted = rental.deleted;
            this.createdAt = rental.createdAt;
            this.updatedAt = rental.updatedAt;
            return this;
        }

        public Rental build() {
            // For new instances or when updating, set updatedAt before building
            this.updatedAt = LocalDateTime.now();
            if (this.uuid == null) { // Ensure UUID for new entities if not copied
                this.uuid = UUID.randomUUID();
            }
            if (this.createdAt == null) { // Ensure createdAt for brand new entities
                this.createdAt = this.updatedAt; // createdAt is same as first updatedAt
            }
            // Default status for brand new rentals (if not explicitly set by .status() or .copy())
            if (this.status == null && this.id == 0) { // id == 0 implies new entity not yet persisted
                this.status = RentalStatus.ACTIVE;
            }
            return new Rental(this);
        }
    }
}