package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Driver {
    @Id
    private int id;
    private String firstName;
    private String lastName;
    private String licenseCode;

    protected Driver(){}

    private Driver(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.licenseCode = builder.licenseCode;
    }

    public int getId() {
        return id;
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

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", licenseCode='" + licenseCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id && Objects.equals(firstName, driver.firstName) && Objects.equals(lastName, driver.lastName) && Objects.equals(licenseCode, driver.licenseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, licenseCode);
    }

    public static class Builder {
        private int id;
        private String firstName;
        private String lastName;
        private String licenseCode;

        public Builder setId(int id) {
            this.id = id;
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

        public Builder copy(Driver driver) {
            this.id = driver.id;
            this.firstName = driver.firstName;
            this.lastName = driver.lastName;
            this.licenseCode = driver.licenseCode;
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
