package za.ac.cput.domain;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 */

public interface IRent extends IDomain{

    int getId();
    String getBorrower();
    String getCar();
    String getIssuer();
    String getIssuedDate();

   String getDate();
    String getDateReturned();
    String getReceiver();
    boolean finePaid();



}