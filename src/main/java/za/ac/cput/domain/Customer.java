package za.ac.cput.domain;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * Customer.java
 */

public class Customer {

    private int customerId;
    private String name;
    private String contactInfo;
    private String hiringHistory;

    // constructor
    public Customer() {
    }

    // Parameterized constructor
    public Customer(int customerId, String name, String contactInfo, String hiringHistory) {
        this.customerId = customerId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.hiringHistory = hiringHistory;
    }

    // Getters and setters

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getHiringHistory() {
        return hiringHistory;
    }

    public void setHiringHistory(String hiringHistory) {
        this.hiringHistory = hiringHistory;
    }

    // toString method

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", hiringHistory='" + hiringHistory + '\'' +
                '}';
    }
}
