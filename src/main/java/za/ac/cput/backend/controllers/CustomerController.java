package za.ac.cput.backend.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.Customer;

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
                .setName("Jay Olu")
                .setContactInfo("jay@gmail.com")
                .setHiringHistory("Book1")
                .build();

        Customer customer2 = new Customer.CustomerBuilder()
                .setCustomerId(2)
                .setName("Jaden Smith")
                .setContactInfo("jadensmith@gmail.com")
                .setHiringHistory("Book2")
                .build();

        Customer customer3 = new Customer.CustomerBuilder()
                .setCustomerId(3)
                .setName("Lonwabo Magazi")
                .setContactInfo("lonwabomagazi@gmail.com")
                .setHiringHistory("Book3")
                .build();

        Customer customer4 = new Customer.CustomerBuilder()
                .setCustomerId(4)
                .setName("Lilitha Zuma")
                .setContactInfo("lilithazuma@gmail.com")
                .setHiringHistory("Book4")
                .build();

        Customer customer5 = new Customer.CustomerBuilder()
                .setCustomerId(5)
                .setName("Gugu Tom")
                .setContactInfo("tomgugu@gmail.com")
                .setHiringHistory("Book55")
                .build();

        Customer customer6 = new Customer.CustomerBuilder()
                .setCustomerId(6)
                .setName("Samantha Taylor")
                .setContactInfo("samanthataylor@gmail.com")
                .setHiringHistory("History6")
                .build();

        Customer customer7 = new Customer.CustomerBuilder()
                .setCustomerId(7)
                .setName("Harry Thompson")
                .setContactInfo("harrythompson@gmail.com")
                .setHiringHistory("History7")
                .build();



        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        customers.add(customer4);
        customers.add(customer5);
        customers.add(customer6);
        customers.add(customer7);

        return customers;
    }

}