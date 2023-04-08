package za.ac.cput.backend.controllers;
/**
 *  CustomerController.java
 *  This is the controller for the Customer class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.Customer;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomerController {

    @GetMapping("/customers")
    public List<Customer> customers() {
        List<Customer> customers = new ArrayList<>();

        Customer customer1 = new Customer.CustomerBuilder()
                .setCustomerId(1)
                .setName("John Doe")
                .setContactInfo("johndoe@gmail.com")
                .setBorrowingHistory("History1")
                .build();

        Customer customer2 = new Customer.CustomerBuilder()
                .setCustomerId(2)
                .setName("Jane Smith")
                .setContactInfo("janesmith@gmail.com")
                .setBorrowingHistory("History2")
                .build();

        customers.add(customer1);
        customers.add(customer2);

        return customers;
    }
}
