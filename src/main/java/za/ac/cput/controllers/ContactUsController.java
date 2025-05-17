package za.ac.cput.controllers;
/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.ContactUs;
import za.ac.cput.factory.impl.ContactUsFactory;
import za.ac.cput.service.impl.ContactUsServiceImpl;

@RestController
@RequestMapping("/api/contactUs")
public class ContactUsController {

    @Autowired
    private ContactUsServiceImpl contactUsService;

    @PostMapping("/create")
    public ResponseEntity<ContactUs> create(@RequestBody ContactUs contactUs) {

        ContactUs newContactUs = ContactUsFactory.buildContactUs(contactUs.getId(), contactUs.getTitle(), contactUs.getFirstName(), contactUs.getLastName(), contactUs.getEmail(), contactUs.getSubject(), contactUs.getMessage());
        ContactUs saved = this.contactUsService.create(newContactUs);
        return ResponseEntity.ok(saved);
    }
}
