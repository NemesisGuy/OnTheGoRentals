package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Maintenance;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceFactoryTest {
@Test
    void testMaintenanceFactory_pass(){
    MaintenanceFactory maintenanceFactory = new MaintenanceFactory();
    Maintenance maintenance = maintenanceFactory.create();

     assertNotNull(maintenance);
     assertNotNull(maintenance.getId());

}
}