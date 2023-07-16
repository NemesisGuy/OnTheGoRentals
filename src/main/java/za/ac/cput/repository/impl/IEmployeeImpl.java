package za.ac.cput.repository.impl;
/**
 * IEmployeeImpl.java
 * Class for EmployeeInterfaceImplementation
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 1 April 2023
 */


import za.ac.cput.domain.Employee;
import za.ac.cput.repository.IEmployeeRepo;

import java.util.ArrayList;
import java.util.List;


public abstract class IEmployeeImpl implements IEmployeeRepo {

    private List<Employee> employees;

    public IEmployeeImpl() {
        employees = new ArrayList<>();
    }


    public Employee create(Employee employee) {
        employees.add(employee);
        return employee;
    }


    public Employee read(int id) {
        return employees.stream()
                .filter(employee -> employee.getClass().equals(id))
                .findFirst()
                .orElse(null);

    }


    public Object update(Employee employee) {
        Employee employeeToUpdate = read(employee.getId());

        if (employeeToUpdate != null) {
            employees.remove(employeeToUpdate);
            employees.add(employeeToUpdate);
            return employee;
        }
        return null;
    }


    public boolean delete(int id) {
        Employee employeeToDelete = read(id);

        if (employeeToDelete != null) {
            employees.remove(employeeToDelete);
            return true;
        }

        return false;
    }
}




