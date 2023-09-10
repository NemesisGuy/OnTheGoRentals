package za.ac.cput.factory.impl;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Driver;

import static org.junit.jupiter.api.Assertions.*;

class DriverFactoryTest {
    @Test
    public void test(){
        Driver driver = DriverFactory.createDriver("Cwenga","Dlova","10");
        System.out.println(driver.toString());
        assertNotNull(driver);

    }

}