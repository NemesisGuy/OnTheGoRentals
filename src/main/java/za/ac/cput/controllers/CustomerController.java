package za.ac.cput.controllers;
/**
 *  CustomerController.java
 *  This is the controller for the Customer class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.impl.Customer;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    @GetMapping("/api/customers")
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

        Customer customer3 = new Customer.CustomerBuilder()
                .setCustomerId(3)
                .setName("Bob Johnson")
                .setContactInfo("bobjohnson@gmail.com")
                .setBorrowingHistory("History3")
                .build();

        Customer customer4 = new Customer.CustomerBuilder()
                .setCustomerId(4)
                .setName("Mary Williams")
                .setContactInfo("marywilliams@gmail.com")
                .setBorrowingHistory("History4")
                .build();

        Customer customer5 = new Customer.CustomerBuilder()
                .setCustomerId(5)
                .setName("Tom Davis")
                .setContactInfo("tomdavis@gmail.com")
                .setBorrowingHistory("History5")
                .build();

        Customer customer6 = new Customer.CustomerBuilder()
                .setCustomerId(6)
                .setName("Samantha Taylor")
                .setContactInfo("samanthataylor@gmail.com")
                .setBorrowingHistory("History6")
                .build();

        Customer customer7 = new Customer.CustomerBuilder()
                .setCustomerId(7)
                .setName("Harry Thompson")
                .setContactInfo("harrythompson@gmail.com")
                .setBorrowingHistory("History7")
                .build();

        Customer customer8 = new Customer.CustomerBuilder()
                .setCustomerId(8)
                .setName("Lucy Green")
                .setContactInfo("lucygreen@gmail.com")
                .setBorrowingHistory("History8")
                .build();

        Customer customer9 = new Customer.CustomerBuilder()
                .setCustomerId(9)
                .setName("David Brown")
                .setContactInfo("davidbrown@gmail.com")
                .setBorrowingHistory("History9")
                .build();

        Customer customer10 = new Customer.CustomerBuilder()
                .setCustomerId(10)
                .setName("Amy Lee")
                .setContactInfo("amylee@gmail.com")
                .setBorrowingHistory("History10")
                .build();

        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        customers.add(customer4);
        customers.add(customer5);
        customers.add(customer6);
        customers.add(customer7);
        customers.add(customer8);
        customers.add(customer9);
        customers.add(customer10);

        return customers;
    }

}