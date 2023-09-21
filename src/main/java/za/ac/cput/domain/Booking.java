package za.ac.cput.domain;

import jakarta.persistence.*;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Booking {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private double totalPrice;

    private boolean confirmed;

    public Booking() {
        // Default constructor
    }


    private Booking(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.car = builder.car;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.totalPrice = builder.totalPrice;
        this.confirmed = builder.confirmed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Car getCar() {
        return car;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public static class Builder {
        private int id;
        private User user;
        private Car car;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private double totalPrice;
        private boolean confirmed;

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

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder totalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder confirmed(boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public Booking build() {
            return new Booking(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return getId() == booking.getId() &&
                Double.compare(booking.getTotalPrice(), getTotalPrice()) == 0 &&
                isConfirmed() == booking.isConfirmed() &&
                Objects.equals(getUser(), booking.getUser()) &&
                Objects.equals(getCar(), booking.getCar()) &&
                Objects.equals(getStartDate(), booking.getStartDate()) &&
                Objects.equals(getEndDate(), booking.getEndDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getCar(), getStartDate(), getEndDate(), getTotalPrice(), isConfirmed());
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", user=" + user +
                ", car=" + car +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalPrice=" + totalPrice +
                ", confirmed=" + confirmed +
                '}';
    }
}
