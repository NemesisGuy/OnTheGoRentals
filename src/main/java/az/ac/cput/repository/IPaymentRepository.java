package az.ac.cput.repository;

import az.ac.cput.domain.Payment;

import java.util.List;

public interface IPaymentRepository extends IRepository<Payment, Integer>{
    List<Payment> getAllPayments();
    Payment getPaymentById(Integer id);

}
