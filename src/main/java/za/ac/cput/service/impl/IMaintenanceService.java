package za.ac.cput.service.impl;

import za.ac.cput.domain.impl.Maintenance;

import java.util.Set;

public interface IMaintenanceService {
    Maintenance create(Maintenance maintenance);
    boolean delete(Integer id);
    Set<Maintenance> getAll();
}
