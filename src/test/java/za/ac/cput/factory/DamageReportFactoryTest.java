package za.ac.cput.factory;

/**
 * DamageReportFactoryTest.java
 * Class for the Damage Report Factory test
 * Author: Cwenga Dlova (214310671)
 * Date:  06 April 2023
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.DamageReport;
import za.ac.cput.factory.impl.DamageReportFactory;

class DamageReportFactoryTest {
    @Test
    public void testDamageReportFactory() {

        DamageReportFactory damageReportFactory = new DamageReportFactory();
        DamageReport damageReport = damageReportFactory.create();

        Assertions.assertNotNull(damageReport);
        Assertions.assertNotNull(damageReport.getId());

    }
}