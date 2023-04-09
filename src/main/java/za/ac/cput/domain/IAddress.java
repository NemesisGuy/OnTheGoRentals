package za.ac.cput.domain;

public interface IAddress extends IDomain {
    public String getStreetNumber();
    public String getStreetName();
    public String getSuburb();
    public String getCity();
    public String getState();
    public int getPostcode();
}