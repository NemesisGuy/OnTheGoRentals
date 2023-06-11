package za.ac.cput.repository;
/**
 * IDamageReportImpTest.java
 * Class for the Damage report repository test
 * Author: Cwenga Dlova (214310671)
 * Date:  07 April 2023
 */
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.DamageReport;
import za.ac.cput.factory.impl.DamageReportFactory;
import za.ac.cput.repository.impl.IDamageReportRepositoryImp;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IDamageReportRepositoryImpTest {

    private static IDamageReportRepositoryImp repository = IDamageReportRepositoryImp.getRepository();

    private static DamageReportFactory repositoryFactory = new DamageReportFactory();
    private static DamageReport report1 = repositoryFactory.create();
    private static DamageReport report2;

    @Test
    public void test_create() {
        DamageReport created = repository.create(report1);
        assertEquals(created.getId(), report1.getId());
        System.out.println("Created: " + created);
    }

    @Test
    public void test_read() {
        DamageReport read = repository.read(report1.getId());
        //Assertions.assertNull(read);
        System.out.println("Read: " + read);

    }
    @Test
    public void test_update() {
        DamageReport updated = new DamageReport.Builder().copy(report1)
                .setReportDate(LocalDate.parse("2023-05-20"))
                .setDamageLocation("Khayelitsha")
                .setDescription("Smashed side mirror.")
                .build();
        assertNotNull(repository.update(updated));
        System.out.println("Updated: " + updated);
    }
    @Test
    public void test_delete(){
        boolean success = repository.delete(report1.getId());
        //assertTrue(success);
        System.out.println("Deleted: " + success);
    }
}