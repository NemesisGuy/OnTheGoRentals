package za.ac.cput.domain.impl;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import za.ac.cput.domain.IRent;

import java.time.LocalDateTime;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * Rental Class.java
 */


@Entity
public class Rental implements IRent {
    //Declare the private variables
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rentalId;
    //users rent cars ,
    // Define the relationships
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "carId")
    private Car car;
    private int issuer;
    private int receiver;
    private int fine;
    private LocalDateTime issuedDate;
    private LocalDateTime  returnedDate;




    //Initializing a parameterized constructor
    public Rental(int rentalId, Car car,  int issuer,int receiver, int fine, LocalDateTime issuedDate,  LocalDateTime returnedDate) {

        this.rentalId = rentalId;
        this.car = car;
        this.issuer = issuer;
        this.issuedDate = issuedDate;
        this.returnedDate = returnedDate;
        this.receiver = receiver;
        this.fine = fine;
    }


    private Rental(RentalBuilder builder) {

        this.rentalId = builder.rentalId;
        this.issuer = builder.issuer;
        this.issuedDate = builder.issuedDate;
        this.returnedDate = builder.returnedDate;
        this.fine = builder.fine;

    }

    public Rental() {

    }

    public int getRentalId() {
        return this.rentalId;
    }



    @Override
    public int getId() {
        return 0;
    }

    @Override
    public int getBorrower() {
        return 0;
    }


    public int getIssuer() {
        return issuer;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }


    @Override
    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public int getReceiver() {
        return receiver;
    }

    @Override
    public boolean finePaid() {
        return false;
    }


    @Override
    public String toString() {
        return "Rental{" +
                "rentalId=" + rentalId +


                ", issuer='" + issuer + '\'' +
                ", issuedDate='" + issuedDate + '\'' +
                ", dateReturned='" + returnedDate + '\'' +
                ", receiver='" + receiver + '\'' +
                ", finePaid=" + fine +
                '}';
    }

    public User getUser() {
        return user;

    }

    public Car getCar() {
        return car;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setCar(Car car) {
        this.car = car;
    }

    //Builder Class
    public static class RentalBuilder {

        private int rentalId;
        private int userId;
        private int carId;
        private int issuer;
        private int receiver;
        private int fine;

        private LocalDateTime issuedDate;
        private LocalDateTime returnedDate;

        public Rental.RentalBuilder setRentalId(int rentalId) {
            this.rentalId = rentalId;
            return this;
        }

        public Rental.RentalBuilder setBorrower(int userId) {
            this.userId = userId;
            return this;
        }

        public Rental.RentalBuilder setCar(int carId) {
            this.carId = carId;
            return this;
        }

        public Rental.RentalBuilder setIssuer(int issuer) {
            this.issuer = issuer;
            return this;
        }



        public Rental.RentalBuilder setIssuedDate(LocalDateTime issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Rental.RentalBuilder setDateReturned(LocalDateTime returnedDate) {
            this.returnedDate = returnedDate;
            return this;
        }

        public Rental.RentalBuilder setReceiver(int receiver) {
            this.receiver = receiver;
            return this;
        }

        public Rental.RentalBuilder setFinePaid(int fine) {
            this.fine = fine;
            return this;
        }

        public Rental build() {
            return new Rental(this);
        }
    }
}