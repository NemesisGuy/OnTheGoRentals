package za.ac.cput.factory;

import za.ac.cput.domain.impl.Customer;

import java.util.List;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 */

public interface IFactoryCustomer<T> extends IFactory<Customer>{
    abstract Customer create();

    Customer getById(long id);

    Customer update(Customer entity);

    boolean delete(Customer entity);

    abstract List<Customer> getAll();

    abstract long count();

    Class<Customer> getType();
}
