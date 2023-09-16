package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Payment;
import za.ac.cput.factory.impl.PaymentFactory;
import za.ac.cput.repository.PaymentRepository;
import za.ac.cput.service.PaymentService;
import java.util.List;
import java.util.Optional;

@Service("paymentServiceImpl")
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentFactory paymentFactory;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentFactory paymentFactory) {
        this.paymentRepository = paymentRepository;
        this.paymentFactory = paymentFactory;
    }

    @Override
    public Payment createPayment(Payment payment) {
        Payment newPayment = paymentFactory.create(payment);
        return paymentRepository.save(newPayment);
    }

    @Override
    public Payment updatePayment(Payment payment) {
        if (paymentRepository.existsById(payment.getId())) {
            Payment updatedPayment = paymentFactory.create(payment);
            return paymentRepository.save(updatedPayment);
        }
        return null;
    }

    @Override
    public void deletePayment(int paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getPaymentsByUserId(int userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> getPaymentsByRentalId(int rentalId) {
        return paymentRepository.findByRentalId(rentalId);
    }

    @Override
    public Payment getPaymentById(int paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        return optionalPayment.orElse(null);
    }
}