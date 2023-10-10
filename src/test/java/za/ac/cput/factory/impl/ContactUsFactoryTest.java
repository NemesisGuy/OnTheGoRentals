package za.ac.cput.factory.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.ContactUs;

import static org.junit.jupiter.api.Assertions.*;

class ContactUsFactoryTest {
    @Test
    public void testContactUsFactory() {
        ContactUsFactory contactUsFactory = new ContactUsFactory();
        ContactUs contactUs = contactUsFactory.create();
        Assertions.assertNotNull(contactUs);
        assertNotNull(contactUs.getId());

    }
}