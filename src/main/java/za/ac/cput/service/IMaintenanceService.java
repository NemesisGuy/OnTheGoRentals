package za.ac.cput.service;

import za.ac.cput.domain.Maintenance;

import java.util.Set;

public interface IMaintenanceService {
    Maintenance create(Maintenance maintenance);

    Maintenance read(Integer id);

    Maintenance update(Maintenance maintenance);

    boolean delete(Integer id);

    Set<Maintenance> getAll();
}
