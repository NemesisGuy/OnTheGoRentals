package za.ac.cput.domain;
/**
 * Payment.java
 * Class for the Payment
 * Author: Aqeel Hanslo (219374422)
 * Date: 30 March 2023
 */


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Payment  {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;
    private double paymentAmount;
    private String paymentMethod;
    private LocalDate paymentDate;
    //private Rental rentalId;
    private int rentalId;

    public Payment() {
    }

    public Payment(Builder builder) {
        this.paymentId = builder.paymentId;
        this.paymentAmount = builder.paymentAmount;
        this.paymentMethod = builder.paymentMethod;
        this.paymentDate = builder.paymentDate;
        this.rentalId = builder.rentalId;
    }

    public int getId() {
        return paymentId;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public int getRentalId() {
        return rentalId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", paymentAmount=" + paymentAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentDate='" + paymentDate + '\'' +
                ", rentalId='" + rentalId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return paymentId == payment.paymentId && Double.compare(payment.paymentAmount, paymentAmount) == 0 && Objects.equals(paymentMethod, payment.paymentMethod) && Objects.equals(paymentDate, payment.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, paymentAmount, paymentMethod, paymentDate);
    }

    // Inner Builder Class
    public static class Builder {
        private int paymentId;
        private double paymentAmount;
        private String paymentMethod;
        private LocalDate paymentDate;
        private int rentalId;

        public Builder setPaymentId(int paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder setPaymentAmount(double paymentAmount) {
            this.paymentAmount = paymentAmount;
            return this;
        }

        public Builder setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder setPaymentDate(LocalDate paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public Builder setRentalId(int rentalId) {
            this.rentalId = rentalId;
            return this;
        }

        public Builder copy(Payment payment) {
            this.paymentId = payment.paymentId;
            this.paymentAmount = payment.paymentAmount;
            this.paymentMethod = payment.paymentMethod;
            this.paymentDate = payment.paymentDate;
            this.rentalId = payment.rentalId;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
