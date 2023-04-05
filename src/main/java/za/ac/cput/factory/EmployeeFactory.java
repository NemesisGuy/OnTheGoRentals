package za.ac.cput.factory;

import za.ac.cput.domain.Employee;

import java.util.List;
/**
 * EmployeeFactory.java
 * Class for Employee Factory
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 20 March 2023
 */

public class EmployeeFactory implements IFactoryEmployee<Employee> {


    @Override
    public Employee create() {
        return null;
    }

    @Override
    public Employee getById(long id) {
        return null;
    }

    @Override
    public Employee update(Employee entity) {
        return null;
    }

    @Override
    public boolean delete(Employee entity) {
        return false;
    }

    @Override
    public List<Employee> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Employee> getType() {
        return null;
    }
}
