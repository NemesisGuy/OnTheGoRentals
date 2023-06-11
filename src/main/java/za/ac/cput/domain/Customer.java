package za.ac.cput.domain;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * Customer.java
 */

public class Customer implements IDomain {
    private int customerId;
    private String name;
    private String contactInfo;
    private String hiringHistory;

    public Customer(int i, String lonwabo, String s, String hiringHistory) {
    }

    private Customer(CustomerBuilder builder) {
        this.customerId = builder.customerId;
        this.name = builder.name;
        this.contactInfo = builder.contactInfo;
        this.hiringHistory = builder.hiringHistory;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getHiringHistory() {
        return hiringHistory;
    }

    @Override
    public int getId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", hiringHistory='" + hiringHistory + '\'' +
                '}';
    }

    public void setName(String updatedName) {
    }

    public static class CustomerBuilder {
        private int customerId;
        private String name;
        private String contactInfo;
        private String hiringHistory;

        public CustomerBuilder setCustomerId(int customerId) {
            this.customerId = customerId;
            return this;
        }

        public CustomerBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public CustomerBuilder setContactInfo(String contactInfo) {
            this.contactInfo = contactInfo;
            return this;
        }

        public CustomerBuilder setHiringHistory(String hiringHistory) {
            this.hiringHistory = hiringHistory;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}