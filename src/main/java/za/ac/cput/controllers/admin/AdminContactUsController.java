package za.ac.cput.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.ContactUs;
import za.ac.cput.factory.impl.ContactUsFactory;
import za.ac.cput.service.impl.ContactUsServiceImpl;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/contactUs")
public class AdminContactUsController {

    @Autowired
    private ContactUsServiceImpl contactUsService;

    @PostMapping("/create")
    public ResponseEntity<ContactUs> create(@RequestBody ContactUs contactUs){

        ContactUs newContactUs = ContactUsFactory.buildContactUs(contactUs.getId(), contactUs.getTitle(), contactUs.getFirstName(), contactUs.getLastName(), contactUs.getEmail(), contactUs.getSubject(), contactUs.getMessage());
        ContactUs saved = this.contactUsService.create(newContactUs);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/read/{id}")
    public ResponseEntity<ContactUs> read(@PathVariable("id") int id){

        ContactUs readContactUs = this.contactUsService.read(id);
        return ResponseEntity.ok(readContactUs);
    }
    @PutMapping("/update/{contactId}")
    public ResponseEntity<ContactUs> update(@PathVariable int contactId, ContactUs updatedContactsUs){
        ContactUs update = contactUsService.update(updatedContactsUs);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<ArrayList<ContactUs>> getAll(){
        ArrayList<ContactUs> listAll = this.contactUsService.findAll();
        return ResponseEntity.ok(listAll);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        this.contactUsService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
