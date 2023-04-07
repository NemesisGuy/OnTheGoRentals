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
    @Override
    public Maintenance create() {
        return Maintenance.builder()
                .id(new Random().nextInt(1000000))
                .build();
    }

    @Override
    public Maintenance getById(long id) {
        return null;
    }

    @Override
    public Maintenance update(Maintenance entity) {
        return null;
    }

    @Override
    public boolean delete(Maintenance entity) {
        return false;
    }

    @Override
    public List<Maintenance> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Maintenance> getType() {
        return Maintenance.class;
    }
}

