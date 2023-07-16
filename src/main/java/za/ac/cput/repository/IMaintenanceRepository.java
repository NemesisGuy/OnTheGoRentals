/* IMantenanceRepository.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository;

import za.ac.cput.domain.Maintenance;

import java.util.Set;

public interface IMaintenanceRepository extends IRepository<Maintenance, Integer> {
    Set<Maintenance> getAll();

    Maintenance getMaintenanceById(Integer id);
}
