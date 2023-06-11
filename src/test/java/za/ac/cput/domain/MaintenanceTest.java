package za.ac.cput.domain;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.Maintenance;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class MaintenanceTest {
    private int maintenanceId = 55236;
    private String maintenanceType = "Brake repair";
    private String serviceProvider = null;
    private LocalDate serviceDate = LocalDate.parse("2023-04-06");
    private Car id = null;

    @Test
    public void testMaintenance() {
        Maintenance maintenance = new Maintenance.Builder()
                .setMaintenanceId(maintenanceId)
                .setMaintenanceType(maintenanceType)
                .setServiceProvider(serviceProvider)
                .setServiceDate(serviceDate)
                .build();

        System.out.println(maintenance.toString());
    }

    @Test
    public void testObjectIdentity() {
        Maintenance maintenance = new Maintenance.Builder()
                .setMaintenanceId(102030)
                .setMaintenanceType("Brake Repair")
                .setServiceProvider(serviceProvider)
                .setServiceDate(LocalDate.parse("2023-04-07"))
                .build();

        Maintenance maintenance1 = new Maintenance.Builder()
                .setMaintenanceId(102030)
                .setMaintenanceType("Brake Repair")
                .setServiceProvider(serviceProvider)
                .setServiceDate(LocalDate.parse("2023-04-07"))
                .build();

        assertNotSame(maintenance, maintenance1);
    }

    @Test
    public void testInequality() {
        Maintenance maintenance = new Maintenance.Builder()
                .setMaintenanceId(123450)
                .setMaintenanceType("Brake Repair")
                .setServiceProvider(serviceProvider)
                .setServiceDate(LocalDate.parse("2023-04-08"))
                .build();

        Maintenance maintenance1 = new Maintenance.Builder()
                .setMaintenanceId(102030)
                .setMaintenanceType("Tire Replacement")
                .setServiceProvider(serviceProvider)
                .setServiceDate(LocalDate.parse("2023-04-07"))
                .build();

        assertNotEquals(maintenance, maintenance1);
    }
}