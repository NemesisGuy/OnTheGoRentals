package za.ac.cput.domain;

import org.junit.jupiter.api.*;
import za.ac.cput.scratch.Rental;
import java.time.LocalDate;

class PaymentTest {
    private Payment payment;
    private double paymentAmount = 2000.0;
    private String paymentMethod = "Credit";
    private LocalDate paymentDate = LocalDate.parse("2023-01-01");
    private Rental rentalId = null;

    @Test
    public void testPayment() {

        payment = new Payment.Builder()
                .setPaymentAmount(paymentAmount)
                .setPaymentMethod(paymentMethod)
                .setPaymentDate(paymentDate)
                .setRentalId(rentalId)
                .build();

        System.out.println(payment.toString());

        Assertions.assertEquals(paymentAmount, payment.getPaymentAmount());
        Assertions.assertEquals(paymentMethod, payment.getPaymentMethod());
        Assertions.assertEquals(paymentDate, payment.getPaymentDate());
        Assertions.assertEquals(rentalId, payment.getRentalId());
    }

}