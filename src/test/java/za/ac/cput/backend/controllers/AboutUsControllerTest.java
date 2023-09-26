package za.ac.cput.backend.controllers;
/**AboutUsControllerTest.java
 * This is a Controller Test class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 * */
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.AboutUs;
import za.ac.cput.factory.impl.AboutUsFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AboutUsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseURL = "http://localhost:8080/api/aboutUs";
    private  AboutUs about = AboutUsFactory.createAboutUs(2, "Cape Town International Apt, Cape Town", "Monday - Sunday: 6:00am - 22:30pm", "info@onthegorental.com", "+27 21 461 6182", "+27 78 303 9813");


    @Test
    @Order(1)
    void read(){
        String url = baseURL + "/read/" + about.getId();
        System.out.println("URL" + url);

        ResponseEntity<AboutUs> response = restTemplate.getForEntity(url, AboutUs.class);
        assertEquals(about.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }
}
