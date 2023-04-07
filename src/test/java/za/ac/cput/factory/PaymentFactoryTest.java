package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Payment;
import java.time.LocalDate;

class PaymentFactoryTest {

    @Test
    void testPaymentFactory_pass() {
        PaymentFactory paymentFactory = new PaymentFactory();
        Payment payment = paymentFactory.create();

        Assertions.assertNotNull(payment);
        Assertions.assertNotNull(payment.getId());
    }

    @Test
    void testPaymentFactory_fail() {
        PaymentFactory paymentFactory = new PaymentFactory();
        Payment payment = paymentFactory.create();

        Assertions.assertNull(payment);
        Assertions.assertNull(payment.getId());
    }

}