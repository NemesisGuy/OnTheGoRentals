package za.ac.cput.domain;


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
