package za.ac.cput.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.impl.Maintenance;
import za.ac.cput.factory.impl.MaintenanceFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
class MaintenanceServiceImplTest {
    private static MaintenanceServiceImpl service = MaintenanceServiceImpl.getService();
    private static MaintenanceFactory maintenanceFactory = new MaintenanceFactory();
    private static Maintenance maintenance = maintenanceFactory.createMaintenance("Oil filter", "hippo", LocalDate.parse("2023-05-01"));

    @Test
    void a_create() {
        Maintenance created = service.create(maintenance);
        System.out.println("Created: " + created);
        assertNotNull(created);
    }

    @Test
    void b_read() {
        Maintenance read = service.read(maintenance.getId());
        System.out.println("Read: " + read);
        Assertions.assertNotNull(read);
    }

    @Test
    void c_update() {
        Maintenance updated = new Maintenance.Builder()
                .copy(maintenance)
                .setMaintenanceType("(updated)")
                .setServiceProvider("(updated)")
                .setServiceDate(LocalDate.parse("2023-05-01"))
                .build();
        System.out.println("Updated: " + service.update(updated));
        Assertions.assertNotSame(updated, maintenance);
    }

    @Test
    void d_delete() {
        Integer id = maintenance.getId();
        boolean success = service.delete(id);
        assertTrue(success);
        System.out.println(success);
    }

    @Test
    void e_getAll() {
        System.out.println("Show all: ");
        System.out.println(service.getAll());
    }
}