package za.ac.cput.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.RentalStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Rental.java
 * Entity for the Rental. Designed with an immutable public API.
 * State changes are exclusively handled via the inner Builder class.
 *
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    private UUID issuer;
    private UUID receiver;
    private int fine;
    private LocalDateTime issuedDate;
    private LocalDateTime expectedReturnDate;
    private LocalDateTime returnedDate;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    private boolean deleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Setters are now PRIVATE to enforce the builder pattern for all state changes.
    private void setId(int id) { this.id = id; }
    private void setUuid(UUID uuid) { this.uuid = uuid; }
    private void setUser(User user) { this.user = user; }
    private void setCar(Car car) { this.car = car; }
    private void setDriver(Driver driver) { this.driver = driver; }
    private void setIssuer(UUID issuer) { this.issuer = issuer; }
    private void setReceiver(UUID receiver) { this.receiver = receiver; }
    private void setFine(int fine) { this.fine = fine; }
    private void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }
    private void setExpectedReturnDate(LocalDateTime expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }
    private void setReturnedDate(LocalDateTime returnedDate) { this.returnedDate = returnedDate; }
    private void setStatus(RentalStatus status) { this.status = status; }
    private void setDeleted(boolean deleted) { this.deleted = deleted; }
    private void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    private void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }


    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- BUILDER ---
    public static class Builder {
        private int id;
        private UUID uuid;
        private User user;
        private Car car;
        // ... all other fields from Rental
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

        // --- Builder Setters ---
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
            if (rental == null) return this;
            this.id = rental.id;
            this.uuid = rental.uuid;
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

        /**
         * Creates a new instance of a Rental object from the builder's state.
         * @return A new Rental object.
         */
        public Rental build() {
            Rental rental = new Rental();
            this.applyTo(rental); // Use applyTo to set fields
            return rental;
        }

        /**
         * Applies the builder's current state to an existing Rental object.
         * This is the key method for updating managed JPA entities without public setters.
         * @param rental The target Rental object to modify.
         * @return The modified Rental object.
         */
        public Rental applyTo(Rental rental) {
            rental.setId(this.id);
            rental.setUuid(this.uuid);
            rental.setUser(this.user);
            rental.setCar(this.car);
            rental.setDriver(this.driver);
            rental.setIssuer(this.issuer);
            rental.setReceiver(this.receiver);
            rental.setFine(this.fine);
            rental.setIssuedDate(this.issuedDate);
            rental.setExpectedReturnDate(this.expectedReturnDate);
            rental.setReturnedDate(this.returnedDate);
            rental.setStatus(this.status);
            rental.setDeleted(this.deleted);
            rental.setCreatedAt(this.createdAt);
            rental.setUpdatedAt(this.updatedAt);
            return rental;
        }
    }
}