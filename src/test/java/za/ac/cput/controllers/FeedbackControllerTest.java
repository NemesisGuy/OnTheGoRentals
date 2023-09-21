package za.ac.cput.controllers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import za.ac.cput.domain.Feedback;
import za.ac.cput.factory.impl.FeedbackFactory;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FeedbackControllerTest {
    private static Feedback feedback = FeedbackFactory.createFeedback("Linda", " I received a car in a very good condition");
    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseURL = "http://localhost:8080/feedback";
    @Test
    void a_create() {
        String url = baseURL + "/create";
        ResponseEntity<Feedback> postResponse = restTemplate.postForEntity(url, feedback, Feedback.class);
        assertNotNull(postResponse);
        assertNotNull(postResponse.getBody());
        ///assertEquals(postResponse.getStatusCode(), HttpStatus.OK;
        Feedback savedFeedbackk = postResponse.getBody();
        System.out.println("Saved data: " + savedFeedbackk);
        assertEquals(feedback.getId(), savedFeedbackk.getId());

    }

    @Test
    void b_read() {
        ResponseEntity<Feedback> responseEntity = restTemplate.getForEntity(baseURL + "/read/" + feedback.getId(), Feedback.class);


        if (responseEntity.getBody() != null) {
            Feedback retrievedFeedback = responseEntity.getBody();
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());


        } else {
            fail("Response body is null");
        }
    }



    @Test
    void c_updated() {
        Feedback updated = new Feedback.Builder().copy(feedback).setName("Candice").setComment("Excellent Services").build();
        String url = baseURL +"/update/";
        System.out.println("URL: "+url);
        System.out.println("Post data: " +updated);
        ResponseEntity<Feedback>response = restTemplate.postForEntity(url,updated,Feedback.class);
        assertNotNull(response.getBody());
    }
    @Test
    void e_delete() {
        String url = baseURL + "/delete/"+feedback.getId();
        System.out.println("URL: "+url);
        restTemplate.delete(url);

    }

    @Test
    void d_getall() {
        String url = baseURL +"/getall/";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity=new HttpEntity<>(null,headers);
        ResponseEntity<String>response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
        System.out.printf("Show ALL: ");
        System.out.println(response);
        System.out.println(response.getBody());
    }
}