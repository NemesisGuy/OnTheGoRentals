package za.ac.cput.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Peter Buckingham - 220165289
 * Date: April 2023
 * Rental Class.java
 */


@Entity
public class Rental {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne (cascade = CascadeType.ALL)//many rentals to one user
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne (cascade = CascadeType.ALL) //many rentals to one car
    @JoinColumn(name = "car_id")
    private Car car;

    @OneToMany(mappedBy = "rental")
    private List<DamageReport> damageReportList = new ArrayList<>();
    //driver
    @ManyToOne //many rentals to one driver
    @JoinColumn(name = "driver_id")
    private Driver driver;

    private int issuer;
    private int receiver;
    private int fine;
    private LocalDateTime issuedDate;
    private LocalDateTime returnedDate;

    //Initializing a parameterized constructor
    public Rental(int id, User user, Car car, int issuer, int receiver, int fine, LocalDateTime issuedDate, LocalDateTime returnedDate) {

        this.id = id;
        this.user = user;
        this.car = car;
        this.issuer = issuer;
        this.issuedDate = issuedDate;
        this.returnedDate = returnedDate;
        this.receiver = receiver;
        this.fine = fine;
    }

    private Rental(Builder builder) {

        this.id = builder.id;
        this.user = builder.user;
        this.car = builder.car;
        this.receiver = builder.receiver;
        this.issuer = builder.issuer;
        this.issuedDate = builder.issuedDate;
        this.returnedDate = builder.returnedDate;
        this.fine = builder.fine;

    }

    public Rental() {

    }

    //Builder Class
    public static Builder builder() {
        return new Builder();
    }

    public int getRentalId() {
        return this.id;
    }
    public int getId() {
        return 0;
    }

    public int getFine() {
        return fine;
    }

    public int getIssuer() {
        return issuer;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }


    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public int getReceiver() {
        return receiver;
    }


    public boolean finePaid() {
        return false;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "rentalId=" + id +
                ", issuer='" + issuer + '\'' +
                ", issuedDate='" + issuedDate + '\'' +
                ", dateReturned='" + returnedDate + '\'' +
                ", receiver='" + receiver + '\'' +
                ", finePaid=" + fine +
                '}';
    }

    public User getUser() {
        return this.user;

    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return this.car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public static class Builder {

        private int id;
        private User user;
        private Car car;
        private int issuer;
        private int receiver;
        private int fine;

        private LocalDateTime issuedDate;
        private LocalDateTime returnedDate;

        public Builder setId(int id) {
            this.id = id;
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

        public Builder setIssuer(int issuer) {
            this.issuer = issuer;
            return this;
        }


        public Builder setIssuedDate(LocalDateTime issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Builder setDateReturned(LocalDateTime returnedDate) {
            this.returnedDate = returnedDate;
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

        public Rental build() {
            return new Rental(this);
        }

        public Builder copy(Rental rental) {
            this.id = rental.id;
            this.user = rental.user;
            this.car = rental.car;
            this.issuer = rental.issuer;
            this.issuedDate = rental.issuedDate;
            this.returnedDate = rental.returnedDate;
            this.receiver = rental.receiver;
            this.fine = rental.fine;
            return this;
        }
    }
}