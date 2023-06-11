package za.ac.cput.service.impl;

import za.ac.cput.domain.impl.Maintenance;
import za.ac.cput.repository.IMaintenanceRepository;
import za.ac.cput.repository.impl.IMaintenanceRepositoryImpl;

import java.util.Set;

public class MaintenanceServiceImpl implements IMaintenanceService {
    private static IMaintenanceService service = null;
    private IMaintenanceRepository repository =  null;
    private MaintenanceServiceImpl(){
        repository = IMaintenanceRepositoryImpl.getRepository();
    }
    public static IMaintenanceService getService(){
        if(service == null){
            service= new MaintenanceServiceImpl();
        }
        return service;
    }
    @Override
    public Maintenance create(Maintenance maintenance) {
        Maintenance created = repository.create(maintenance);
        return created;
    }

    @Override
    public boolean delete(Integer id) {
        boolean success = repository.delete(id);
        return success;
    }

    @Override
    public Set<Maintenance> getAll() {
        return (Set<Maintenance>) repository.getAll();
    }
}
