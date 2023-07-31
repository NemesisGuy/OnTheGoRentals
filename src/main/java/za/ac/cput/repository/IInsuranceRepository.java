package za.ac.cput.repository;
/**
 * IInsuranceRepository.java
 * Interface for the Insurance Repository
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Insurance;

import java.util.List;

public interface IInsuranceRepository extends JpaRepository<Insurance, Integer> {
    public List<Insurance> findAllByInsuranceType(String insuranceType);

}
