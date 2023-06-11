package za.ac.cput.factory;
/**
 * FeedbackInterfaceFactory.java
 * Class for FeedbackInterfaceFactory
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 20 March 2023
 */

import za.ac.cput.domain.impl.Feedback;

import java.util.List;

public interface IFactoryFeedback<T> {
    T create();

    T getById(long id);

    T update(T entity);

    Feedback update(Feedback entity);

    boolean delete(T entity);

    boolean delete(Feedback entity);

    List<T> getAll();

    long count();

    Class<T> getType();
}

