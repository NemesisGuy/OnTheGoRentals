package za.ac.cput.domain;
public interface IEmployee  extends IDomain{
    public int getId();
    public String getFirstName();
    public String getLastName();
    public String getEmployeeId();
    public String getEmail();
    public String getPhoneNumber();
    public double getSalary();
}