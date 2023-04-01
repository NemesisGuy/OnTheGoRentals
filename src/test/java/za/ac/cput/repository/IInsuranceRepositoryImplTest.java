package za.ac.cput.repository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.Insurance;
import za.ac.cput.factory.InsuranceFactory;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class IInsuranceRepositoryImplTest {
    private static IInsuranceRepositoryImpl repository = IInsuranceRepositoryImpl.getRepository();
    private static Insurance insurance = InsuranceFactory.createInsurance
                    (
                            "Collision Damage Waiver",
                            42000.0,
                            LocalDate.parse("2022-01-01"),
                            LocalDate.parse("2023-12-31"),
                            null
                    );

    @Test
    void a_create() {
        Insurance created = repository.create(insurance);
        assertEquals(created.getInsuranceId(), insurance.getInsuranceId());
        System.out.println("Created: " + created);
    }

    @Test
    void b_read() {
        Insurance read = repository.read(insurance.getInsuranceId());
        assertNotNull(read);
        System.out.println("Read: " + read);
    }

    @Test
    void c_update() {
        Insurance updated = new Insurance.Builder().copy(insurance)
                .setInsuranceAmount(2000.0)
                .build();
        assertNotNull(repository.update(updated));
        System.out.println("Updated: " + updated);
    }

    @Test
    void d_getAllInsurancePolicies() {
        System.out.println("Show all: " + repository.getAllInsurancePolicies());
    }

    @Test
    void e_getInsuranceById() {
        Insurance id = repository.getInsuranceById(insurance.getInsuranceId());
        System.out.println("Search by Id: " + id);
    }

    @Test
    void f_delete() {
        boolean success = repository.delete(insurance.getInsuranceId());
        assertTrue(success);
        System.out.println("Deleted: " + success);
    }
}