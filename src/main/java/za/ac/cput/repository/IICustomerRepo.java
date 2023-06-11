package za.ac.cput.repository;

import za.ac.cput.domain.impl.Customer;

import java.util.List;

public interface IICustomerRepo extends IRepository <Customer, String> {

    Customer getCustomerById(Integer id);

    List<Customer> getAllCustomer();
}
