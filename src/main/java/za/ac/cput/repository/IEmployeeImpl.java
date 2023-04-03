package za.ac.cput.repository;
/**
 * IEmployeeImpl.java
 * Class for EmployeeInterfaceImplementation
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 1 April 2023
 */


import za.ac.cput.domain.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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
            employees .remove(employeeToUpdate);
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



/**
 * private List<Employee> employees;
 *
 *
 *     public IEmployeeImpl() {
 *         employees = new ArrayList<>();
 *     }
 *
 *
 *     public Employee create(Employee employee) {
 *         employees.add(employee);
 *         return employee;
 *     }
 *     public Employee read(int id) {
 *         return employees.stream()
 *                 .filter(employee -> employee.getId().equals(id))
 *                 .findFirst()
 *                 .orElse(null);
 *     }
 *     public Employee update(Employee employee) {
 *         Employee employeeToUpdate = read(employees.id());
 *
 *         if (employeeToUpdate != null) {
 *             employees .remove(employeeToUpdate);
 *             employees.add(employeeToUpdate);
 *             return employee;
 *         }
 *         return null;
 * }
 *     public boolean delete(int id) {
 *         Employee employeeToDelete = read(id);
 *
 *         if (employeeToDelete != null) {
 *             employees.remove(employeeToDelete);
 *             return true;
 *         }
 *
 *         return false;
 *     }
 *
 *     public List<Employee> getAllEmployees () {
 *         return Collections.unmodifiableList(employees);
 *     }
 *
 *     public Employee getEmployeeById(int Employee_Id) {
 *         return read(int.valueOf(Employee_Id));
 *     }
 */


