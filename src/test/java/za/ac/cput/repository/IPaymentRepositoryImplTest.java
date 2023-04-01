package za.ac.cput.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.Payment;
import za.ac.cput.factory.PaymentFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class IPaymentRepositoryImplTest {
    private static IPaymentRepositoryImpl repository = IPaymentRepositoryImpl.getRepository();
    private static Payment payment = PaymentFactory.createPayment
                    (
                            20000,
                            "cash",
                            LocalDate.parse("2023-01-01"),
                            null
                    );

    @Test
    void a_create() {
        Payment created = repository.create(payment);
        System.out.println("Created: " + created);
        assertEquals(payment.getPaymentId(), created.getPaymentId());
    }

    @Test
    void b_read() {
        Payment read = repository.read(payment.getPaymentId());
        System.out.println("Read: " + read);
        Assertions.assertNotNull(read);
    }

    @Test
    void c_update() {
        Payment updated = new Payment.Builder()
                .copy(payment)
                .setPaymentAmount(40000)
                .setPaymentMethod("Credit")
                .build();
        Assertions.assertNotNull(repository.update(updated));
        System.out.println("Updated: " + updated);

    }

    @Test
    void e_getPaymentById() {
        Payment getById = repository.getPaymentById(payment.getPaymentId());
        System.out.println("Get By ID: " + getById);
    }

    @Test
    void d_delete() {
        boolean deleted = repository.delete(payment.getPaymentId());
        assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }

    @Test
    void f_getAllPayments() {
        System.out.println("Show all: " + repository.getAllPayments());
        assertNotEquals(repository, repository.toString());
    }



}