package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Feedback;
import za.ac.cput.repository.FeedbackRepository;
import za.ac.cput.service.IFeedbackService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements IFeedbackService {
    private final FeedbackRepository repository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository repository) {

        this.repository = repository;

    }

    @Override
    public Feedback create(Feedback feedback) {
        return repository.save(feedback);
    }

    @Override
    public Feedback read(Integer id) {
        Optional<Feedback> optionalFeedback = repository.findByIdAndDeletedFalse(id);
        return optionalFeedback.orElse(null);
    }

    @Override
    public Feedback read(UUID uuid) {
        return repository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

    @Override
    public Feedback update(Feedback feedback) {
        if (this.repository.existsById(feedback.getId())) {
            System.out.println("update: true");
            return this.repository.save((feedback));
        }
        System.out.println("debug update: false");
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Feedback feedback = this.repository.findById(id).orElse(null);
        if (feedback != null && !feedback.isDeleted()) {
            feedback = new Feedback.Builder().copy(feedback).setDeleted(true).build();
            this.repository.save(feedback);
            return true;
        }

        return false;
    }

    @Override
    public List<Feedback> getAll() {
        List<Feedback> feedbackList = this.repository.findByDeletedFalse();
        return feedbackList;
    }
}
