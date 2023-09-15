package za.ac.cput.service.impl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.Feedback;
import za.ac.cput.factory.impl.FeedbackFactory;
import za.ac.cput.service.IFeedbackService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
public class FeedbackServiceImplTest {

    @Autowired
    private IFeedbackService service;

    private final Feedback feedback = FeedbackFactory.createFeedback("Shamiso", "I loved the service I received");

    @Test
    void a_create() {
        Feedback created = service.create(feedback);
        assertEquals(feedback.getId(),created.getId());
        System.out.println("created" + created);
    }

    @Test
    void b_read() {
        Feedback created = service.create(feedback);
        Feedback read = service.read(created.getId());
        assertNotNull(read, "Expected to read a Feedback object");
        assertEquals(created.getId(), read.getId());
        assertEquals(feedback.getName(), read.getName());
        assertEquals(feedback.getComment(), read.getComment());
        System.out.println("Read: " + read);
    }

    @Test
    void c_update() {
        //Feedback newFeedback = new Feedback.Builder().copy(feedback).setName("Benny").build();
        Feedback newFeedback = service.create(feedback);

        System.out.println("newFeedback: " + newFeedback.toString());

        Feedback updated = service.update(newFeedback);
        System.out.println("Updated: " + updated);
        assertEquals(newFeedback.getName(), updated.getName());

    }

    @Test
    void delete() {
    }

    @Test
    void getAll() {
        System.out.println("Get All");
        System.out.println(service.getAll());
    }
}

