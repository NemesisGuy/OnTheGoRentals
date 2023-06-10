package za.ac.cput.repository;

import za.ac.cput.domain.Customer;

import java.util.List;

public interface CustomerService {

        Customer create(Customer customer);

        Customer read(int customerId);

        Customer update(Customer customer);

        void delete(int customerId);

        List<Customer> getAllCustomers();
    }


