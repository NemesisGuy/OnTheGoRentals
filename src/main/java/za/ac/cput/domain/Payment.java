package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String paymentMethod;
    private double amount;
    private String status;

    public Payment() {
    }

    public Payment(Builder builder) {
        this.id = builder.id;
        this.paymentMethod = builder.paymentMethod;
        this.amount = builder.amount;
        this.status = builder.status;
    }

    public int getId() {
        return id;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id == payment.id &&
                Double.compare(payment.amount, amount) == 0 &&
                Objects.equals(paymentMethod, payment.paymentMethod) &&
                Objects.equals(status, payment.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentMethod, amount, status);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }

    public static class Builder {
        private int id;
        private String paymentMethod;
        private double amount;
        private String status;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder copy(Payment payment) {
            this.id = payment.id;
            this.paymentMethod = payment.paymentMethod;
            this.amount = payment.amount;
            this.status = payment.status;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
