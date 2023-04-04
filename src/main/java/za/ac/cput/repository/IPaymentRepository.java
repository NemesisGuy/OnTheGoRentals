package za.ac.cput.repository;

import za.ac.cput.domain.Insurance;
import za.ac.cput.domain.Payment;

import java.util.List;

public interface IPaymentRepository extends IRepository<Payment, Integer>{
    List<Payment> getAllPayments();
    Payment getPaymentById(Integer id);

}
