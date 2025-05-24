package za.ac.cput.domain;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
public class Driver {
    @Id
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();
    private String firstName;
    private String lastName;
    private String licenseCode;
    private boolean deleted = false;
    @PrePersist
    protected  void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }

    public Driver() {
    }

    private Driver(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.licenseCode = builder.licenseCode;
        this.deleted = builder.deleted;
    }

    public int getId() {
        return id;
    }
    public UUID getUuid() {
        return uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public static Driver.Builder builder() {
        return new Driver.Builder();
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
                '}';
    }



    public static class Builder {
        private int id;
        private UUID uuid ;
        private String firstName;
        private String lastName;
        private String licenseCode;
        private boolean deleted = false;

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

        public Builder copy(Driver driver) {
            this.id = driver.id;
            this.uuid = driver.uuid;
            this.firstName = driver.firstName;
            this.lastName = driver.lastName;
            this.licenseCode = driver.licenseCode;
            this.deleted = driver.deleted;
            return this;
        }

        public Driver build() {
            return new Driver(this);
        }

        public Builder id(int nextInt) {
            return null;
        }
    }
}
