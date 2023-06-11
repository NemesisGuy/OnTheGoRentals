package za.ac.cput.domain;

/**
 * IEmployee.java
 * Interface for the Employee
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

public interface IEmployee extends IDomain {
    public int getId();

    public String getFirstName();

    public String getLastName();

    public String getEmployeeId();

    public String getEmail();

    public String getPhoneNumber();

    public double getSalary();
}