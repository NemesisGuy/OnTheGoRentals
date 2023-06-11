package za.ac.cput.repository;
/**
 * IFeedbackRepo.java
 * Class for FeedbackRepoInterface
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 1 April 2023
 */
import za.ac.cput.domain.impl.Feedback;

import java.util.List;

public interface IFactoryRepository extends IFRepo <Feedback, String> {

    List<Feedback> getAllFeedbacks();

    List<Feedback> getFeedbackComment(String comment);

    Feedback getFeedbackId(String id);

}

