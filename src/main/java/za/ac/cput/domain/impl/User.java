package za.ac.cput.domain.impl;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import za.ac.cput.domain.IUser;

@JsonPOJOBuilder
public class User implements IUser {
    private String userName;
    private String email;
    private String pictureUrl;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String role;

    public User() {
    }

    public User(String userName, String email, String pictureUrl) {
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getRole() {
        return this.role;
    }
}
