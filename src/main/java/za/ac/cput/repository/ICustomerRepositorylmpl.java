package za.ac.cput.repository;

import za.ac.cput.domain.Customer;

import java.util.ArrayList;
import java.util.List;

public class ICustomerRepositorylmpl implements IRepository<Customer, String> {


    private List<Customer> Customers;

    public ICustomerRepositorylmpl() {
        Customers = new ArrayList<>();
    }

    public static ICustomerRepositorylmpl getRepository() {
        return null;
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
