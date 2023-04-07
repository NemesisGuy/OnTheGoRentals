/* IMantenanceRepository.java
 Entity for the Maintenance
 Author: Asiphe Funda (215092317)
 Date: 06 April 2023
*/
package za.ac.cput.repository;

import za.ac.cput.domain.Maintenance;

import java.util.List;

public interface IMaintenanceRepository extends IRepository <Maintenance, Integer>{
    public List<Maintenance> getAll();
    Maintenance getMaintenanceById(Integer id);
}
