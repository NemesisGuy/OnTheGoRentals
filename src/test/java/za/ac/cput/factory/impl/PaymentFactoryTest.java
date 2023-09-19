package za.ac.cput.factory.impl;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Payment;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentFactoryTest {

    @Test
    void testCreatePayment() {
        int id = 1;
        String paymentMethod = "Credit Card";
        double amount = 100.00;
        String status = "Paid";

        Payment payment = PaymentFactory.createPayment(id, paymentMethod, amount, status);

        assertEquals(id, payment.getId());
        assertEquals(paymentMethod, payment.getPaymentMethod());
        assertEquals(amount, payment.getAmount());
        assertEquals(status, payment.getStatus());
    }
}
