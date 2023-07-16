package za.ac.cput.factory; /**
 * EmployeeFactoryInterface.java
 * Class for EmployeeInterfaceFactory
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 20 March 2023
 */

import za.ac.cput.domain.Employee;

import java.util.List;

public interface IFactoryEmployee<T> {
    T create();

    T getById(long id);

    T update(T entity);

    Employee update(Employee entity);

    boolean delete(T entity);

    boolean delete(Employee entity);

    List<T> getAll();

    long count();

    Class<T> getType();
}
