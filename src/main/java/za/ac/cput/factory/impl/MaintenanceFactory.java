/* MaintenanceFactory.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.factory.impl;

import za.ac.cput.domain.impl.Maintenance;
import za.ac.cput.factory.IFactory;

import java.time.LocalDate;
import java.util.Random;

public class MaintenanceFactory implements IFactory<Maintenance> {

    public Maintenance create() {
        return new Maintenance.Builder()
                .setId(new Random().nextInt(1000000))
                .setMaintenanceType("Oil filter")
                .setServiceProvider("hippo")
                .setServiceDate(LocalDate.parse("2023-06-11"))
                .build();
    }
}

