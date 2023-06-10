package za.ac.cput.repository;

import za.ac.cput.domain.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ICustomerRepositorylmpl implements ICustomerRepository {

    private final Map<Integer, Customer> customerDatabase = new HashMap<>();
    private int nextCustomerId = 1;

    @Override
    public Customer create(Customer customer) {
        customer.setCustomerId(nextCustomerId);
        customerDatabase.put(nextCustomerId, customer);
        nextCustomerId++;
        return customer;
    }

    @Override
    public Customer read(int customerId) {
        return customerDatabase.get(customerId);
    }

    @Override
    public Customer update(Customer customer) {
        if (customerDatabase.containsKey(customer.getCustomerId())) {
            customerDatabase.put(customer.getCustomerId(), customer);
            return customer;
        } else {
            throw new IllegalArgumentException("Customer does not exist");
        }
    }

    @Override
    public boolean delete(int customerId) {
        if (customerDatabase.containsKey(customerId)) {
            customerDatabase.remove(customerId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerDatabase.values());
    }
}
