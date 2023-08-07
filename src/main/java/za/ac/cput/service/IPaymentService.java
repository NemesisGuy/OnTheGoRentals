package za.ac.cput.service;

import za.ac.cput.domain.Insurance;
import za.ac.cput.domain.Payment;

import java.util.List;

public interface IPaymentService extends IService<Payment, Integer> {
    List<Payment> getAllPayments();
}
