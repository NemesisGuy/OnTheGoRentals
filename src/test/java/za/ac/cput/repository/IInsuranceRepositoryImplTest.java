package za.ac.cput.repository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import za.ac.cput.domain.Insurance;
import za.ac.cput.factory.InsuranceFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class IInsuranceRepositoryImplTest {

    private static IInsuranceRepositoryImpl repository = IInsuranceRepositoryImpl.getRepository();
    private static InsuranceFactory repositoryFactory = new InsuranceFactory();
    private static Insurance insurance = repositoryFactory.create();
    private static Insurance insurance2;

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
                .setInsuranceType("Liability Insurance")
                .setInsuranceAmount(2000.0)
                .setInsuranceCoverageStartDate(LocalDate.parse("11-02-23", DateTimeFormatter.ofPattern("MM-dd-yy")))
                .setInsuranceCoverageEndDate(LocalDate.parse("12-05-23", DateTimeFormatter.ofPattern("MM-dd-yy")))
                .build();
        assertNotNull(repository.update(updated));
        System.out.println("Updated: " + updated);
    }

    @Test
    void d_getAllInsurancePolicies() {
        insurance2 = new InsuranceFactory().create();
        Insurance created = repository.create(insurance2);

        List<Insurance> list = repository.getAllInsurancePolicies();
        System.out.println("\nShow all: ");
        for (Insurance insurance : list) {
            System.out.println(insurance);
        }
        assertNotSame(insurance, insurance2);
    }

    @Test
    void e_getInsuranceById() {
        Insurance id = repository.getInsuranceById(insurance.getInsuranceId());
        System.out.println("\nSearch by Id: " + id);
        assertNotNull(id);
    }

    @Test
    void f_delete() {
        boolean success = repository.delete(insurance.getInsuranceId());
        assertTrue(success);
        System.out.println("Deleted: " + success);
    }
}