package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.entity.Faq;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * IFaqRepository.java
 * Interface for the IFaqRepository
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@Repository
public interface IFaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findByDeletedFalse();

    Optional<Faq> findByIdAndDeletedFalse(Integer integer);

    Optional<Faq> findByUuidAndDeletedFalse(UUID uuid);

    boolean existsByIdAndDeletedFalse(Integer faqId);
}
