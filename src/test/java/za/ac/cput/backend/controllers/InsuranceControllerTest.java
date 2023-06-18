package za.ac.cput.backend.controllers;

import org.junit.jupiter.api.Disabled;
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
import za.ac.cput.domain.impl.Insurance;
import za.ac.cput.domain.impl.Payment;
import za.ac.cput.factory.impl.InsuranceFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class InsuranceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    //testing
    private final String baseURL = "http://localhost:8080/api/insurance";

    private Insurance insurance = InsuranceFactory.createInsurance(
            "Collision Damage Waiver",
            42000.0,
            LocalDate.parse("2022-01-01"),
            LocalDate.parse("2023-12-31"),
            null
    );

    @Test
    void a_create() {
        String url = baseURL + "/create";
        System.out.println("URL: " + url);

        ResponseEntity<Insurance> response = restTemplate.postForEntity(url, insurance, Insurance.class);
        assertNotNull(response);
        assertNotNull(response.getBody());

        Insurance saved = response.getBody();
        assertEquals(saved.getId(), saved.getId());
        System.out.println("Saved data: " + saved);
    }
    @Disabled
    @Test
    void b_read() {
        String url = baseURL + "/read/" + insurance.getId();
        System.out.println("URL: " + url);

        ResponseEntity<Insurance> response = restTemplate.getForEntity(url, Insurance.class);
        assertEquals(insurance.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }

    @Test
    void c_getAll() {
        String url = baseURL + "/get-all";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Show ALL:");
        System.out.println(response);
        System.out.println(response.getBody());
    }

    @Test
    void d_update() {
        Insurance updated = new Insurance.Builder()
                .copy(insurance)
                .setInsuranceAmount(50000.0)
                .build();
        String url = baseURL + "/update";
        System.out.println("URL: " + url);
        System.out.println("Post data: " + updated);

        restTemplate.put(url, updated);

//        ResponseEntity<Payment> response = restTemplate.postForEntity(url, updated, Payment.class);
//        assertNotNull(response.getBody());
    }

    @Disabled
    @Test
    void e_delete() {
        String url = baseURL + "/delete/" + insurance.getId();
        System.out.println("URL: " + url);
        restTemplate.delete(url);
    }
}