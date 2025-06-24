package za.ac.cput.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
public class Driver {

    @jakarta.persistence.Id // This is the correct JPA annotation for the primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String licenseCode;
    @Column(nullable = false)
    private boolean deleted;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false)
    private LocalDateTime updatedAt;

    public Driver() {
    }

    private Driver(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.licenseCode = builder.licenseCode;
        this.deleted = builder.deleted;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;

    }

    public static Driver.Builder builder() {
        return new Driver.Builder();
    }

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();

        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        this.deleted = false; // Default value for deleted
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id && deleted == driver.deleted && Objects.equals(uuid, driver.uuid) && Objects.equals(firstName, driver.firstName) && Objects.equals(lastName, driver.lastName) && Objects.equals(licenseCode, driver.licenseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, firstName, lastName, licenseCode, deleted);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", licenseCode='" + licenseCode + '\'' +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }


    public static class Builder {
        private int id;
        private UUID uuid;
        private String firstName;
        private String lastName;
        private String licenseCode;
        private boolean deleted; // Good default
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
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

        public Builder setLicenseCode(String licenseCode) {
            this.licenseCode = licenseCode;
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

        public Builder copy(Driver driver) {
            this.id = driver.id;
            this.uuid = driver.uuid;
            this.firstName = driver.firstName;
            this.lastName = driver.lastName;
            this.licenseCode = driver.licenseCode;
            this.deleted = driver.deleted;
            this.createdAt = driver.createdAt;
            this.updatedAt = driver.updatedAt;
            return this;
        }

        public Driver build() {
            return new Driver(this);
        }


    }
}
