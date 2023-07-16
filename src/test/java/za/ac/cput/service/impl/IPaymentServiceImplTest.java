package za.ac.cput.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.Payment;
import za.ac.cput.factory.impl.PaymentFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@TestMethodOrder(MethodOrderer.MethodName.class)
class IPaymentServiceImplTest {
    private static IPaymentServiceImpl service = IPaymentServiceImpl.getPaymentService();
    private static PaymentFactory paymentFactory = new PaymentFactory();
    private static Payment pay = paymentFactory.create();

    @Test
    void a_create() {
        Payment add1 = service.create(pay);
        System.out.println("Create : " + add1);
        Assertions.assertNotNull(add1);
    }

    @Test
    void b_read() {
        Payment read = service.read(pay.getId());
        System.out.println("Read: " + read);
        Assertions.assertNotNull(read);
    }

    @Test
    void c_update() {
        Payment updated = new Payment.Builder()
                .copy(pay)
                .setPaymentAmount(40000)
                .setPaymentMethod("Credit")
                .setPaymentDate(LocalDate.parse("01-01-23", DateTimeFormatter.ofPattern("MM-dd-yy")))
                .build();
        System.out.println("Updated: " + service.update(updated));
        Assertions.assertNotSame(updated, pay);
    }

    @Test
    void d_getPaymentById() {
        Payment getById = service.getPaymentById(pay.getId());
        System.out.println("Get By ID: " + getById);
    }

    @Test
    void e_getAllPayments() {
        System.out.println("Show all: ");
        System.out.println(service.getAllPayments());
    }

    @Test
    void f_delete() {
        boolean success = service.delete(pay.getId());
        Assertions.assertTrue(success);
        System.out.println("Success: " + success);
        System.out.println(service.getAllPayments());
    }
}