package za.ac.cput.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerTest {

    @Test
    public void testCustomer() {
        Customer customer = new Customer.CustomerBuilder()
                .setCustomerId(218331851)
                .setName("Lonwabo Magazi")
                .setContactInfo("218331851@mycput.ac.za")
                .setHiringHistory("")
                .build();

        System.out.println(customer.toString());

        assertEquals(218331851, customer.getCustomerId());
        assertEquals("Lonwabo Magazi", customer.getName());
        assertEquals("218331851@mycput.ac.za", customer.getContactInfo());
        assertEquals("", customer.getHiringHistory());
    }
}

