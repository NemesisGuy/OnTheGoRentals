package za.ac.cput.repository;
/**
 * IPaymentRepository.java
 * Interface for the Payment Repository
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Payment;

import java.util.List;

public interface IPaymentRepository extends JpaRepository<Payment, Integer> {
    public List<Payment> findAllByPaymentMethod(String paymentMethod);

}
