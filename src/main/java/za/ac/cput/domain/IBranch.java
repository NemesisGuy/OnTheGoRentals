package za.ac.cput.domain;

/**
 * IBranch.java
 * Interface for the IBranch
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

public interface IBranch extends IDomain {

    int getBranchId();


    String getBranchName();



    Address getAddress();



    String getEmail();


}
