package za.ac.cput.domain;
/**
 * IInsurance.java
 * Interface for the Insurance
 * Author: Aqeel Hanslo (219374422)
 * Date: 04 April 2023
 */



import java.time.LocalDate;

public interface IInsurance extends IDomain {

    int getId();

    String getInsuranceType();

    double getInsuranceAmount();

    LocalDate getInsuranceCoverageStartDate();

    LocalDate getInsuranceCoverageEndDate();

    Rental getRentalId();

}
