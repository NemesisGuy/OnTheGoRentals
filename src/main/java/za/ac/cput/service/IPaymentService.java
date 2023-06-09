package za.ac.cput.service;

import za.ac.cput.domain.impl.Payment;

import java.util.List;

public interface IPaymentService extends IService<Payment, Integer> {
    List<Payment> getAllPayments();

    Payment getPaymentById(Integer id);
}
