/*
package za.ac.cput.controllers;

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
import za.ac.cput.domain.Driver;
import za.ac.cput.factory.impl.DriverFactory;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DriverControllerTest {
private static Driver driver = DriverFactory.createDriver("Cwenga","Dlova","10");
   @Autowired
   private TestRestTemplate restTemplate;
   private final String baseURL = "http://localhost:8080/driver";
    @Test
    void a_create() {
        String url = baseURL +"/create";
        ResponseEntity<Driver> postResponse = restTemplate.postForEntity(url,driver,Driver.class);
        assertNotNull(postResponse);
        assertNotNull(postResponse.getBody());

        Driver savedEmployee=postResponse.getBody();
        System.out.println("Saved Data: " +"savedEmployee");
        assertEquals(driver.getId(),savedEmployee.getId());
    }

    @Test
    void b_read() {
        String url = baseURL + "/read/" + driver.getId();
        System.out.println("URL: " + url);
        ResponseEntity<Driver> response = restTemplate.getForEntity(url, Driver.class);
        assertEquals(driver.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }

    @Test
    void c_updated() {
        Driver updated = new Driver.Builder().copy(driver).setFirstName("Sanelisiwe").setLastName("Hlazo").setLicenseCode("8").build();
        String url = baseURL +"/update/";
        System.out.println("URL: "+url);
        System.out.println("Post data: " +updated);
        ResponseEntity<Driver>response = restTemplate.postForEntity(url,updated,Driver.class);
        assertNotNull(response.getBody());
    }
    @Test
    void e_delete() {
        String url = baseURL + "/delete/"+driver.getId();
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
}*/
