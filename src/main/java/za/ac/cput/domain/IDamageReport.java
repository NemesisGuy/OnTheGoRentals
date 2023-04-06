package za.ac.cput.domain;

import za.ac.cput.scratch.Customer;
import za.ac.cput.scratch.Rental;

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
