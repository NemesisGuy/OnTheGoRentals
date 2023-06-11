package za.ac.cput.factory.impl;
/**
 * DamageReportFactory.java
 * Class for the Damage report factory
 * Author: Cwenga Dlova (214310671)
 * Date:  06 April 2023
 */
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.DamageReport;
import za.ac.cput.factory.IFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class DamageReportFactory implements IFactory<DamageReport> {

    @Override
    public DamageReport create() {
        return new DamageReport.Builder()
                .setId(new Random().nextInt(1000000))
                .setCarId(Car.builder().build())
                .setRentalId(null)
                .setCustomerId(null)
                .setReportDate(LocalDate.parse("2023-01-05"))
                .setDamageLocation("Cape Town")
                .setDescription("Drunk driving recklessness.")
                .build();
    }

}
