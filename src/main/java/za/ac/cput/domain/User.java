package za.ac.cput.domain;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonPOJOBuilder
public class User implements IUser {
    private String name;
    private String email;
    private String pictureUrl;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String role;

    public User() {
    }

    public User(String name, String email, String pictureUrl) {
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;

    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public User getUser() {
        User user = new User();
        return user;
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getRole() {
        return null;
    }
}
