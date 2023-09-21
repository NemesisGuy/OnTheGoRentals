package za.ac.cput.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.factory.impl.HelpCenterFactory;

import java.time.LocalDateTime;

class HelpCenterFactoryTest {
    @Test
    void testCreateHelpCenter() {
        HelpCenterFactory factory = new HelpCenterFactory();

        LocalDateTime timeCreated = LocalDateTime.now();
        LocalDateTime timeUpdated = LocalDateTime.now();

        HelpCenter helpCenter1 = factory.createHelpCenter("General", "How to Create an Account", "To create an account, click on the 'Sign Up' button and fill in the required information.", timeCreated, timeUpdated);
//        HelpCenter helpCenter2 = factory.createHelpCenter(2, "Booking", "How to Book a Car", "To book a car, select the desired car from the list, choose the rental dates, and proceed to checkout.", LocalDateTime.now(), LocalDateTime.now());

        Assertions.assertNotNull(helpCenter1);
//        Assertions.assertNotNull(helpCenter2);

//        Assertions.assertEquals(1, helpCenter1.getId());
        Assertions.assertEquals("General", helpCenter1.getCategory());
        Assertions.assertEquals("To create an account, click on the 'Sign Up' button and fill in the required information.", helpCenter1.getContent());
        Assertions.assertEquals(timeCreated, helpCenter1.getCreatedAt());
        Assertions.assertEquals(timeUpdated, helpCenter1.getUpdatedAt());

        System.out.println(helpCenter1);
    }

}