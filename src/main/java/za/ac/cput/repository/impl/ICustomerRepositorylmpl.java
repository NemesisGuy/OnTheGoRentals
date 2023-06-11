package za.ac.cput.repository.impl;

import za.ac.cput.domain.impl.Customer;
import za.ac.cput.repository.IRepository;

import java.util.ArrayList;
import java.util.List;

public class ICustomerRepositorylmpl implements IRepository<Customer, String> {


    private List<Customer> Customers;

    public ICustomerRepositorylmpl() {
        Customers = new ArrayList<>();
    }


    @Override
    public Customer create(Customer entity) {
        return null;
    }

    @Override
    public Customer read(String s) {
        return null;
    }

    @Override
    public Customer update(Customer entity) {
        return null;
    }

    @Override
    public boolean delete(String s) {
        return false;
    }
}
