/* MaintenanceFactory.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.factory;

import za.ac.cput.domain.Maintenance;

import java.util.List;
import java.util.Random;

public class MaintenanceFactory implements IFactory<Maintenance>{

    public Maintenance create() {
        return Maintenance.builder()
                .maintenanceId(new Random().nextInt(1000000))
                .build();
    }


    public Maintenance getById(long id) {
        return null;
    }


    public Maintenance update(Maintenance entity) {
        return null;
    }


    public boolean delete(Maintenance entity) {
        return false;
    }


    public List<Maintenance> getAll() {
        return null;
    }


    public long count() {
        return 0;
    }


    public Class<Maintenance> getType() {
        return Maintenance.class;
    }
}

