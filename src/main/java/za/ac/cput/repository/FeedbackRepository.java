package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
