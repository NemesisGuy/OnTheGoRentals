package za.ac.cput.factory.impl;

import za.ac.cput.domain.Payment;

public class PaymentFactory {
    public static Payment createPayment(int id, String paymentMethod, double amount, String status) {
        return new Payment.Builder()
                .setId(id)
                .setPaymentMethod(paymentMethod)
                .setAmount(amount)
                .setStatus(status)
                .build();
    }
}
