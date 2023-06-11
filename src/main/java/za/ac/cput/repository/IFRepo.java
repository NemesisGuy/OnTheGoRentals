package za.ac.cput.repository;

/**
 * IFeedbackRepository.java
 * interface for the IFeedbackRepository
 * Author: Shamiso Moyo Chaka(220365393)
 * Date: 1 April2021
 */
public interface IFRepo<T, ID> {
    T create(T Feedback);

    T read(ID id);

    T update(T Feedback);

    boolean delete(ID id);
}

