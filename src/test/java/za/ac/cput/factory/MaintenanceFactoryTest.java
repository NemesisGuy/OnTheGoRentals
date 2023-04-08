package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.Maintenance;

import java.time.LocalDate;

class MaintenanceFactoryTest {
    private int maintenanceId = 55236;
    private String maintenanceType = "Brake repair";
    private String serviceProvider = null;
    private LocalDate serviceDate = LocalDate.parse("2023-04-06");
    private Car id = null;
@Test
    void testMaintenance(){
    Maintenance maintenance= new Maintenance.Builder()
            .setMaintenanceId(maintenanceId)
            .setMaintenanceType(maintenanceType)
            .setServiceProvider(serviceProvider)
            .setServiceDate(serviceDate)
            .build();

    System.out.println(maintenance.toString());
}

}
