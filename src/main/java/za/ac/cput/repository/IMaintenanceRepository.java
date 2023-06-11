/* IMantenanceRepository.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository;

import za.ac.cput.domain.impl.Maintenance;

import java.util.List;

public interface IMaintenanceRepository extends IRepository<Maintenance, Integer> {
    List<Maintenance> getAll();

    public List<Maintenance> getAllMaintenance();

    Maintenance getMaintenanceById(Integer id);
}
