package za.ac.cput.service;

import za.ac.cput.domain.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(Payment payment);

    Payment updatePayment(Payment payment);

    void deletePayment(int paymentId);

    List<Payment> getAllPayments();

    List<Payment> getPaymentsByUserId(int userId);

    List<Payment> getPaymentsByRentalId(int rentalId);

    Payment getPaymentById(int paymentId);
}