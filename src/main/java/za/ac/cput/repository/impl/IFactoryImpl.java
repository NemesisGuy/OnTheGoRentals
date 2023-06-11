package za.ac.cput.repository.impl;
/**
 * IFeedback Interface implementation.java
 * Class for FeedbackRepoImplementation
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 1 April 2023
 */

import za.ac.cput.domain.impl.Feedback;
import za.ac.cput.repository.IFactoryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IFactoryImpl implements IFactoryRepository {

    private List<Feedback> feedbacks;

    public IFactoryImpl() {
        feedbacks = new ArrayList<>();
    }


    @Override
    public Feedback create(Feedback Feedback) {
        feedbacks.add(Feedback);
        return Feedback;
    }

    @Override
    public Feedback read(String comment) {
        return feedbacks.stream()
                .filter(feedback -> feedback.getClass().equals(comment))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Feedback update(Feedback Feedback) {
        Feedback feedbackToUpdate = read(Feedback.getComment());

        if (feedbackToUpdate != null) {
            feedbacks.remove(feedbackToUpdate);
            feedbacks.add(Feedback);
            return Feedback;
        }

        return null;
    }


    @Override
    public boolean delete(String comment) {
        Feedback feedbackToDelete = read(comment);

        if (feedbackToDelete != null) {
            feedbacks.remove(feedbackToDelete);
            return true;
        }
        return false;
    }

    @Override
    public List<Feedback> getAllFeedbacks() {

        return Collections.unmodifiableList(feedbacks);
    }

    @Override
    public List<Feedback> getFeedbackComment(String comment) {
        return feedbacks.stream()
                .filter(feedback -> feedback.getComment().equalsIgnoreCase(comment))
                .collect(Collectors.toList());
    }

    @Override
    public Feedback getFeedbackId(String id) {
        return null;
    }
}
