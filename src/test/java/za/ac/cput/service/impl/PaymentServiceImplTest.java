package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.ac.cput.domain.Payment;
import za.ac.cput.service.PaymentService;
import za.ac.cput.service.impl.PaymentServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

    private PaymentService paymentService = new PaymentServiceImpl();

    @Mock
    private PaymentService mockPaymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreatePayment() {
        Payment payment = new Payment();

        when(mockPaymentService.createPayment(payment)).thenReturn(payment);

        Payment createdPayment = paymentService.createPayment(payment);

        assertEquals(payment, createdPayment);

        verify(mockPaymentService, times(1)).createPayment(payment);
    }

    @Test
    void testUpdatePayment() {
        Payment payment = new Payment();

        when(mockPaymentService.updatePayment(payment)).thenReturn(payment);

        Payment updatedPayment = paymentService.updatePayment(payment);

        assertEquals(payment, updatedPayment);

        verify(mockPaymentService, times(1)).updatePayment(payment);
    }

    @Test
    void testDeletePayment() {
        int paymentId = 1;

        doNothing().when(mockPaymentService).deletePayment(paymentId);

        paymentService.deletePayment(paymentId);

        verify(mockPaymentService, times(1)).deletePayment(paymentId);
    }

    @Test
    void testGetAllPayments() {
        List<Payment> payments = new ArrayList<>();

        when(mockPaymentService.getAllPayments()).thenReturn(payments);

        List<Payment> retrievedPayments = paymentService.getAllPayments();

        assertEquals(payments, retrievedPayments);

        verify(mockPaymentService, times(1)).getAllPayments();
    }

    @Test
    void testGetPaymentsByUserId() {
        int userId = 1;
        List<Payment> payments = new ArrayList<>();

        when(mockPaymentService.getPaymentsByUserId(userId)).thenReturn(payments);

        List<Payment> retrievedPayments = paymentService.getPaymentsByUserId(userId);

        assertEquals(payments, retrievedPayments);

        verify(mockPaymentService, times(1)).getPaymentsByUserId(userId);
    }

    @Test
    void testGetPaymentsByRentalId() {
        int rentalId = 1;
        List<Payment> payments = new ArrayList<>();

        when(mockPaymentService.getPaymentsByRentalId(rentalId)).thenReturn(payments);

        List<Payment> retrievedPayments = paymentService.getPaymentsByRentalId(rentalId);

        assertEquals(payments, retrievedPayments);

        verify(mockPaymentService, times(1)).getPaymentsByRentalId(rentalId);
    }

    @Test
    void testGetPaymentById() {
        int paymentId = 1;
        Payment payment = new Payment();

        when(mockPaymentService.getPaymentById(paymentId)).thenReturn(payment);

        Payment retrievedPayment = paymentService.getPaymentById(paymentId);

        assertEquals(payment, retrievedPayment);

        verify(mockPaymentService, times(1)).getPaymentById(paymentId);
    }
}
