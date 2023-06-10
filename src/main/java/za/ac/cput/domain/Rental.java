package za.ac.cput.domain;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * Rental Class.java
 */

public class Rental {

    private int rentalId;
    private String borrower;
    private String car;
    private String issuer;
    private String issuedDate;
    private String date;
    private String dateReturned;
    private String receiver;
    private boolean finePaid;

    public Rental(int rentalId, String borrower, String car, String issuer, String issuedDate, String date, String dateReturned, String receiver, boolean finePaid) {
        this.rentalId = rentalId;
        this.borrower = borrower;
        this.car = car;
        this.issuer = issuer;
        this.issuedDate = issuedDate;
        this.date = date;
        this.dateReturned = dateReturned;
        this.receiver = receiver;
        this.finePaid = finePaid;
    }

    public int getRentalId() {
        return rentalId;
    }

    public String getBorrower() {
        return borrower;
    }

    public String getCar() {
        return car;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public String getDate() {
        return date;
    }

    public String getDateReturned() {
        return dateReturned;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isFinePaid() {
        return finePaid;
    }

    public static class RentalBuilder {
        private int rentalId;
        private String borrower;
        private String car;
        private String issuer;
        private String issuedDate;
        private String date;
        private String dateReturned;
        private String receiver;
        private boolean finePaid;

        public RentalBuilder setRentalId(int rentalId) {
            this.rentalId = rentalId;
            return this;
        }

        public RentalBuilder setBorrower(String borrower) {
            this.borrower = borrower;
            return this;
        }

        public RentalBuilder setCar(String car) {
            this.car = car;
            return this;
        }

        public RentalBuilder setIssuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public RentalBuilder setIssuedDate(String issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public RentalBuilder setDate(String date) {
            this.date = date;
            return this;
        }

        public RentalBuilder setDateReturned(String dateReturned) {
            this.dateReturned = dateReturned;
            return this;
        }

        public RentalBuilder setReceiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public RentalBuilder setFinePaid(boolean finePaid) {
            this.finePaid = finePaid;
            return this;
        }

        public Rental build() {
            return new Rental(rentalId, borrower, car, issuer, issuedDate, date, dateReturned, receiver, finePaid);
        }
    }

    @Override
    public String toString() {
        return "Rental{" +
                "rentalId=" + rentalId +
                ", borrower='" + borrower + '\'' +
                ", car='" + car + '\'' +
                ", issuer='" + issuer + '\'' +
                ", issuedDate='" + issuedDate + '\'' +
                ", date='" + date + '\'' +
                ", dateReturned='" + dateReturned + '\'' +
                ", receiver='" + receiver + '\'' +
                ", finePaid=" + finePaid +
                '}';
    }
}
