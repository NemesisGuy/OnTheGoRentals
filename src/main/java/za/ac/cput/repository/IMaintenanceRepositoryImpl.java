package za.ac.cput.repository;

import za.ac.cput.domain.Car;
import za.ac.cput.domain.Maintenance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IMaintenanceRepositoryImpl implements IMaintenanceRepository{
    private  List<Maintenance> maintenance;

    public IMaintenanceRepositoryImpl(){
        maintenance = new ArrayList<>();

    }
    @Override
    public Maintenance create(Maintenance entity) {
        maintenance.add(entity);
        return entity;
    }

    @Override
    public Maintenance read(Integer id) {
        return maintenance.stream()
                .filter(maintenance -> maintenance.getId()==id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Maintenance update(Maintenance entity) {
        Maintenance maintenanceToUpdate = read(entity.getId());

        if (maintenanceToUpdate != null) {
            maintenance.remove(maintenanceToUpdate);
            maintenance.add(entity);
            return entity;
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {

        Maintenance maintenanceToDelete = read(id);

        if (maintenanceToDelete != null) {
            maintenance.remove(maintenanceToDelete);
            return true;
        }

        return false;
    }
    @Override
    public List<Maintenance> getAll() {
        return Collections.unmodifiableList(maintenance);
    }

    @Override
    public Maintenance getMaintenanceById(Integer id) {

        return read(id);
    }
}

