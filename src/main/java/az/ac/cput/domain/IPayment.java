package az.ac.cput.domain;
/**
 * IPayment.java
 * Interface for the Payment
 * Author: Aqeel Hanslo (219374422)
 * Date: 04 April 2023
 */

import az.ac.cput.scratch.Rental;

import java.time.LocalDate;

public interface IPayment {

    int getPaymentId();

    double getPaymentAmount();

    String getPaymentMethod();

    LocalDate getPaymentDate();

    Rental getRentalId();
}
