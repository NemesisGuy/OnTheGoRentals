package za.ac.cput.domain.entity;
/**
 * ContactUs.java
 * Domain Class for the Contact Us
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 */

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
public class ContactUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String message;
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt; // Added for tracking submission time
    @Column(updatable = false)
    private LocalDateTime updatedAt; // Added for tracking updates, if needed
    @Column(nullable = false)
    private boolean deleted = false;

    protected ContactUs() {
    }

    public ContactUs(Builder builder) {

        this.id = builder.id;
        this.uuid = builder.uuid;
        this.title = builder.title;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.subject = builder.subject;
        this.message = builder.message;
        this.createdAt = builder.createdAt; // Ensure createdAt is set
        this.updatedAt = builder.updatedAt; // Ensure updatedAt is set
        this.deleted = builder.deleted;

    }

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) this.uuid = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
        this.deleted = false; // Default
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ContactUs contactUs = (ContactUs) o;
        return id == contactUs.id && deleted == contactUs.deleted && Objects.equals(uuid, contactUs.uuid) && Objects.equals(title, contactUs.title) && Objects.equals(firstName, contactUs.firstName) && Objects.equals(lastName, contactUs.lastName) && Objects.equals(email, contactUs.email) && Objects.equals(subject, contactUs.subject) && Objects.equals(message, contactUs.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, title, firstName, lastName, email, subject, message, deleted);
    }

    @Override
    public String toString() {
        return "ContactUs{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deleted=" + deleted +
                '}';
    }

    public static class Builder {

        private int id;
        private UUID uuid;
        private String title;
        private String firstName;
        private String lastName;
        private String email;
        private String subject;
        private String message;
        private LocalDateTime createdAt; // Added for tracking submission time
        private LocalDateTime updatedAt; // Added for tracking updates, if needed
        private boolean deleted;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
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

        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder copy(ContactUs contactUs) {

            this.id = contactUs.id;
            this.uuid = contactUs.uuid;
            this.title = contactUs.title;
            this.firstName = contactUs.firstName;
            this.lastName = contactUs.lastName;
            this.email = contactUs.email;
            this.subject = contactUs.subject;
            this.message = contactUs.message;
            this.createdAt = contactUs.createdAt;
            this.updatedAt = contactUs.updatedAt;
            this.deleted = contactUs.deleted;

            return this;
        }

        public ContactUs build() {
            return new ContactUs(this);
        }
    }
}
