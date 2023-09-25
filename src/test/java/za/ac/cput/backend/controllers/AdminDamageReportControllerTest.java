package za.ac.cput.backend.controllers;
/**DamageReportControllerTest.java
 * Controller Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023*/
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import za.ac.cput.controllers.admin.AdminDamageReportController;
import za.ac.cput.domain.*;
import za.ac.cput.factory.impl.DamageReportFactory;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminDamageReportControllerTest {

    @Autowired private AdminDamageReportController controller;

    @Autowired private TestRestTemplate restTemplate;

    private String baseURL;
    private static DamageReport damageReport1;

    @BeforeEach
    void setUp(){
        Car car1 = new Car.Builder().id(9).make("Renault").model("Kiger").priceGroup(PriceGroup.ECONOMY).category("Compact").year(2022).licensePlate("ADX147").build();
        Car car2 = new Car.Builder().id(10).make("Suzuki").model("Swift").priceGroup(PriceGroup.ECONOMY).category("Compact").year(2023).licensePlate("ADC123").build();
        Car car3 = new Car.Builder().id(11).make("Suzuki").model("Baleno").priceGroup(PriceGroup.STANDARD).category("Compact").year(2023).licensePlate("ADE123").build();

        User user1 = new User.Builder().id(11).userName("Eddie").email("jacobse@cput.ac.za").firstName("Edison").lastName("Jacobs").pictureUrl("image-9.jpg").phoneNumber("079 576 0088").password("eddie#2023").role("Driver").build();
        Rental rental1 = new Rental.Builder().setId(10).setUser(user1).setCar(car1).setIssuer(5).setReceiver(1).setFine(700).setIssuedDate(LocalDateTime.now()).setDateReturned(java.time.LocalDateTime.parse("2023-09-25T20:30:53")).build();
        damageReport1 = DamageReportFactory.createReport(13,rental1, "Scratches on the bumper.", LocalDateTime.parse("2023-09-25T20:40:56"), "Paarl", 3500.00);
        baseURL = "http://localhost:8080" + "/api/admin/damageReport";
    }
    @Test
    @Order(1)
    void save(){
        assertNotNull(controller);
        String url = baseURL + "/create";
        ResponseEntity<DamageReport> damageReportResponseEntity = restTemplate.postForEntity(url, damageReport1, DamageReport.class);
        assertNotNull(damageReportResponseEntity);
        assertEquals(HttpStatus.OK, damageReportResponseEntity.getStatusCode());
        assertNotNull(damageReportResponseEntity.getBody());
        DamageReport newDamageReport = damageReportResponseEntity.getBody();
        assertNotNull(newDamageReport);
        System.out.println("Saved report" + newDamageReport);

    }
    @Test
    @Order(2)
    void findById(){

        String url = baseURL + "/read/" + damageReport1.getId();
        System.out.println("URL" + url);

        ResponseEntity<DamageReport> response = restTemplate.getForEntity(url, DamageReport.class);
        assertEquals(damageReport1.getId(), response.getBody().getId());
        System.out.println(response.getBody());
    }
    @Test
    @Order(3)
    void update(){

        DamageReport updated = new DamageReport.Builder().copy(damageReport1)
                .setRepairCost(3000.00).build();
        String url = baseURL + "/update";
        System.out.println("URL" + url);
        System.out.println("Updated report: " + updated);

        ResponseEntity<DamageReport> updateResponse =  restTemplate.postForEntity(url, updated, DamageReport.class);
        assertNotNull(updateResponse.getBody());
        DamageReport updatedReport = updateResponse.getBody();
        System.out.println("Updated data: " + updatedReport);
    }

    @Test
    @Order(4)
    void getAll(){
        String url = baseURL + "/All";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Show All: ");
        System.out.println(response);
        System.out.println(response.getBody());
    }
    @Test
    @Order(5)
    void deleteById(){
        String url = baseURL + "/delete/" + damageReport1.getId();
        System.out.println("URL: " + url);
        restTemplate.delete(url);
    }
}

