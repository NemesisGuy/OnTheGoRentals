package za.ac.cput.backend.controllers;
/**AdminAboutUsControllerTest.java
 * This is an Admin Controller Test class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 * */
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import za.ac.cput.domain.AboutUs;
import za.ac.cput.factory.impl.AboutUsFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminAboutControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseURL = "http://localhost:8080/api/admin/aboutUs";

    private  AboutUs about = AboutUsFactory.createAboutUs(2, "Cape Town International Apt, Cape Town", "Monday - Sunday: 6:00am - 22:30pm", "info@onthegorental.com", "+27 21 461 6182", "+27 78 303 9813");

    @Test
    @Order(1)
    void create(){
        String url = baseURL + "/create";
        System.out.println("URL: " + url);

        ResponseEntity<AboutUs> postResponse = restTemplate.postForEntity(url, about, AboutUs.class);
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotNull(postResponse.getBody());

        AboutUs saved = postResponse.getBody();
        Assertions.assertEquals(about.getId(), saved.getId());
        System.out.println("Saved data: " + saved);
    }
    @Test
    @Order(2)
    void read(){
        String url = baseURL + "/read/" + about.getId();
        System.out.println("URL" + url);

        ResponseEntity<AboutUs> response = restTemplate.getForEntity(url, AboutUs.class);
        assertEquals(about.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }
    @Test
    @Order(3)
    void update(){
        AboutUs updated = new AboutUs.Builder().copy(about)
                .setTelephone("+27 21 578 5624").build();
        String url = baseURL + "/update";
        System.out.println("URL" + url);
        System.out.println("Updated: " + updated);

        ResponseEntity<AboutUs> updateResponse =  restTemplate.postForEntity(url, updated, AboutUs.class);
        assertNotNull(updateResponse.getBody());
        AboutUs updatedDetails = updateResponse.getBody();
        System.out.println("Updated data: " + updatedDetails);
    }
    @Test
    @Order(4)
    void getAll(){
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
    void delete(){
        String url = baseURL + "/delete/" + about.getId();
        System.out.println("URL: " + url);
        restTemplate.delete(url);
    }
}
