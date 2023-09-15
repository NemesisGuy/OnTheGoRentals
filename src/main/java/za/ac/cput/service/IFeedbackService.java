package za.ac.cput.service;
import za.ac.cput.domain.Feedback;
import java.util.List;

public interface IFeedbackService {

    Feedback create(Feedback feedback);

    Feedback read(Integer id);

    Feedback update(Feedback feedback);

    boolean delete(Integer id);

    List<Feedback> getAll();
}

