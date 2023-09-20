package za.ac.cput.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentTest {

    private int id;
    private String paymentMethod;
    private double amount;
    private String status;

    @Test
    public void testPayment() {
        Payment payment = new Payment.Builder()
                .setId(id)
                .setPaymentMethod("Credit Card")
                .setAmount(100.00)
                .setStatus("Paid")
                .build();

        System.out.println(payment.toString());

        assertEquals(id, payment.getId());
        assertEquals("Credit Card", payment.getPaymentMethod());
        assertEquals(100.00, payment.getAmount());
        assertEquals("Paid", payment.getStatus());
    }
}
