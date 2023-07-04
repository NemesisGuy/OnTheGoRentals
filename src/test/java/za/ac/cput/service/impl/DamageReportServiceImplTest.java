package za.ac.cput.service.impl;

/**
 * DamageReportServiceTest.java
 * Class for the Damage Report service test
 * Author: Cwenga Dlova (214310671)
 * Date:  11 June 2023
 */

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.DamageReport;
import za.ac.cput.factory.impl.DamageReportFactory;
import za.ac.cput.repository.impl.DamageReportRepositoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageReportServiceImplTest {

    private static DamageReportRepositoryImpl repository = DamageReportRepositoryImpl.getRepository();
    private static DamageReportFactory repositoryFactory = new DamageReportFactory();
    private static DamageReport report1 = repositoryFactory.create();
    private static DamageReport report2;

    @Test
    public void test_1() {
        DamageReport created = repository.create(report1);
        assertEquals(created.getId(), report1.getId());
        System.out.println("Created: " + created);
    }

    @Test
    public void test_2() {
        boolean deleted = repository.delete(report1.getId());
        //assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }


}