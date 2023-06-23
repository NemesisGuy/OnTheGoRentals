package za.ac.cput.domain;

import java.time.LocalDateTime;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * IRent.java
 */


public interface IRent extends IDomain {


    int getId();

    int getBorrower();



    int getIssuer();

    LocalDateTime getIssuedDate();

    LocalDateTime getReturnedDate();

    int getReceiver();

    boolean finePaid();



}