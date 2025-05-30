package za.ac.cput.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.domain.entity.security.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Peter Buckingham - 220165289
 * Date: April 2023
 * Rental Class.java
 */
@Getter

@Entity
public class Rental implements Serializable {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY) // Good to keep LAZY

    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Good to keep LAZY
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY) // Good to keep LAZY
    @JoinColumn(name = "driver_id")
    private Driver driver;
    private int issuer;
    private int receiver;
    private int fine;
    @Column(nullable = false)
    private LocalDateTime issuedDate;
    private LocalDateTime expectedReturnDate; // Optional, can be used for future enhancements
    private LocalDateTime returnedDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;
    @Column(nullable = false)
    private boolean deleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt; // Added for tracking
//    @Column(nullable = false)
    private LocalDateTime updatedAt; // Added for tracking

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) this.uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.deleted = false; // Default
        if (this.status == null) this.status = RentalStatus.PENDING_CONFIRMATION; // Default status
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public Rental() {
    }

    public Rental(int id, User user, Car car, int issuer, int receiver, int fine, LocalDateTime issuedDate, LocalDateTime expectedReturnDate,LocalDateTime returnedDate, RentalStatus status , LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.car = car;
        this.issuer = issuer;
        this.receiver = receiver;
        this.fine = fine;
        this.issuedDate = issuedDate;
        this.expectedReturnDate = expectedReturnDate;
        this.returnedDate = returnedDate;
        this.deleted = false;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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
        this.deleted = builder.deleted;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rental rental = (Rental) o;
        return id == rental.id && issuer == rental.issuer && receiver == rental.receiver && fine == rental.fine && deleted == rental.deleted && Objects.equals(uuid, rental.uuid) && Objects.equals(user, rental.user) && Objects.equals(car, rental.car) && Objects.equals(driver, rental.driver) && Objects.equals(issuedDate, rental.issuedDate) && Objects.equals(returnedDate, rental.returnedDate) && status == rental.status && Objects.equals(createdAt, rental.createdAt) && Objects.equals(updatedAt, rental.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, user, car, driver, issuer, receiver, fine, issuedDate, returnedDate, status, deleted, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", user=" + user +
                ", car=" + car +
                ", driver=" + driver +
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
        private int issuer;
        private int receiver;
        private int fine;
        private LocalDateTime issuedDate;
        private LocalDateTime expectedReturnDate;
        private LocalDateTime returnedDate;
        private RentalStatus status;
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

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setCar(Car car) {
            this.car = car;
            return this;
        }

        public Builder setDriver(Driver driver) {
            this.driver = driver;
            return this;
        }

        public Builder setIssuer(int issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder setReceiver(int receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder setFine(int fine) {
            this.fine = fine;
            return this;
        }

        public Builder setIssuedDate(LocalDateTime issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }
        public Builder setExpectedReturnDate(LocalDateTime expectedReturnDate) {
            this.expectedReturnDate = expectedReturnDate;
            return this;
        }

        public Builder setReturnedDate(LocalDateTime returnedDate) {
            this.returnedDate = returnedDate;
            return this;
        }

        public Builder setStatus(RentalStatus status) {
            this.status = status;
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

        public Builder copy(Rental rental) {
            this.id = rental.id;
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
            return this;
        }

        public Rental build() {
            return new Rental(this);
        }
    }

}
