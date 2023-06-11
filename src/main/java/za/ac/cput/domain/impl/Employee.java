/**
 * Employee.java
 * POJO Class for Employee
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 20 March 2023
 */

package za.ac.cput.domain.impl;

import za.ac.cput.domain.IEmployee;

public class Employee implements IEmployee {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String employeeId;
    private final String email;
    private final String phoneNumber;
    private final double salary;

    // private constructor
    private Employee(EmployeeBuilder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.employeeId = builder.employeeId;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.salary = builder.salary;
    }


    // getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getSalary() {
        return salary;
    }

    // EmployeeBuilder class
    public static class EmployeeBuilder {
        private final int id;
        private final String firstName;
        private final String lastName;
        private String employeeId;
        private String email;
        private String phoneNumber;
        private double salary;

        // constructor
        public EmployeeBuilder() {
            this.id = build().id;
            this.firstName = build().firstName;
            this.lastName = build().lastName;
        }

        public EmployeeBuilder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public EmployeeBuilder email(String email) {
            this.email = email;
            return this;
        }

        public EmployeeBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public EmployeeBuilder salary(double salary) {
            this.salary = salary;
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }

        @Override
        public String toString() {
            return "EmployeeBuilder{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", employeeId='" + employeeId + '\'' +
                    ", email='" + email + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", salary=" + salary +
                    '}';
        }
    }
}

