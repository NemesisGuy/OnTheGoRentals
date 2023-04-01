package za.ac.cput.repository;

import za.ac.cput.domain.Insurance;
import za.ac.cput.domain.Payment;

import java.util.List;

public interface IPaymentRepository extends IRepository<Payment, String>{
    List<Payment> getAllPayments();
    Payment getPaymentById(String id);

}
