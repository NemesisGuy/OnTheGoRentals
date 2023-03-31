package za.ac.cput.domain;

import java.util.Date;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 */


public class Rental {


    //Declare the private variables
    private Customer borrower;
    private Employee issuer;
    private Date issuedDate;
    private Date dateReturned;
    private Employee receiver;
    private boolean finePaid;
    private Car car;


    //Initializing a parameterized constructor
    public Rental(Customer b, Car car, Employee iss, Date iDate, Date rDate, Employee rec, boolean fine) {

        this.borrower = b;
        this.car = car;
        this.issuer = iss;
        this.issuedDate = iDate;
        this.dateReturned = rDate;
        this.receiver = rec;
        this.finePaid = fine;

    }

    public Customer getBorrower() {
        return borrower;
    }

    public Car getCar() {
        return car;
    }

    public Employee getIssuer() {
        return issuer;
    }

    public Date getIssueDate() {
        return issuedDate;
    }

    public Date getDateReturned() {
        return dateReturned;
    }

    public Employee getReceiver() {
        return receiver;
    }

    public boolean getFineStatus() {
        return finePaid;
    }


    public void setBorrower() {

        this.borrower = borrower;

    }

    public void setBook(Car car) {
        this.car = car;
    }

    public void setIssuer(Employee issuer1) {
        this.issuer = issuer1;
    }

    public void setIssueDate(Date issueDate1) {
        this.issuedDate = issueDate1;
    }

    public void setDateReturned(Date dateReturned1) {
        this.dateReturned = dateReturned1;
    }

    public void setReceiver(Employee receiver) {
        this.receiver = receiver;
    }

    public void setFineStatus(boolean fineStatus) {
        this.finePaid = fineStatus;
    }
}

