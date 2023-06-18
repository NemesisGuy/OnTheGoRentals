package za.ac.cput.controllers;

/**
 * ReservationsControllerTest.java
 * Class for the Reservations Controller Test
 * Author: Cwenga Dlova (214310671)
 * Date:  13 June 2023
 */

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.impl.Reservations;
import za.ac.cput.factory.impl.ReservationsFactory;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReservationsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseURL = "http://localhost:8080/api/reservations";
    private Reservations reservations = ReservationsFactory.createReservations("Cape Town", LocalDate.parse("2023-02-25"), Time.valueOf(LocalTime.of(12,00)), "Claremont", LocalDate.parse("2023-07-25"), Time.valueOf(LocalTime.of(13,00)));


    @Test
    void a_create(){

        String url = baseURL + "/create";
        ResponseEntity<Reservations> postResponse = restTemplate.postForEntity(url, reservations, Reservations.class);
        //assertNotNull(postResponse);
       // assertNotNull(postResponse.getBody());
        Reservations newreservation = postResponse.getBody();
        System.out.println("saved data: " +  newreservation);
        //assertEquals(reservations.getId(), postResponse.getBody().getId());

    }

    @Test
    void b_update(){

        Reservations updated = new Reservations.Builder().copy(reservations).setPickUpLocation("Khayelitsha").build();
        String url = baseURL + "/update";
        System.out.println("URL: "  + url);
        System.out.println("post data: "  + updated);
        ResponseEntity<Reservations>  response = restTemplate.postForEntity(url, updated, Reservations.class);
        assertNotNull(response.getBody());

    }

    @Test
    @Disabled
    void c_getAll() {

        String url = baseURL + "/getAll";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Integer> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.GET, entity, Integer.class);
        System.out.println("Show All: ");
        System.out.println(response);
        System.out.println(response.getBody());


    }

}