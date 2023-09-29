package za.ac.cput.service.impl;
/**AboutUsServiceImplTest.java
 * Service class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 * */
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.AboutUs;
import za.ac.cput.factory.impl.AboutUsFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class AboutUsServiceImplTest {
    @Autowired
    private AboutUsServiceImpl aboutUsService;

    private AboutUs about = AboutUsFactory.createAboutUs(1, "28 Strand St, City Centre, Cape Town, 8100","Monday - Friday: 6h00am to 22h30pm","info@carental.com","+27210001111","+27791112222");

    @Test
    @Order(1)
    void create(){
        AboutUs created = aboutUsService.create(about);
        System.out.println("About details created: " + created);
        assertNotNull(created);
    }

    @Test
    @Order(2)
    void read(){
        AboutUs read = aboutUsService.read(about.getId());
        System.out.println("Read test: " + read);
        assertNotNull(read);
    }
    @Test
    @Order(3)
    void update(){
        AboutUs updated = new AboutUs.Builder().copy(about)
                .setWhatsApp("+27824561237").build();
        System.out.println("Updated: " + aboutUsService.update(updated));
        assertNotSame(updated.getWhatsApp(), about.getWhatsApp());
    }
    @Test
    @Order(4)
    void getAll(){
        List<AboutUs> listAll = aboutUsService.getAll();
        System.out.println("\nshow all: " + listAll);
    }
    @Test
    @Order(5)
    void delete(){
        this.aboutUsService.delete(about.getId());
        List<AboutUs> aboutUsList = this.aboutUsService.getAll();
        assertEquals(0, aboutUsList.size());
    }
}