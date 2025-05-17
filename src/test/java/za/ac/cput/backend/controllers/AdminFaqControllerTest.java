/*
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
import za.ac.cput.domain.Faq;
import za.ac.cput.factory.impl.FaqFactory;

import java.time.LocalDateTime;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminFaqControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseURL = "http://localhost:8080/api/admin/faq";

    private static Faq faqToCreate = FaqFactory.faqCreated(12,"What is the cancellation policy?", "Our cancellation policy allows for free cancellations up to 24 hours before your reservation starts. After that, a cancellation fee may apply.", LocalDateTime.now(), LocalDateTime.now());

*/
/*    @Test
    void a_createFaq() {
        String url = baseURL + "/create";
        System.out.println("URL: " + url);

        ResponseEntity<Faq> postResponse = restTemplate.postForEntity(url, faqToCreate, Faq.class);
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotNull(postResponse.getBody());

        Faq saved = postResponse.getBody();
        Assertions.assertEquals(faqToCreate.getId(), saved.getId());
        System.out.println("Saved data: " + saved);
    }*//*


 */
/*  @Test
    void b_readFaq() {
        String url = baseURL + "/read/" + faqToCreate.getId();
        System.out.println("URL: " + url);

        ResponseEntity<Faq> response = restTemplate.getForEntity(url, Faq.class);
        Assertions.assertEquals(faqToCreate.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }
*//*

    @Test
    void c_updateFaq() {
        Faq updated = new Faq.Builder().copy(faqToCreate)
                .setQuestion("Updated Question")
                .setAnswer("Updated Answer")
                .setUpdatedAt(LocalDateTime.now())
                .build();

        String url = baseURL + "/update";
        System.out.println("URL: " + url);
        System.out.println("Post data: " + updated);

        ResponseEntity<Faq> response = restTemplate.postForEntity(url, updated, Faq.class); // Corrected the expected response type to Faq
        Assertions.assertNotNull(response.getBody());
        Faq updatedFaq = response.getBody();
        System.out.println("Updated data: " + updatedFaq);
    }

    @Test
    void d_delete() {
        String url = baseURL + "/delete/" + faqToCreate.getId();
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
}*/
