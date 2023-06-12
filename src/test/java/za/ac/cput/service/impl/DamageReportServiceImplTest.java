package za.ac.cput.service.impl;

/**
 * DamageReportServiceTest.java
 * Class for the Damage Report service test
 * Author: Cwenga Dlova (214310671)
 * Date:  11 June 2023
 * 12 June 2023: changes made by Cwenga
 */

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.DamageReport;
import za.ac.cput.factory.impl.DamageReportFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DamageReportServiceImplTest {

    private static DamageReportServiceImpl service = DamageReportServiceImpl.getService();
    private static DamageReportFactory damageReportFactory = new DamageReportFactory();

    private static DamageReport report = damageReportFactory.createDamageReport(null, null, (LocalDate.parse("2023-05-20")), "Paarl", "Wheel puncture.");



    @Test
    public void test_1() {
        DamageReport created = service.create(report);
        assertEquals(created.getId(), report.getId());
        System.out.println("Created: " + created);
    }
    @Test
    public void test_2(){
        report = damageReportFactory.createDamageReport(null, null, (LocalDate.parse("2023-05-20")), "Paarl", "Wheel puncture.");
        boolean deleted = service.delete(report.getId());
        assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }

    /**@Test
    void test_3() {
        System.out.println("List All Reported Damages: ");
        System.out.println(service.getAll());
    }*/


}