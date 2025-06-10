package za.ac.cput.factory.impl;

import za.ac.cput.domain.entity.Driver;
import za.ac.cput.factory.IFactory;


public class DriverFactory implements IFactory<Driver> {
    public static Driver createDriver(int id, String firstName, String lastName, String licenseCode) {
        return new Driver.Builder()
                .setId(id)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setLicenseCode(licenseCode)
                .build();

    }

    public Driver create() {
        return new Driver.Builder().build();
    }

}
