package za.ac.cput.domain;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * Customer.java
 */

public class Customer{
    private int CustomerId;
    private String Name;
    private String ContactInfo;
    private String HiringHistory;


    //Builder Class
    private Customer(CustomerBuilder builder) {

        this.CustomerId = builder.CustomerId;
        this.Name = builder.Name;
        this.ContactInfo = builder.ContactInfo;
        this.HiringHistory = builder.HiringHistory;

    }

    //Getters

    public int getCustomerId() {
        return CustomerId;
    }

    //Setters
    public void setCustomerId(int customerId) {
        this.CustomerId = CustomerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getContactInfo() {
        return ContactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.ContactInfo = contactInfo;
    }

    public String getHiringHistory() {
        return HiringHistory;
    }

    public void setHiringHistory(String hiringHistory) {
        this.HiringHistory = hiringHistory;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "CustomerId=" + CustomerId +
                "Name='" + Name + '\'' +
                ", ContactInfo=" + ContactInfo + '\'' +
                ", HiringHistory=" + HiringHistory + '\'' +
                '}';
    }


    public int getId() {
        return 0;
    }


    //Builder ClassS
    public static class CustomerBuilder {
        private int CustomerId;
        private String Name;
        private String ContactInfo;
        private String HiringHistory;

        public CustomerBuilder setCustomerId(int CustomerId) {
            this.CustomerId = this.CustomerId;
            return this;
        }

        public CustomerBuilder setName(String Name) {
            this.Name = Name;
            return this;
        }

        public Customer.CustomerBuilder setContactInfo(String ContactInfo) {
            this.ContactInfo = ContactInfo;
            return this;
        }

        public Customer.CustomerBuilder setBorrowingHistory(String BorrowingHistory) {
            this.HiringHistory = HiringHistory;
            return this;
        }

        public Customer build() {
            return new Customer(this);

        }

    }

}