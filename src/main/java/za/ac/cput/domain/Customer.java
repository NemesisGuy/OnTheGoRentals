package za.ac.cput.domain;

import java.util.Objects;

    /**
     * Lonwabo Magazi-218331851
     * Date: March 2023
     */

    public class Customer {
        private int Customer_Id;
        private String Name;
        private String ContactInfo;
        private String BorrowingHistory;

        public Customer() {
        }

        public Customer(String Name,
                        String ContactInfo,
                        String BorrowingHistory,
                        String referenceNumber) {
            this.Name = Name;
            this.ContactInfo = ContactInfo;
            this.BorrowingHistory = BorrowingHistory;
        }

        private Customer(Builder builder) {
            this.Customer_Id = Customer_Id;
            this.Name = Name;
            this.ContactInfo = ContactInfo;
            this.BorrowingHistory = BorrowingHistory;

        }

        public Customer(String borrowingHistory) {
            BorrowingHistory = borrowingHistory;
        }

        public Customer(String Name, String contactInfo) {
            this.Name = Name;
            ContactInfo = contactInfo;

        }
        public int getCustomer_Id() {
            return Customer_Id;
        }
        public String getBorrowingHistory() {
            return BorrowingHistory;
        }

        public void setBorrowingHistory(String borrowingHistory) {
            BorrowingHistory = borrowingHistory;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            this.Name = Name;
        }

        public String getContactInfo() {
            return ContactInfo;
        }

        public void setContactInfo(String contactInfo) {
            ContactInfo = contactInfo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Customer customer)) return false;
            return Name.equals(customer.Name) && ContactInfo.equals(customer.ContactInfo) && BorrowingHistory.equals(customer.BorrowingHistory);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Name, ContactInfo, BorrowingHistory);
        }

        public static class Builder {
            private String Name, ContactInfo, BorrowingHistory;
            private int Customer_Id;

            public Builder setName(String Name) {
                this.Name = Name;
                return this;
            }
            public Builder setCustomer_Id(int customer_Id) {
                this.Customer_Id = customer_Id;
                return this;
            }

            public Builder setContactInfo(String ContactInfo) {
                this.ContactInfo = ContactInfo;
                return this;
            }

            public Builder setBorrowingHistory(String BorrowingHistory) {
                this.BorrowingHistory = BorrowingHistory;
                return this;
            }
        }
    }
