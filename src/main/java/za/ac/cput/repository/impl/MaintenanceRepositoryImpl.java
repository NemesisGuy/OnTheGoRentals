/* IMantenanceImpl.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository.impl;

import za.ac.cput.domain.Maintenance;
import za.ac.cput.repository.IMaintenanceRepository;

import java.util.HashSet;
import java.util.Set;

public class MaintenanceRepositoryImpl implements IMaintenanceRepository {
    private static MaintenanceRepositoryImpl repository = null;
    private Set<Maintenance> maintenanceDB = null;

    private MaintenanceRepositoryImpl() {
        maintenanceDB = new HashSet<Maintenance>();
    }

    public static MaintenanceRepositoryImpl getRepository() {
        if (repository == null) {
            repository = new MaintenanceRepositoryImpl();

        }
        return repository;
    }


    @Override
    public Maintenance create(Maintenance maintenance) {
        boolean success = maintenanceDB.add(maintenance);
        if (!success)
            return null;
        return maintenance;
    }

    @Override
    public Maintenance read(Integer id) {
        for (Maintenance m : maintenanceDB) {
            if (m.getId() == (id)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public Maintenance update(Maintenance maintenance) {
        Maintenance maintenanceOriginal = read(maintenance.getId());
        if (maintenanceOriginal == null)
            return null;

        boolean successDelete = maintenanceDB.remove(maintenanceOriginal);
        if (!successDelete)
            return null;

        boolean successAdd = maintenanceDB.add(maintenance);
        if (!successAdd)
            return null;
        return maintenance;
    }

    @Override
    public boolean delete(Integer id) {

        Maintenance maintenanceToDelete = read(id);

        if (maintenanceToDelete == null)
            return false;
        maintenanceDB.remove(maintenanceToDelete);
        return true;

    }

    @Override
    public Set<Maintenance> getAll() {
        return maintenanceDB;
    }

    @Override
    public Maintenance getMaintenanceById(Integer id) {
        return read(id);
    }
}

