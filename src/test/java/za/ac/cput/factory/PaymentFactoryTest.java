package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.Payment;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PaymentFactoryTest {

    @Test
    void testPaymentFactory_pass() {
        Payment payment = PaymentFactory.createPayment
                (
                        20000,
                        "cash",
                        LocalDate.parse("2023-01-01"),
                        null
                );

        System.out.println(payment.toString());
        Assertions.assertNotNull(payment);
    }

    @Test
    void testPaymentFactory_fail() {
        Payment payment = PaymentFactory.createPayment
                (
                        20000,
                        "cash",
                        LocalDate.parse("01-01-2023"),
                        null
                );

        System.out.println(payment.toString());
        Assertions.assertNotNull(payment);
    }

}