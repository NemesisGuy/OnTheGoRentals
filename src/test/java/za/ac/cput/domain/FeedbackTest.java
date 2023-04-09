package za.ac.cput.domain;
/**
 *    FeedbackTest.java
 *    Test for Feedback
 *    Author: Shamiso Moyo Chaka (220365393)
 *    Date: 1 April 2021
 */
import org.junit.jupiter.api.Test;

public class FeedbackTest {
    @Test
    void testFeedbackConstructor () {
        Feedback feedback1 = new Feedback.FeedbackBuilder()
                .setName("Shamiso")
                .setComment("This is a test!")
                .setId(2345)
                .build();

        Feedback feedback2 = new Feedback.FeedbackBuilder()
                .setName("Moyo")
                .setComment("Needs improvement.")
                .setId(2345)
                .build();

        System.out.println(feedback1.getComment());
        System.out.println(feedback1.getName());
        System.out.println(feedback1.getId());

        System.out.println("xxxxxxxxxxxxxx");

        System.out.println(feedback2.getComment());
        System.out.println(feedback2.getName());
        System.out.println(feedback2.getId());

    }

}
/**String name = "Shamiso";
 String comment = "This is a test for Feedback ";
 String id = "1234";



 Feedback feedback = new Feedback.builder()
 .name(name)
 .comment(comment)
 .id(id)
 .build();
 */

