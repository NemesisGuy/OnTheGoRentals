/**package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.ac.cput.domain.Payment;
import za.ac.cput.factory.impl.PaymentFactory;
import za.ac.cput.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentFactory paymentFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUpdatePayment() {
        Payment payment = new Payment();
        Payment updatedPayment = new Payment();

        when(paymentRepository.existsById(anyInt())).thenReturn(true);
        when(paymentRepository.save(payment)).thenReturn(updatedPayment);

        Payment result = paymentService.updatePayment(payment);

        assertNotNull(result);
        assertEquals(updatedPayment, result);

        verify(paymentRepository, times(1)).existsById(anyInt());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testUpdatePaymentWhenNotExists() {
        Payment payment = new Payment();

        when(paymentRepository.existsById(anyInt())).thenReturn(false);

        Payment result = paymentService.updatePayment(payment);

        assertNull(result);

        verify(paymentRepository, times(1)).existsById(anyInt());
        verify(paymentRepository, never()).save(payment);
    }

    @Test
    void testDeletePayment() {
        int paymentId = 1;
        paymentService.deletePayment(paymentId);
        verify(paymentRepository, times(1)).deleteById(paymentId);
    }

    @Test
    void testGetAllPayments() {
        List<Payment> payments = new ArrayList<>();
        when(paymentRepository.findAll()).thenReturn(payments);
        List<Payment> result = paymentService.getAllPayments();
        assertNotNull(result);
        assertEquals(payments, result);
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testGetPaymentById() {
        int paymentId = 1;
        Payment payment = new Payment();
        Optional<Payment> optionalPayment = Optional.of(payment);
        when(paymentRepository.findById(paymentId)).thenReturn(optionalPayment);
        Payment result = paymentService.getPaymentById(paymentId);
        assertNotNull(result);
        assertEquals(payment, result);
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPaymentByIdWhenNotExists() {
        int paymentId = 1;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
        Payment result = paymentService.getPaymentById(paymentId);
        assertNull(result);
        verify(paymentRepository, times(1)).findById(paymentId);
    }
}*/
