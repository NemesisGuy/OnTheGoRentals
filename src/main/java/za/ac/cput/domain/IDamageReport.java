package za.ac.cput.domain;

import az.ac.cput.scratch.Customer;
import az.ac.cput.scratch.Rental;

import java.time.LocalDate;

public interface IDamageReport {
    String getId();
    Rental getRentalId();
    Customer getCustomerId();
    Car getCarId();
    LocalDate getReportDate();
    String getDamageLocation();
    String getDescription();
}
