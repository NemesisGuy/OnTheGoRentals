package za.ac.cput.factory.impl;
/**
 * DamageReportFactory.java
 * Class for the Damage report factory
 * Author: Cwenga Dlova (214310671)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.Car;
import za.ac.cput.domain.Customer;
import za.ac.cput.domain.DamageReport;
import za.ac.cput.domain.Rental;
import za.ac.cput.factory.IFactory;

import java.time.LocalDate;
import java.util.Random;

public class DamageReportFactory implements IFactory<DamageReport> {

    @Override
    public DamageReport create() {
        return new DamageReport.Builder()
                .setId(new Random().nextInt(1000000))
                .setRentalId(null)
                .setCustomerId(null)
                .setCarId(new Car())
                .setReportDate(LocalDate.parse("2023-01-05"))
                .setDamageLocation("Cape Town")
                .setDescription("Drunk driving recklessness.")
                .build();
    }

    public DamageReport createDamageReport(Rental rentalId, Customer customerId, LocalDate reportDate, String damageLocation, String description) {

        return new DamageReport.Builder()
                .setId(new Random().nextInt())
                .setRentalId(rentalId)
                .setCustomerId(customerId)
                .setCarId(Car.builder().build())
                .setReportDate(reportDate)
                .setDamageLocation(damageLocation)
                .setDescription(description)
                .build();

    }
}
