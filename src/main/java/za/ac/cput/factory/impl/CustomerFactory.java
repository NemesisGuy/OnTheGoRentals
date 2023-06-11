package za.ac.cput.factory.impl;

import za.ac.cput.domain.impl.Customer;
import za.ac.cput.factory.IFactoryCustomer;

import java.util.List;
import java.util.Random;

public class CustomerFactory implements IFactoryCustomer<Customer> {
    @Override
    public Customer create() {
        return new Customer.CustomerBuilder()
                .setCustomerId(new Random().nextInt(1000000))
                .build();

    }

    @Override
    public Customer getById(long id) {
        return null;
    }

    @Override
    public Customer update(Customer entity) {
        return null;
    }

    @Override
    public boolean delete(Customer entity) {
        return false;
    }

    @Override
    public List<Customer> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Customer> getType() {
        return null;
    }
}
