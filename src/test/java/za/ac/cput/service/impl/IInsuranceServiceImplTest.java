package za.ac.cput.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.Insurance;
import za.ac.cput.factory.impl.InsuranceFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
class IInsuranceServiceImplTest {

    @Autowired
    private IInsuranceServiceImpl service;
    private static InsuranceFactory insuranceFactory = new InsuranceFactory();
    private static Insurance insurance = insuranceFactory.create();

    @Test
    void a_create() {
        Insurance add1 = service.create(insurance);
        System.out.println("Create : " + add1);
        Assertions.assertNotNull(add1);
    }

    @Test
    void b_read() {
        Insurance read = service.read(insurance.getId());
        System.out.println("Read: " + read);
        Assertions.assertNotNull(read);
    }

    @Test
    void c_update() {
        Insurance updated = new Insurance.Builder().copy(insurance)
                .setInsuranceType("Liability Insurance")
                .setInsuranceAmount(2000.0)
                .setInsuranceCoverageStartDate(LocalDate.parse("11-02-23", DateTimeFormatter.ofPattern("MM-dd-yy")))
                .setInsuranceCoverageEndDate(LocalDate.parse("12-05-23", DateTimeFormatter.ofPattern("MM-dd-yy")))
                .build();
        System.out.println("Updated: " + service.update(updated));
        Assertions.assertNotSame(updated, insurance);
    }

    @Test
    void findByType() {
        final String TYPE = "Liability Insurance";
        System.out.println("show all by Type: " + TYPE);

        List<Insurance> typeList = service.findAllByInsuranceType(TYPE);
        for (Insurance insurance: typeList) {
            System.out.println(insurance);
        }
    }

    @Test
    void e_getAllPayments() {
        System.out.println("Show all: ");
        System.out.println(service.getAllInsurancePolicies());
    }

    @Test
    void f_delete() {
        boolean success = service.delete(insurance.getId());
        Assertions.assertTrue(success);
        System.out.println("Success: " + success);
        System.out.println(service.getAllInsurancePolicies());
    }
}