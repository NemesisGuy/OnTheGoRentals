package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Feedback;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    Optional<Feedback> findByIdAndDeletedFalse(Integer id);

    List<Feedback> findByDeletedFalse();


    Optional<Feedback> findByUuidAndDeletedFalse(UUID uuid);
}
