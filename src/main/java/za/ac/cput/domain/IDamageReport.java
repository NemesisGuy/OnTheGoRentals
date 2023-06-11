package za.ac.cput.domain;

/**
 * IDamageReport.java
 * Interface for the IDamageReport
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.Customer;
import za.ac.cput.domain.impl.Rental;

import java.time.LocalDate;

public interface IDamageReport extends IDomain {
    int getId();

    Rental getRentalId();

    Customer getCustomerId();

    Car getCarId();

    LocalDate getReportDate();

    String getDamageLocation();

    String getDescription();
}
