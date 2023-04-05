package az.ac.cput.domain;
/**
 * IInsurance.java
 * Interface for the Insurance
 * Author: Aqeel Hanslo (219374422)
 * Date: 04 April 2023
 */

import az.ac.cput.scratch.Rental;

import java.time.LocalDate;

public interface IInsurance {

    int getInsuranceId();

    String getInsuranceType();

    double getInsuranceAmount();

    LocalDate getInsuranceCoverageStartDate();

    LocalDate getInsuranceCoverageEndDate();

    Rental getRentalId();

}
