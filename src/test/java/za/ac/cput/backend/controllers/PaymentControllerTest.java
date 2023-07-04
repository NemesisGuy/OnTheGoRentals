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
import za.ac.cput.domain.impl.Payment;
import za.ac.cput.factory.impl.PaymentFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class PaymentControllerTest {

    //testing
    private final String baseURL = "http://localhost:8080/api/payment";
    Payment payment = PaymentFactory.createPayment(
            2000.00,
            "cash",
            LocalDate.parse("01-01-23", DateTimeFormatter.ofPattern("MM-dd-yy")),
            01
    );
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void a_processPayment() {
        String url = baseURL + "/create";
        System.out.println("URL: " + url);

        ResponseEntity<Payment> response = restTemplate.postForEntity(url, payment, Payment.class);
        assertNotNull(response);
        assertNotNull(response.getBody());

        Payment saved = response.getBody();
        assertEquals(saved.getId(), saved.getId());
        System.out.println("Saved data: " + saved);
    }

    @Disabled
    @Test
    void b_getPayment() {
        String url = baseURL + "/read/" + payment.getId();
        System.out.println("URL: " + url);

        ResponseEntity<Payment> response = restTemplate.getForEntity(url, Payment.class);
        assertEquals(payment.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }

    @Test
    void c_getAllPayments() {
        String url = baseURL + "/get-all";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Show ALL:");
        System.out.println(response);
        System.out.println(response.getBody());
    }

    @Test
    void d_updatePayment() {
        Payment updated = new Payment.Builder()
                .copy(payment)
                .setPaymentAmount(4000.0)
                .build();
        String url = baseURL + "/update";
        System.out.println("URL: " + url);
        System.out.println("Post data: " + updated);

        ResponseEntity<Payment> response = restTemplate.postForEntity(url, updated, Payment.class);
        assertNotNull(response.getBody());
    }

    @Disabled
    @Test
    void e_deletePayment() {
        String url = baseURL + "/delete/" + payment.getId();
        System.out.println("URL: " + url);
        restTemplate.delete(url);
    }
}