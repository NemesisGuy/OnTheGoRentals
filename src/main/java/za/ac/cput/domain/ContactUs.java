package za.ac.cput.domain;
/**ContactUs.java
 * Domain Class for the Contact Us
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 * */
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;
@Entity
public class ContactUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;
    private String message;

    protected ContactUs() {
    }

    public ContactUs(Builder builder){

        this.id = builder.id;
        this.title = builder.title;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.subject = builder.subject;
        this.message = builder.message;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactUs contactUs = (ContactUs) o;
        return id == contactUs.id && Objects.equals(title, contactUs.title) && Objects.equals(firstName, contactUs.firstName) && Objects.equals(lastName, contactUs.lastName) && Objects.equals(email, contactUs.email) && Objects.equals(subject, contactUs.subject) && Objects.equals(message, contactUs.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, firstName, lastName, email, subject, message);
    }

    @Override
    public String toString() {
        return "ContactUs{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static class Builder {

        private int id;
        private String title;
        private String firstName;
        private String lastName;
        private String email;
        private String subject;
        private String message;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder copy(ContactUs contactUs){

            this.id = contactUs.id;
            this.title = contactUs.title;
            this.firstName = contactUs.firstName;;
            this.lastName = contactUs.lastName;
            this.email = contactUs.email;
            this.subject = contactUs.subject;
            this.message = contactUs.message;

            return this;
        }

        public ContactUs build(){
            return new ContactUs(this);
        }
    }
}
