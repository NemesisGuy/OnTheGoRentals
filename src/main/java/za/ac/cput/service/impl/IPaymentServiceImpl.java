package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Insurance;
import za.ac.cput.domain.Payment;
import za.ac.cput.repository.IPaymentRepository;
import za.ac.cput.service.IPaymentService;

import java.util.List;

@Service
public class IPaymentServiceImpl implements IPaymentService {

    private IPaymentRepository repository;

    @Autowired
    private IPaymentServiceImpl(IPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment create(Payment payment) {
        return this.repository.save(payment);
    }

    @Override
    public Payment read(Integer id) {
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public Payment update(Payment payment) {
        if ( this.repository.existsById(payment.getId())) {
            return this.repository.save(payment);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Payment> getAllPayments() {
        return this.repository.findAll();
    }

    public List<Payment> findAllByPaymentMethod(String paymentMethod) {
        return this.repository.findAllByPaymentMethod(paymentMethod);
    }
}
