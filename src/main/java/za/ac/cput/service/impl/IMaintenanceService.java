/* IMaintenanceService.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 10 April 2023

 */
package za.ac.cput.service.impl;

import za.ac.cput.domain.impl.Maintenance;

import java.util.Set;

public interface IMaintenanceService {
    Maintenance create(Maintenance maintenance);
    Maintenance read(Integer id);
    Maintenance update(Maintenance maintenance);
    boolean delete(Integer id);

    Set<Maintenance> getAll();
}
