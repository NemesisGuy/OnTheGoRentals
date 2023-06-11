package za.ac.cput.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.impl.Payment;
import za.ac.cput.factory.impl.PaymentFactory;
import za.ac.cput.repository.impl.IPaymentRepositoryImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class IPaymentRepositoryImplTest {
    private static IPaymentRepositoryImpl repository = IPaymentRepositoryImpl.getRepository();
    private static PaymentFactory paymentFactory = new PaymentFactory();
    private static Payment payment = paymentFactory.create();
    private static Payment payment2;


    @Test
    void a_create() {
        Payment created = repository.create(payment);
        assertEquals(payment.getId(), created.getId());
        System.out.println("Created: " + created);
    }

    @Test
    void b_read() {
        Payment read = repository.read(payment.getId());
        Assertions.assertNotNull(read);
        System.out.println("Read: " + read);
    }

    @Test
    void c_update() {
        Payment updated = new Payment.Builder()
                .copy(payment)
                .setPaymentAmount(40000)
                .setPaymentMethod("Credit")
                .setPaymentDate(LocalDate.parse("01-01-23", DateTimeFormatter.ofPattern("MM-dd-yy")))
                .build();
        Assertions.assertNotEquals(updated, payment);
        System.out.println("Updated: " + updated);
    }

    @Test
    void d_getAllPayments() {
        payment2 = paymentFactory.create();
        Payment created = repository.create(payment2);

        List<Payment> list = repository.getAllPayments();
        System.out.println("\nShow all:");
        for (Payment payment : list) {
            System.out.println(payment);
        }
        assertNotSame(payment, payment2);
    }

    @Test
    void e_getPaymentById() {
        Payment id = repository.getPaymentById(payment.getId());
        System.out.println("\nSearch by Id: " + id);
        assertNotNull(id);
    }

    @Test
    void f_delete() {
        boolean deleted = repository.delete(payment.getId());
        Assertions.assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }





}