package za.ac.cput.domain;

import za.ac.cput.domain.impl.Customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerTest {

    public void testCustomer() {

        Customer customer = new Customer.CustomerBuilder()
                .build();

        customer.setCustomerId(Integer.parseInt("218331851"));
        customer.setName("Lonwabo Magazi");
        customer.setContactInfo("218331851@mycput.ac.za");
        customer.setHiringHistory("");

        System.out.println(customer.toString());


        assertEquals("218331851", customer.getCustomerId());

        assertEquals("Lonwabo Magazi", customer.getName());

        assertEquals("218331851@mycput.ac.za", customer.getContactInfo());

        assertEquals(" ", customer.getHiringHistory());
    }


}

