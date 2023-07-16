package za.ac.cput.factory.impl;

import za.ac.cput.domain.Feedback;
import za.ac.cput.factory.IFactoryFeedback;

import java.util.List;

public class FeedbackFactory implements IFactoryFeedback {


    @Override
    public Object create() {
        return null;
    }

    @Override
    public Object getById(long id) {
        return null;
    }

    @Override
    public Object update(Object entity) {
        return null;
    }

    @Override
    public Feedback update(Feedback entity) {
        return null;
    }

    @Override
    public boolean delete(Object entity) {
        return false;
    }

    @Override
    public boolean delete(Feedback entity) {
        return false;
    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class getType() {
        return null;
    }
}

