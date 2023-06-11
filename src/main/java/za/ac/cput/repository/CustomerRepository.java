package za.ac.cput.repository;

import za.ac.cput.domain.Customer;

import java.util.List;

public interface CustomerRepository {

    Customer create(Customer customer);

    Customer read(int customerId);

    Customer update(Customer customer);

    boolean delete(int customerId);

    List<Customer> getAllCustomers();
}
