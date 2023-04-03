package za.ac.cput.domain;
/**
 *    EmployeeTest.java
 *    Test for employee
 *    Author: Shamiso Moyo Chaka (220365393)
 *    Date: 1 April 2021
 */
public class EmployeeTest {
    public static void main(String[] args) {
        Employee employee1 = new Employee.EmployeeBuilder()
                .employeeId("SM234")

                .email("shamiso.moyo@gmail.com")
                .phoneNumber("234-1234")
                .salary(30000.00)
                .build();

        System.out.println(employee1.getId());
        System.out.println(employee1.getFirstName());
        System.out.println(employee1.getLastName());
        System.out.println(employee1.getEmployeeId());
        System.out.println(employee1.getEmail());
        System.out.println(employee1.getPhoneNumber());
        System.out.println(employee1.getSalary());
    }
}
