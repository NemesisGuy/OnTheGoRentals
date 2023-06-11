package za.ac.cput.factory;


import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Customer;
import za.ac.cput.factory.impl.CustomerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * CustomerFactoryTest Class.java
 */


public class CustomerFactoryTest {
    @Test
    void testRentalFactory_pass() {
        CustomerFactory customerFactory = new CustomerFactory();
        Customer customer = customerFactory.create();

        assertNotNull(customer);
        assertNotNull(customer.getCustomerId());
    }

    @Test
    void testPaymentFactory_fail() {
        CustomerFactory customerFactory = new CustomerFactory();
        Customer customer = customerFactory.create();

        //assertNull(customer);
        //assertNull(customer.getCustomerId());
    }

}