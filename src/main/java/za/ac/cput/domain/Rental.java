package za.ac.cput.domain;

import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.domain.security.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Peter Buckingham - 220165289
 * Date: April 2023
 * Rental Class.java
 */

@Entity
public class Rental implements Serializable {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY) // Good to keep LAZY

    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Good to keep LAZY
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY) // Good to keep LAZY
    @JoinColumn(name = "driver_id")
    private Driver driver;

    private int issuer;
    private int receiver;
    private int fine;
    private LocalDateTime issuedDate;
    private LocalDateTime returnedDate;
    @Enumerated(EnumType.STRING)
    private RentalStatus status;
    private boolean deleted = false;
    @PrePersist
    protected  void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }
    public Rental() {
    }

    public Rental(int id, User user, Car car, int issuer, int receiver, int fine, LocalDateTime issuedDate, LocalDateTime returnedDate, RentalStatus status) {
        this.id = id;
        this.user = user;
        this.car = car;
        this.issuer = issuer;
        this.receiver = receiver;
        this.fine = fine;
        this.issuedDate = issuedDate;
        this.returnedDate = returnedDate;
        this.deleted = false;
        this.status = status;
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
        this.returnedDate = builder.returnedDate;
        this.deleted = builder.deleted;
        this.status = builder.status;
    }

    public int getId() {
        return id;
    }
    public UUID getUuid() {
        return uuid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public int getIssuer() {
        return issuer;
    }

    public void setIssuer(int issuer) {
        this.issuer = issuer;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public boolean finePaid() {
        return false; // Placeholder logic
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rental rental = (Rental) o;
        return id == rental.id && issuer == rental.issuer && receiver == rental.receiver && fine == rental.fine && deleted == rental.deleted && Objects.equals(uuid, rental.uuid) && Objects.equals(user, rental.user) && Objects.equals(car, rental.car) && Objects.equals(driver, rental.driver) && Objects.equals(issuedDate, rental.issuedDate) && Objects.equals(returnedDate, rental.returnedDate) && status == rental.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, user, car, driver, issuer, receiver, fine, issuedDate, returnedDate, status, deleted);
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
                ", returnedDate=" + returnedDate +
                ", status=" + status +
                ", deleted=" + deleted +
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
        private LocalDateTime returnedDate;
        private RentalStatus status;
        private boolean deleted;



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

        public Builder copy(Rental rental) {
            this.id = rental.id;
            this.user = rental.user;
            this.car = rental.car;
            this.driver = rental.driver;
            this.issuer = rental.issuer;
            this.receiver = rental.receiver;
            this.fine = rental.fine;
            this.issuedDate = rental.issuedDate;
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
