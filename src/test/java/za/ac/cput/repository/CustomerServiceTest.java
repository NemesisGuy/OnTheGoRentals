package za.ac.cput.repository;

import org.junit.jupiter.api.Test;
import za.ac.cput.Service.CustomerRepositoryImpl;
import za.ac.cput.Service.CustomerService;
import za.ac.cput.domain.Customer;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceTest {

    @Test
    public void testCustomerService() {
        // Create a customer repository
        CustomerRepository customerRepository = new CustomerRepositoryImpl();

        // Create a customer service
        CustomerService customerService = new CustomerServiceImpl(customerRepository);

        // Create a customer
        Customer customer = new Customer.CustomerBuilder()
                .setCustomerId(1)
                .setName("Lonwabo")
                .setContactInfo("lonwabo@gmail.com")
                .setHiringHistory("Hiring history")
                .build();

        // Test create operation
        Customer createdCustomer = customerService.create(customer);
        assertNotNull(createdCustomer);
        assertEquals(1, customerService.getAllCustomers().size());

        // Test read operation
        Customer retrievedCustomer = customerService.read(createdCustomer.getCustomerId());
        assertNotNull(retrievedCustomer);
        assertEquals("Lonwabo", retrievedCustomer.getName());

        // Test update operation
        retrievedCustomer.setName("Jay");
        Customer updatedCustomer = customerService.update(retrievedCustomer);
        assertNotNull(updatedCustomer);
        assertEquals("Jay", updatedCustomer.getName());

        // Test delete operation
        customerService.delete(updatedCustomer.getCustomerId());
        assertNull(customerService.read(updatedCustomer.getCustomerId()));
        assertEquals(0, customerService.getAllCustomers().size());
    }
}

