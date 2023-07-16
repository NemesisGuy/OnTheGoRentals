/* MaintenanceServiceImpl.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 19 March 2023

 */
package za.ac.cput.service.impl;

import za.ac.cput.domain.Maintenance;
import za.ac.cput.repository.impl.MaintenanceRepositoryImpl;
import za.ac.cput.service.IMaintenanceService;

import java.util.Set;

public class MaintenanceServiceImpl implements IMaintenanceService {
    private static MaintenanceServiceImpl service;
    private static MaintenanceRepositoryImpl repository;

    private MaintenanceServiceImpl() {
        repository = MaintenanceRepositoryImpl.getRepository();
    }

    public static MaintenanceServiceImpl getService() {
        if (service == null) {
            service = new MaintenanceServiceImpl();
        }
        return service;
    }

    @Override
    public Maintenance create(Maintenance maintenance) {
        Maintenance created = repository.create(maintenance);
        return created;
    }

    @Override
    public Maintenance read(Integer id) {
        Maintenance read = repository.read(id);
        return read;
    }

    @Override
    public Maintenance update(Maintenance maintenance) {
        Maintenance updated = repository.update(maintenance);
        return updated;
    }

    @Override
    public boolean delete(Integer id) {
        boolean success = repository.delete(id);
        return success;
    }

    @Override
    public Set<Maintenance> getAll() {
        return repository.getAll();
    }
}
