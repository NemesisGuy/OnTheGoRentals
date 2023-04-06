package za.ac.cput.domain;
/**
 * DamageReportTest.java
 * Class for the Damage report test
 * Author: Cwenga Dlova (214310671)
 * Date:  01 April 2023
 */
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.scratch.Customer;
import za.ac.cput.scratch.Rental;
import java.time.LocalDate;

class DamageReportTest {

    private int id= 7512344;
    private Rental rentalId = null;
    private Customer customerId = null;
    private Car carId = null;
    private LocalDate reportDate = LocalDate.parse("2023-04-05");
    private String damageLocation = "Cape Town CBD.";
    private String description = "Accidentally pumbed into another car and smashed the windscreen.";

    @Test
    public void testDamageReport(){
        DamageReport report = new DamageReport.Builder()
                .setId(String.valueOf(id))
                .setRentalId(rentalId)
                .setCustomerId(customerId)
                .setCarId(carId)
                .setReportDate(reportDate)
                .setDamageLocation(damageLocation)
                .setDescription(description)
                .build();

        System.out.println(report.toString());
    }
    @Test
    public void testEquality() {
        DamageReport report2 = new DamageReport.Builder()
                .setId(String.valueOf(4565232))
                .setRentalId(rentalId)
                .setCustomerId(customerId)
                .setCarId(carId)
                .setReportDate(LocalDate.parse("2023-03-27"))
                .setDamageLocation(damageLocation)
                .setDescription("Car rolled on the high way")
                .build();

        DamageReport report3 = new DamageReport.Builder()
                .setId(String.valueOf(5556231))
                .setRentalId(rentalId)
                .setCustomerId(customerId)
                .setCarId(carId)
                .setReportDate(LocalDate.parse("2023-03-27"))
                .setDamageLocation("Khayelitsha")
                .setDescription("Car had wheel pucture.")
                .build();

        Assertions.assertNotEquals(report2, report3);
    }

}