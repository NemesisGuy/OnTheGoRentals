package za.ac.cput.backend.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.ContactUs;
import za.ac.cput.factory.impl.ContactUsFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactUsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseURL = "http://localhost:8080/api/contactUs";

    private ContactUs contactUscreated = ContactUsFactory.buildContactUs(4, "Mrs", "Azana", "Zolile", "zolileazana@gmail.com","Booking Cancellation", "I made a booking to rent a car. However, there has been changes on my travelling arrangements and would like to cancel the booking.");

    @Test
    @Order(1)
    void create(){

        String url = baseURL + "/create";
        System.out.println("URL: " + url);

        ResponseEntity<ContactUs> postResponse = restTemplate.postForEntity(url, contactUscreated, ContactUs.class);
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotNull(postResponse.getBody());

        ContactUs saved = postResponse.getBody();
        Assertions.assertEquals(contactUscreated.getId(), saved.getId());
        System.out.println("Saved data: " + saved);

    }
}
