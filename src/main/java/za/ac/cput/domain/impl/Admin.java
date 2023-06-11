package za.ac.cput.domain.impl;

public class Admin extends User {

    boolean isAdmin = true;


    public Admin(String name, String email, String pictureUrl) {
        super(name, email, pictureUrl);
    }
}
