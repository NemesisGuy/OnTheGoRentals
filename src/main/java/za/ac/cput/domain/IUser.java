package za.ac.cput.domain;

import za.ac.cput.domain.impl.User;

public interface IUser  extends IDomain{
    int getId();
    User getUser();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPhoneNumber();
    String getPassword();
    String getRole();
    boolean equals(Object o) ;
    public int hashCode();


}
