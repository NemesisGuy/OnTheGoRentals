/* IMantenanceImpl.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository.impl;

import za.ac.cput.domain.impl.Maintenance;
import za.ac.cput.repository.IMaintenanceRepository;

import java.util.ArrayList;
import java.util.List;

public class IMaintenanceRepositoryImpl implements IMaintenanceRepository {
    private static IMaintenanceRepositoryImpl repository = null;
    private List<Maintenance> maintenanceDB;

    private IMaintenanceRepositoryImpl() {
        maintenanceDB = new ArrayList<>();
    }

    public static IMaintenanceRepositoryImpl getRepository() {
        if (repository == null) {
            repository = new IMaintenanceRepositoryImpl();

        }
        return IMaintenanceRepositoryImpl.getRepository();
    }


    @Override
    public Maintenance create(Maintenance entity) {
        maintenanceDB.add(entity);
        return entity;
    }

    @Override
    public Maintenance read(Integer id) {
        return maintenanceDB.stream()
                .filter(maintenance -> maintenance.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Maintenance update(Maintenance entity) {
        Maintenance maintenanceToUpdate = read(entity.getId());

        if (maintenanceToUpdate != null) {
            maintenanceDB.remove(maintenanceToUpdate);
            maintenanceDB.add(entity);
            return entity;
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {

        Maintenance maintenanceToDelete = read(id);

        if (maintenanceToDelete != null) {
            maintenanceDB.remove(maintenanceToDelete);
            return true;
        }

        return false;
    }

    @Override
    public List<Maintenance> getAll() {
        return null;
    }

    @Override
    public List<Maintenance> getAllMaintenance() {
        return null;
    }

    @Override
    public Maintenance getMaintenanceById(Integer id) {
        return null;
    }
}

