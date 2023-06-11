package za.ac.cput.repository;
/**
 * IPaymentRepository.java
 * Interface for the Payment Repository
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.impl.Payment;

import java.util.List;

public interface IPaymentRepository extends IRepository<Payment, Integer>{
    List<Payment> getAllPayments();
    Payment getPaymentById(Integer id);

}
