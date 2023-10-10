package za.ac.cput.service.impl;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.ContactUs;
import za.ac.cput.factory.impl.ContactUsFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class ContactUsServiceImplTest {

    @Autowired
    private ContactUsServiceImpl contactUsService;

    private ContactUs contactUs = ContactUsFactory.buildContactUs(1, "Miss", "Angel", "Hendricks", "hendricks.a@gmail.com", "After Rental", "Hi there, I would like to find out if you have any branches around Worcester. If you do, would I be able to rent a car this side and drop it off at the Airport branch?");
    private ContactUs contactUs1 = ContactUsFactory.buildContactUs(2, "Mr", "Joshua", "SteinKaap", "steinkaapj@yahoo.com", "Car Rental", "If I book a car online and make all the necessary payments, can I send someone else to pickup the car on my behalf?");
    private ContactUs contactUs2 = ContactUsFactory.buildContactUs(3, "Mr", "Aldrin", "Pollard", "a.pollard@gmail.com", "Payment", "I received a notification that there's an outstanding balance of the damages the car came back with. Can someone contact me on this and explain how come when I had payed a deposit.");

    @Test
    @Order(1)
    void create(){

        ContactUs created = contactUsService.create(contactUs);
        System.out.println("First Contact has been created: " + created);
        assertNotNull(created);

        ContactUs created1 = contactUsService.create(contactUs1);
        System.out.println("Second Contact has been created: " + created1);
        assertNotNull(created);

        ContactUs created2 = contactUsService.create(contactUs2);
        System.out.println("Third Contact has been created: " + created2);
        assertNotNull(created2);

        assertNotEquals(created1, created2);
        assertNotEquals(created1, created);
        assertNotEquals(created, created2);
    }

    @Test
    @Order(2)
    void read(){
        ContactUs read = contactUsService.read(contactUs.getId());
        System.out.println("Read test: " + read);
        assertNotNull(read);

    }

    @Test
    @Order(3)
    void update(){
        ContactUs updated = new ContactUs.Builder().copy(contactUs)
                        .setLastName("Lou").build();
        System.out.println("Updated: " + contactUsService.update(updated));
        assertNotSame(updated.getLastName(), contactUs.getLastName());
    }

    @Test
    @Order(4)
    void getAll() {

        System.out.println("Show all: ");
        ArrayList<ContactUs> listAll = contactUsService.findAll();
        for (ContactUs contactUs0 : listAll) {
            System.out.println(contactUs0);
        }
    }

    @Test
    @Order(5)
    void delete(){
        this.contactUsService.deleteById(contactUs1.getId());
        List<ContactUs> contactUsList = this.contactUsService.findAll();
        assertEquals(2, contactUsList.size());
    }
}