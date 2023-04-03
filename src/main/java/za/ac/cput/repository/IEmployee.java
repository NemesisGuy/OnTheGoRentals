/**
 * IEmployee.java
 * Class for EmployeeRepoInterface
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 20 March 2023
 */
package za.ac.cput.repository;
import za.ac.cput.domain.Employee;

import java.util.List;

public interface IEmployee extends IEmployeeRepo<Employee, String> {

    List<Employee> findAll();

    List<Employee> findByEmployeeID(String Employee_ID);

    Employee getEmployeeById(String id);
}
