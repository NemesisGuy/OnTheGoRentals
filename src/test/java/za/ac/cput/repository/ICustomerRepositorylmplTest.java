package za.ac.cput.repository;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Customer;
import za.ac.cput.factory.CustomerFactory;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryImplTest {

    private static CustomerRepository repository = new CustomerRepositoryImpl();
    private static CustomerFactory customerFactory = new CustomerFactory();
    private static Customer customer1 = customerFactory.create();
    private static Customer customer2;

    @Test
    public void testCreate() {
        Customer created = repository.create(customer1);
        assertEquals(created.getCustomerId(), customer1.getCustomerId());
        System.out.println("Created: " + created);
    }

    @Test
    public void testRead() {
        repository.create(customer1); // Create the customer first
        Customer read = repository.read(customer1.getCustomerId());
        assertNotNull(read);
        System.out.println("Read: " + read);
    }

    @Test
    public void testUpdate() {
        repository.create(customer1); // Create the customer first
        customer1.setName("Updated Name");
        Customer updated = repository.update(customer1);
        assertNotNull(updated);
        System.out.println("Updated: " + updated);
    }

    @Test
    public void testDelete() {
        repository.create(customer1); // Create the customer first
        boolean success = repository.delete(customer1.getCustomerId());
        assertTrue(success);
        assertNull(repository.read(customer1.getCustomerId())); // Ensure the customer is deleted
        System.out.println("Deleted: " + success);
    }
}
