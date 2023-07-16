package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Maintenance;
import za.ac.cput.factory.impl.MaintenanceFactory;

class MaintenanceFactoryTest {
    @Test
    void testMaintenance() {

        MaintenanceFactory maintenanceFactory = new MaintenanceFactory();
        Maintenance maintenance = maintenanceFactory.create();

        Assertions.assertNotNull(maintenance);
        Assertions.assertNotNull(maintenance.getId());

    }

}
