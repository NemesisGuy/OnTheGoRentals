package za.ac.cput.repository;

import za.ac.cput.domain.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerRepositoryImpl implements CustomerRepository {

    private final Map<Integer, Customer> customers;

    public CustomerRepositoryImpl() {
        this.customers = new HashMap<>();
    }

    @Override
    public Customer create(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
        return customer;
    }

    @Override
    public Customer read(int customerId) {
        return customers.get(customerId);
    }

    @Override
    public Customer update(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
        return customer;
    }

    @Override
    public void delete(int customerId) {
        customers.remove(customerId);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }
}
