package za.ac.cput.factory;

import za.ac.cput.domain.Car;
import za.ac.cput.domain.Maintenance;

import java.util.List;
import java.util.Random;

public class MaintenanceFactory implements IFactory<Maintenance>{
    @Override
    public Maintenance create() {
        // implement logic to create a new Car object
        return Maintenance.builder()
                //generate random number placeholder for id, will probably be replaced by database auto-increment value later
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

