package za.ac.cput.repository;

import za.ac.cput.domain.Payment;

import java.util.ArrayList;
import java.util.List;

public class IPaymentRepositoryImpl implements IPaymentRepository {
    private List<Payment> paymentDB;
    private static IPaymentRepositoryImpl repository = null;

    private IPaymentRepositoryImpl() {
        paymentDB = new ArrayList<>();
    }

    // Singleton
    public static IPaymentRepositoryImpl getRepository() {
        if (repository == null) {
            repository = new IPaymentRepositoryImpl();
        }
        return repository;
    }

    @Override
    public Payment create(Payment payment) {
        paymentDB.add(payment);
        return payment;
    }

    @Override
    public Payment read(Integer id) {
        Payment payment = paymentDB.stream()
                .filter(p -> p.getId()==id)
                .findAny()
                .orElse(null);
        return payment;
    }

    @Override
    public Payment update(Payment payment) {
        Payment old = read(payment.getId());
        if (old != null) {
            paymentDB.remove(old);
            paymentDB.add(payment);
            return payment;
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Payment paymentToDelete = read(id);
        if (paymentToDelete == null)
            return false;
        paymentDB.remove(paymentToDelete);
        return true;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentDB;
    }

    @Override
    public Payment getPaymentById(Integer id) {
        return read(id);
    }
}
