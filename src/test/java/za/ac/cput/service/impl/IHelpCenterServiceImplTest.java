/*
package za.ac.cput.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.factory.impl.HelpCenterFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
class IHelpCenterServiceImplTest {
    @Autowired
    private IHelpCenterServiceImpl helpCenterService;

    private static HelpCenter helpCenter1 = HelpCenterFactory.createHelpCenter(
            "General",
            "How to Create an Account",
            "To create an account, click on the 'Sign Up' button and fill in the required information.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter2 = HelpCenterFactory.createHelpCenter(
            "Booking",
            "How to Book a Car",
            "To book a car, select the desired car from the list, choose the rental dates, and proceed to checkout.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter3 = HelpCenterFactory.createHelpCenter(
            "Accounts",
            "How to Create an Account",
            "To create a user account, click on the 'Sign Up' button and fill in the required information.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter4 = HelpCenterFactory.createHelpCenter(
            "Accounts",
            "How to Log In",
            "To log in as a user, enter your username and password on the login page and click 'Log In'.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter5 = HelpCenterFactory.createHelpCenter(
            "Accounts",
            "How to Reset Password",
            "To reset your password, click on the 'Forgot Password' link on the login page and follow the instructions.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter6 = HelpCenterFactory.createHelpCenter(
            "Accounts",
            "How to Update Profile Information",
            "To update your profile information, go to your profile page and click on the 'Edit Profile' button.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter7 = HelpCenterFactory.createHelpCenter(
            "Accounts",
            "How to View Rental History",
            "To view your rental history, go to your profile page and click on the 'Rental History' tab.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter8 = HelpCenterFactory.createHelpCenter(
            "Accounts",
            "How to Contact Support",
            "To contact customer support, visit the 'Contact Us' page and fill out the contact form.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter9 = HelpCenterFactory.createHelpCenter(
            "Booking",
            "How to Extend a Rental",
            "To extend a rental, go to your rental history and click on the 'Extend Rental' button next to the reservation.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter10 = HelpCenterFactory.createHelpCenter(
            "General",
            "How to View Rental History",
            "To view your rental history, go to your profile page and click on the 'Rental History' tab.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter11 = HelpCenterFactory.createHelpCenter(
            "Booking",
            "How to Select Pickup/Drop-off Locations",
            "When booking a car, you can select pickup and drop-off locations on the booking form.",
            LocalDateTime.now(), LocalDateTime.now());
    private static HelpCenter helpCenter12 = HelpCenterFactory.createHelpCenter(
            "General",
            "How to Reset Password",
            "To reset your password, click on the 'Forgot Password' link on the login page and follow the instructions.",
            LocalDateTime.now(), LocalDateTime.now());

    private static HelpCenter testCase0 = HelpCenterFactory.createHelpCenter(
            "test case?",
            "title",
            "content",
            LocalDateTime.now(), LocalDateTime.now());

    @Test
    void a_create() {

        HelpCenter created1 = helpCenterService.create(helpCenter1);
        System.out.println("Created 1: " + created1);
        Assertions.assertNotNull(created1);

        HelpCenter created2 = helpCenterService.create(helpCenter2);
        System.out.println("Created 2: " + created2);
        Assertions.assertNotNull(created2);

        HelpCenter created3 = helpCenterService.create(helpCenter3);
        System.out.println("Created 3: " + created3);
        Assertions.assertNotNull(created3);

        HelpCenter created4 = helpCenterService.create(helpCenter4);
        System.out.println("Created 4: " + created4);
        Assertions.assertNotNull(created4);

        HelpCenter created5 = helpCenterService.create(helpCenter5);
        System.out.println("Created 5: " + created5);
        Assertions.assertNotNull(created5);

        HelpCenter created6 = helpCenterService.create(helpCenter6);
        System.out.println("Created 6: " + created6);
        Assertions.assertNotNull(created6);

        HelpCenter created7 = helpCenterService.create(helpCenter7);
        System.out.println("Created 7: " + created7);
        Assertions.assertNotNull(created7);

        HelpCenter created8 = helpCenterService.create(helpCenter8);
        System.out.println("Created 8: " + created8);
        Assertions.assertNotNull(created8);

        HelpCenter created9 = helpCenterService.create(helpCenter9);
        System.out.println("Created 9: " + created9);
        Assertions.assertNotNull(created9);

        HelpCenter created10 = helpCenterService.create(helpCenter10);
        System.out.println("Created 10: " + created10);
        Assertions.assertNotNull(created10);

        HelpCenter created11 = helpCenterService.create(helpCenter11);
        System.out.println("Created 11: " + created11);
        Assertions.assertNotNull(created11);

        HelpCenter created12 = helpCenterService.create(helpCenter12);
        System.out.println("Created 12: " + created12);
        Assertions.assertNotNull(created12);

        HelpCenter test = helpCenterService.create(testCase0);
        System.out.println("test case 1: " + test);
        Assertions.assertNotNull(test);

        Assertions.assertNotSame(created1, created2);
        Assertions.assertNotSame(created3, created4);
        Assertions.assertNotSame(created5, created6);
        Assertions.assertNotSame(created7, created8);
        Assertions.assertNotSame(created9, created10);
        Assertions.assertNotSame(created11, created12);
    }

    @Test
    void b_read() {
        HelpCenter readTest = helpCenterService.read(testCase0.getId());
        System.out.println("Read test: " + readTest);
        Assertions.assertNotNull(readTest);
    }

    @Test
    void c_update() {
        HelpCenter updatedTest = new HelpCenter.Builder().copy(testCase0)
                .setCategory("Updated Category")
                .setUpdatedAt(LocalDateTime.now())
                .build();
        System.out.println("Updated: " + helpCenterService.update(updatedTest));
        Assertions.assertNotSame(updatedTest.getCategory(), testCase0.getCategory());
    }

    @Test
    void d_getAll() {
        System.out.println("Show all: ");

        ArrayList<HelpCenter> helpCenterList = helpCenterService.getAll();
        for (HelpCenter helpCenter: helpCenterList) {
            System.out.println(helpCenter);
        }
    }

    @Test
    void e_getAllByCategory() {
        final String CATEGORY = "Booking";
        System.out.println("Show all by Category: ");

        ArrayList<HelpCenter> categoryList = helpCenterService.getAllByCategory(CATEGORY);
        for (HelpCenter helpCenter : categoryList) {
            System.out.println(helpCenter);
        }
    }

    @Test
    void f_delete() {
        System.out.println(helpCenterService.read(testCase0.getId()));
        boolean success = helpCenterService.delete(testCase0.getId());
        Assertions.assertTrue(success);
        System.out.println("Deleted: " + success);
    }
}*/
