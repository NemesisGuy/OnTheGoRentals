package za.ac.cput.domain.impl;

import za.ac.cput.domain.IRent;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * Rental Class.java
 */


public class Rental implements IRent {

    //Declare the private variables

    private int rentalId;
    private String borrower;
    private String car;
    private String issuer;
    private String issuedDate;

    private String Date;
    private String dateReturned;
    private String receiver;
    private boolean finePaid;


    //Initializing a parameterized constructor
    public Rental (int rentalId, String borrower, String car, String issuer, String issuedDate, String Date, String DateReturned, String receiver, boolean fine){

        this.rentalId = rentalId;
        this.borrower = borrower;
        this.car = car;
        this.issuer = issuer;
        this.issuedDate = issuedDate;
        this.Date = Date;
        this.dateReturned = DateReturned;
        this.receiver = receiver;
        this.finePaid = fine;
    }


    public int getRentalId()  {
        return this.rentalId;
    }

    public String getBorrower(String borrower) {
        return this.borrower;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getBorrower() {
        return null;
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

    @Override
    public String getDate() {
        return null;
    }


    public String getDateReturned() {
        return dateReturned;
    }


    public String getReceiver() {
        return receiver;
    }

    @Override
    public boolean finePaid() {
        return false;
    }

    public boolean isFinePaid() {
        return finePaid;
    }




    //Builder Class
    public static class RentalBuilder {

        private int rentalId;
        private String borrower;
        private String car;
        private String issuer;

        private  String Date;
        private String issuedDate;
        private String dateReturned;
        private String receiver;
        private boolean finePaid;


        public Rental.RentalBuilder setRentalId(int rentalId) {
            this.rentalId = rentalId;
            return this;
        }
        public Rental.RentalBuilder setBorrower(String borrower) {
            this.borrower = borrower;
            return this;
        }

        public Rental.RentalBuilder setCar(String car) {
            this.car = car;
            return this;
        }

        public Rental.RentalBuilder setIssuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Rental.RentalBuilder setDate(String Date) {
            this.Date = Date;
            return this;
        }

        public Rental.RentalBuilder setIssuedDate(String issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Rental.RentalBuilder setDateReturned(String dateReturned) {
            this.dateReturned = dateReturned;
            return this;
        }

        public Rental.RentalBuilder setReceiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public Rental.RentalBuilder setFinePaid(Boolean finePaid) {
            this.finePaid = finePaid;
            return this;
        }

        public Rental build(){
                return new Rental(this);
            }
        }

        private Rental(RentalBuilder builder) {

        this.rentalId = builder.rentalId;
        this.borrower = builder.borrower;
        this.car = builder.car;
        this.issuer =builder.issuer;
        this.Date = builder.Date;
        this.issuedDate = builder.dateReturned;
        this.dateReturned = builder.receiver;
        this.finePaid = builder.finePaid;

    }

    @Override
    public String toString() {
        return "Rental{" +
                "rentalId=" + rentalId +
                "borrower='" + borrower + '\'' +
                ", car ='" + car + '\'' +
                ", issuer='" + issuer + '\'' +
                ", issuedDate='" + issuedDate + '\'' +
                ", dateReturned='" + dateReturned + '\'' +
                ", receiver='" + receiver + '\'' +
                ", finePaid=" + finePaid +
                '}';
    }
}