package za.ac.cput.repository;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Customer;
import za.ac.cput.factory.CustomerFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IDamageReportRepositoryImpTest {

    private static ICustomerRepositorylmpl repository = ICustomerRepositorylmpl.getRepository();

    private static CustomerFactory repositoryFactory = new CustomerFactory();
    private static Customer customer1 = repositoryFactory.create();
    private static Customer customer2;

    @Test
    public void test_create() {
        Customer created = repository.create(customer1);
        assertEquals(created.getId(), customer1.getId());
        System.out.println("Created: " + created);
    }

    @Test
    public void test_read() {
        Customer read = repository.read(customer1.getId());
        //Assertions.assertNull(read);
        System.out.println("Read: " + read);

    }
    @Test
    public void test_update() {
        Customer updated = new Customer.CustomerBuilder().copy(customer1)
        .setCustomerId(Integer.parseInt("218331851"));
        customer.setName();
        .setContactInfo("218331851@mycput.ac.za");
        .setHiringHistory("");
        assertNotNull(repository.update(updated));
        System.out.println("Updated: " + updated);
    }
    @Test
    public void test_delete(){
        boolean success = repository.delete(customer1.getId());
        //assertTrue(success);
        System.out.println("Deleted: " + success);
    }
}