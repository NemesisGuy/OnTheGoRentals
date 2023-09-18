package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Faq;
import za.ac.cput.factory.impl.FaqFactory;

import java.time.LocalDateTime;

class FaqFactoryTest {
    @Test
    void testCreateFaq() {
        FaqFactory factory = new FaqFactory();

        LocalDateTime timeCreated = LocalDateTime.now();
        LocalDateTime timeUpdated = LocalDateTime.now();

        Faq faq = factory.createFaq("What is a FAQ?", "A FAQ is a list of frequently asked questions and their answers.", timeCreated, timeUpdated);

        Assertions.assertNotNull(faq);
//        Assertions.assertEquals(1, faq.getId());
        Assertions.assertEquals("What is a FAQ?", faq.getQuestion());
        Assertions.assertEquals("A FAQ is a list of frequently asked questions and their answers.", faq.getAnswer());
        Assertions.assertEquals(timeCreated, faq.getCreatedAt());
        Assertions.assertEquals(timeUpdated, faq.getUpdatedAt());

        System.out.println(faq);
    }

}