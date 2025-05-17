package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.HelpCenter;

import java.util.ArrayList;

/**
 * IHelpCenterRepository.java
 * Interface for the IHelpCenterRepository
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@Repository
public interface IHelpCenterRepository extends JpaRepository<HelpCenter, Integer> {
    ArrayList<HelpCenter> findAllByCategory(String category);
}