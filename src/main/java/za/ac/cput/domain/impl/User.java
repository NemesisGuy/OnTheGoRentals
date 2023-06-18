package za.ac.cput.domain.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import za.ac.cput.domain.IDomain;

import java.util.Objects;

@Entity
public class User implements IDomain{
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
    private String email;
    private String pictureUrl;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String role;

    public User() {
        // Default constructor
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public User(int id, String userName, String email, String pictureUrl) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(userName, user.userName) && Objects.equals(email, user.email) && Objects.equals(pictureUrl, user.pictureUrl) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(password, user.password) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, email, pictureUrl, firstName, lastName, phoneNumber, password, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private int id;
        private String userName;
        private String email;
        private String pictureUrl;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String password;
        private String role;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder pictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.userName = this.userName;
            user.email = this.email;
            user.pictureUrl = this.pictureUrl;
            user.firstName = this.firstName;
            user.lastName = this.lastName;
            user.phoneNumber = this.phoneNumber;
            user.password = this.password;
            user.role = this.role;
            return user;
        }

        public Builder copy(User user) {
            this.id = user.id;
            this.userName = user.userName;
            this.email = user.email;
            this.pictureUrl = user.pictureUrl;
            this.firstName = user.firstName;
            this.lastName = user.lastName;
            this.phoneNumber = user.phoneNumber;
            this.password = user.password;
            this.role = user.role;
            return this;
        }

    }
}
