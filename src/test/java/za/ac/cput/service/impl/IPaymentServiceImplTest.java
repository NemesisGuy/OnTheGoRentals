package za.ac.cput.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.Payment;
import za.ac.cput.factory.impl.PaymentFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
class IPaymentServiceImplTest {

    @Autowired
    private IPaymentServiceImpl service;
    private static PaymentFactory paymentFactory = new PaymentFactory();
    private static Payment payment = paymentFactory.create();

    @Test
    void a_create() {
        Payment add1 = service.create(payment);
        System.out.println("Create : " + add1);
        Assertions.assertNotNull(add1);
    }

    @Test
    void b_read() {
        Payment read = service.read(payment.getId());
        System.out.println("Read: " + read);
        Assertions.assertNotNull(read);
    }

    @Test
    void c_update() {
        Payment updated = new Payment.Builder().copy(payment)
                .setPaymentAmount(40000)
                .setPaymentMethod("Credit")
                .setPaymentDate(LocalDate.parse("01-01-23", DateTimeFormatter.ofPattern("MM-dd-yy")))
                .build();
        System.out.println("Updated: " + service.update(updated));
        Assertions.assertNotSame(updated, payment);
    }

    @Test
    void d_getPaymentById() {
        final String METHOD = "Credit";
        System.out.println("show all by Method: " + METHOD);

        List<Payment> methodList = service.findAllByPaymentMethod(METHOD);
        for (Payment payment: methodList) {
            System.out.println(payment);
        }
    }

    @Test
    void e_getAllPayments() {
        System.out.println("Show all: ");
        System.out.println(service.getAllPayments());
    }

    @Test
    void f_delete() {
        boolean success = service.delete(payment.getId());
        Assertions.assertTrue(success);
        System.out.println("Success: " + success);
        System.out.println(service.getAllPayments());
    }
}