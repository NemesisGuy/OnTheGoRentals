package za.ac.cput.repository;

import za.ac.cput.domain.Payment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Payment read(String id) {
        Payment payment = paymentDB.stream()
                .filter(p -> p.getPaymentId().equals(id))
                .findAny()
                .orElse(null);
        return payment;

    }

    @Override
    public Payment update(Payment payment) {
        Payment old = read(payment.getPaymentId());
        if (old != null) {
            paymentDB.remove(old);
            paymentDB.add(payment);
            return payment;
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
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
    public Payment getPaymentById(String id) {
        return read(id);
    }

}
