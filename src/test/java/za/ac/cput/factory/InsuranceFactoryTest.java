package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Insurance;
import za.ac.cput.domain.Payment;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceFactoryTest {

    @Test
    void testInsuranceFactory_pass() {
        Insurance insurance = InsuranceFactory.createInsurance
                (
                        "Collision Damage Waiver",
                        42000.0,
                        LocalDate.parse("2022-01-01"),
                        LocalDate.parse("2023-12-31"),
                        null
                );

        System.out.println(insurance.toString());
        Assertions.assertNotNull(insurance);
    }

    @Test
    void testInsuranceFactory_fail() {
        Insurance insurance = InsuranceFactory.createInsurance
                (
                        "Collision Damage Waiver",
                        42000.0,
                        LocalDate.parse("2022-01-01"),
                        LocalDate.parse("12-31-2023"),
                        null
                );

        System.out.println(insurance.toString());
        Assertions.assertNotNull(insurance);
    }
}