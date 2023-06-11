package za.ac.cput.domain;

/**
 * IAddress.java
 * Interface for the Address
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */
public interface IAddress extends IDomain {
    public String getStreetNumber();

    public String getStreetName();

    public String getSuburb();

    public String getCity();

    public String getState();

    public int getPostcode();
}