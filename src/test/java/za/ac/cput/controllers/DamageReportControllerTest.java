/**package za.ac.cput.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.*;
import za.ac.cput.factory.impl.CarFactory;
import za.ac.cput.factory.impl.DamageReportFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DamageReportControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private DamageReportController controller;
    @Autowired private TestRestTemplate restTemplate;

    private DamageReport report;
    private Rental rental;
    private String baseURL;

    @BeforeEach
    void setUp(){
        //Car car = new Car.Builder().id(1).make("Toyota").model("Yaris").year(2012).category("Compact").licensePlate("ADR 235");
        report = DamageReportFactory.createReport(2, Rental.builder().build(), "Smashed windscreen.", LocalDateTime.parse("2023-09-09T00:00:00"), "Langa Township", 500.00);
        baseURL = "http://localhost:" + this.port + "/damageReport";
        System.out.println(baseURL);
    }

    @Test
    @Order(1)
    void save(){

        String url = baseURL + "/createReport";
        ResponseEntity<DamageReport> response = restTemplate.postForEntity(url, controller, DamageReport.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertNotNull(response.getBody());
        DamageReport newDamagereport = response.getBody();
        assertNotNull(newDamagereport);
        System.out.println("Saved report" + newDamagereport);

    }


}
*/

