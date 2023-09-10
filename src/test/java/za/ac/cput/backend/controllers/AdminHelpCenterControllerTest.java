package za.ac.cput.backend.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.factory.impl.HelpCenterFactory;

import java.time.LocalDateTime;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminHelpCenterControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseURL = "http://localhost:8080/api/admin/help-center";

    private HelpCenter helpCenterToCreate = HelpCenterFactory.helpCenterCreated(14, "Book", "Modifying Booking Dates", "You can modify your booking dates by logging into your account and accessing the 'My Bookings' section.", LocalDateTime.now(), LocalDateTime.now());

    @Test
    void a_createHelpCenter() {
        String url = baseURL + "/create";
        System.out.println("URL: " + url);

        ResponseEntity<HelpCenter> postResponse = restTemplate.postForEntity(url, helpCenterToCreate, HelpCenter.class);
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotNull(postResponse.getBody());

        HelpCenter saved = postResponse.getBody();
        Assertions.assertEquals(helpCenterToCreate.getId(), saved.getId());
        System.out.println("Saved data: " + saved);
    }

    @Test
    void b_readHelpCenter() {
        String url = baseURL + "/read/" + helpCenterToCreate.getId();
        System.out.println("URL: " + url);

        ResponseEntity<HelpCenter> response = restTemplate.getForEntity(url, HelpCenter.class);
        Assertions.assertEquals(helpCenterToCreate.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }

    @Test
    void c_updateHelpCenter() {
        HelpCenter updated = new HelpCenter.Builder().copy(helpCenterToCreate)
                .setCategory("Updated Category")
                .setTitle("Updated Title")
                .setContent("Updated Content")
                .setUpdatedAt(LocalDateTime.now())
                .build();

        String url = baseURL + "/update";
        System.out.println("URL: " + url);
        System.out.println("Post data: " + updated);

        ResponseEntity<HelpCenter> response = restTemplate.postForEntity(url, updated, HelpCenter.class);
        Assertions.assertNotNull(response.getBody());
        HelpCenter updatedHelpCenter = response.getBody();
        System.out.println("Updated data: " + updatedHelpCenter);
    }

    @Test
    void d_delete() {
        String url = baseURL + "/delete/" + helpCenterToCreate.getId();
        System.out.println("URL: " + url);
        restTemplate.delete(url);
    }

    @Test
    void e_getAll() {
        String url = baseURL + "/get-all";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Show ALL:");
        System.out.println(response);
        System.out.println(response.getBody());
    }
}