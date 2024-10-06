package za.ac.cput.domain;

import jakarta.persistence.*;
import za.ac.cput.domain.security.User;

import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

 /*   @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;*/

    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status; // PENDING, CONFIRMED, CANCELED

    // Constructors

    public Booking() {
    }

    public Booking(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.car = builder.car;
//this.driver = builder.driver;
        this.bookingStartDate = builder.bookingDate;
        this.bookingEndDate = builder.returnedDate;
        this.status = builder.status;
    }

    // Getters

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Car getCar() {
        return car;
    }

//    public Driver getDriver() {
//        return driver;
//    }

    public LocalDateTime getBookingStartDate() {
        return bookingStartDate;
    }

    public LocalDateTime getBookingEndDate() {
        return bookingEndDate;
    }

    // Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCar(Car car) {
        this.car = car;
    }

//    public void setDriver(Driver driver) {
//        this.driver = driver;
//    }

    public void setBookingStartDate(LocalDateTime bookingDate) {
        this.bookingStartDate = bookingDate;
    }

    public void setBookingEndDate(LocalDateTime returnedDate) {
        this.bookingEndDate = returnedDate;
    }


    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", user=" + user +
                ", car=" + car +
//                ", driver=" + driver +
                ", bookingDate=" + bookingStartDate +
                ", returnedDate=" + bookingEndDate +
                ", status='" + status +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private User user;
        private Car car;
//        private Driver driver;
        private LocalDateTime bookingDate;
        private LocalDateTime returnedDate;
        private String status;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder car(Car car) {
            this.car = car;
            return this;
        }
//
//        public Builder driver(Driver driver) {
////            this.driver = driver;
//            return this;
//        }

        public Builder bookingDate(LocalDateTime bookingDate) {
            this.bookingDate = bookingDate;
            return this;
        }

        public Builder returnedDate(LocalDateTime returnedDate) {
            this.returnedDate = returnedDate;
            return this;
        }
        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Booking build() {
            return new Booking(this);
        }

        public Builder copy(Booking booking) {
            this.id = booking.id;
            this.user = booking.user;
            this.car = booking.car;
//            this.driver = booking.driver;
            this.bookingDate = booking.bookingStartDate;
            this.returnedDate = booking.bookingEndDate;
            this.status = booking.status;
            return this;
        }
    }
}
