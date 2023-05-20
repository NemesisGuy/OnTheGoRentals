package za.ac.cput.domain;

public class Admin extends User{

    boolean isAdmin = true;


    public Admin(String name, String email, String pictureUrl) {
        super(name, email, pictureUrl);
    }
}
