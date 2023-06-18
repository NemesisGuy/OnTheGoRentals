package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.Payment;
import za.ac.cput.repository.impl.IPaymentRepositoryImpl;
import za.ac.cput.service.IPaymentService;

import java.util.List;
@Service
public class IPaymentServiceImpl implements IPaymentService {
    private static IPaymentServiceImpl service;
    private static IPaymentRepositoryImpl repository;

    private IPaymentServiceImpl() {
        repository = IPaymentRepositoryImpl.getRepository();
    }

    public static IPaymentServiceImpl getPaymentService() {
        if (service == null) {
            service = new IPaymentServiceImpl();
        }
        return service;
    }

    @Override
    public Payment create(Payment payment) {
        Payment created = repository.create(payment);
        return created;
    }

    @Override
    public Payment read(Integer id) {
        Payment read = repository.read(id);
        return read;
    }

    @Override
    public Payment update(Payment payment) {
        Payment updated = repository.update(payment);
        return updated;
    }

    @Override
    public boolean delete(Integer id) {
        boolean success = repository.delete(id);
        return success;
    }

    @Override
    public List<Payment> getAllPayments() {
        return repository.getAllPayments();
    }

    @Override
    public Payment getPaymentById(Integer id) {
        Payment getById = repository.getPaymentById(id);
        return getById;
    }
}
