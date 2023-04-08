package za.ac.cput.domain;
/**
 * IPayment.java
 * Interface for the Payment
 * Author: Aqeel Hanslo (219374422)
 * Date: 04 April 2023
 */



import java.time.LocalDate;

public interface IPayment extends IDomain {

    int getId();
    double getPaymentAmount();
    String getPaymentMethod();
    LocalDate getPaymentDate();
    Rental getRentalId();
}
