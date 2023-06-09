package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Insurance;
import za.ac.cput.factory.impl.InsuranceFactory;

class InsuranceFactoryTest {

    @Test
    void testInsuranceFactory_pass() {
        InsuranceFactory insuranceFactory = new InsuranceFactory();
        Insurance insurance = insuranceFactory.create();

        Assertions.assertNotNull(insurance);
        Assertions.assertNotNull(insurance.getId());
    }

    @Test
    void testInsuranceFactory_fail() {
        InsuranceFactory insuranceFactory = new InsuranceFactory();
        Insurance insurance = insuranceFactory.create();

        // Assertions.assertNull(insurance);
        //  Assertions.assertNull(insurance.getId());
    }
}