package za.ac.cput.backend.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.ContactUs;
import za.ac.cput.factory.impl.ContactUsFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminContactUsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseURL = "http://localhost:8080/api/admin/contactUs";

    private ContactUs createContact = ContactUsFactory.buildContactUs(5, "Mrs.", "Lindelwa", "Mhlekude", "lindelwa.mhle2@gmail.com","Car Rental", "Can I book a car from you guys if I don't have a drivers licence? If yes, will I have to pay an extra money to have another person driving the car?");


    @Test
    @Order(1)
    void create(){

        String url = baseURL + "/create";
        System.out.println("URL: " + url);

        ResponseEntity<ContactUs> postResponse = restTemplate.postForEntity(url, createContact, ContactUs.class);
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotNull(postResponse.getBody());

        ContactUs saved = postResponse.getBody();
        Assertions.assertEquals(createContact.getId(), saved.getId());
        System.out.println("Saved data: " + saved);
    }

    @Test
    @Order(2)
    void findById(){

        String url = baseURL + "/read/" + createContact.getId();
        System.out.println("URL" + url);

        ResponseEntity<ContactUs> response = restTemplate.getForEntity(url, ContactUs.class);
        assertEquals(createContact.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }

    @Test
    @Order(3)
    void update(){
        ContactUs updated = new ContactUs.Builder().copy(createContact)
                .setSubject("Before Rental").build();
        String url = baseURL + "/update";
        System.out.println("URL" + url);
        System.out.println("Updated: " + updated);

        ResponseEntity<ContactUs> updateResponse =  restTemplate.postForEntity(url, updated, ContactUs.class);
        assertNotNull(updateResponse.getBody());
        ContactUs updatedContact = updateResponse.getBody();
        System.out.println("Updated data: " + updatedContact);
    }
    @Test
    @Order(4)
    void getAll() {
        String url = baseURL + "/all";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Show ALL:");
        System.out.println(response);
        System.out.println(response.getBody());
    }
    @Test
    @Order(5)
    void deleteById(){
        String url = baseURL + "/delete/" + createContact.getId();
        System.out.println("URL: " + url);
        restTemplate.delete(url);
    }
}
