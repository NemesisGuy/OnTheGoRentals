package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Faq;

/**
 * IFaqRepository.java
 * Interface for the IFaqRepository
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@Repository
public interface IFaqRepository extends JpaRepository<Faq, Integer> {
}
