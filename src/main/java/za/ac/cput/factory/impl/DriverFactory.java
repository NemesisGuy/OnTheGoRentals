package za.ac.cput.factory.impl;

import za.ac.cput.domain.Driver;
import za.ac.cput.domain.User;
import za.ac.cput.factory.IFactory;

import java.util.Random;

public class DriverFactory implements IFactory<Driver> {
    public Driver create(){
        return new Driver.Builder().build();
    }
    public static Driver createDriver(String firstName, String lastName, String licenseCode){
        return new Driver.Builder()
                .setId(new Random().nextInt(1000000))
                .setFirstName(firstName)
                .setLastName(lastName)
                .setLicenseCode(licenseCode)
                .build();
    }

    //@Override
    //public Driver create() {
       // return new Driver.Builder()
               // .setId(new Random().nextInt(1000000))
               // .setFirstName("Lonwabo")
              //  .setLastName("Magazi")
               // .setLicenseCode("10")
                //.build();
    //}
}
