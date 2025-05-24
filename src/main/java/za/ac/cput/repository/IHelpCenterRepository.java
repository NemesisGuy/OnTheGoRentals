package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.HelpCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * IHelpCenterRepository.java
 * Interface for the IHelpCenterRepository
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@Repository
public interface IHelpCenterRepository extends JpaRepository<HelpCenter, Integer> {
    ArrayList<HelpCenter> findAllByCategory(String category);

    ArrayList<HelpCenter> findAllByCategoryAndDeletedFalse(String category);

    Optional<HelpCenter> findByIdAndDeletedFalse(Integer integer);

    List<HelpCenter>  findByDeletedFalse();

    List<HelpCenter> findByCategoryAndDeletedFalse(String category);

    Optional<HelpCenter> findByUuidAndDeletedFalse(UUID uuid);
}