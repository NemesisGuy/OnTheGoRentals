package za.ac.cput.factory.impl;

import za.ac.cput.domain.Feedback;
import za.ac.cput.factory.IFactory;

public class FeedbackFactory implements IFactory<Feedback> {
    private static int lastGeneratedId = 0;

    public Feedback create() {
        int newId = lastGeneratedId + 1;
        lastGeneratedId = newId;
        return new Feedback.Builder().setId(newId).build();
    }

    public static Feedback createFeedback(String name, String comment) {
        int newId = lastGeneratedId + 1;
        lastGeneratedId = newId;
        return new Feedback.Builder()
                .setId(newId)
                .setName(name)
                .setComment(comment)
                .build();
    }
}
