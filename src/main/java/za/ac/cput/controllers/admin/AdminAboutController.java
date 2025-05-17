package za.ac.cput.controllers.admin;
/**
 * AdminAboutUsController.java
 * This is an Admin Controller class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.AboutUs;
import za.ac.cput.factory.impl.AboutUsFactory;
import za.ac.cput.service.impl.AboutUsServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/aboutUs")
public class AdminAboutController {

    @Autowired
    private AboutUsServiceImpl aboutUsService;

    @PostMapping("/create")
    public ResponseEntity<AboutUs> create(@RequestBody AboutUs aboutUs) {
        AboutUs newAbout = AboutUsFactory.createAboutUs(aboutUs.getId(), aboutUs.getAddress(), aboutUs.getOfficeHours(), aboutUs.getEmail(), aboutUs.getTelephone(), aboutUs.getWhatsApp());
        AboutUs aboutUsSaved = this.aboutUsService.create(newAbout);
        return ResponseEntity.ok(aboutUsSaved);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<AboutUs> read(@PathVariable("id") int id) {
        AboutUs readAbout = this.aboutUsService.read(id);
        return ResponseEntity.ok(readAbout);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AboutUs> update(@PathVariable int id, @RequestBody AboutUs updatedAbout) {
        AboutUs updateAbout = aboutUsService.update(updatedAbout);
        return new ResponseEntity<>(updateAbout, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AboutUs>> getAll() {
        List aboutList = this.aboutUsService.getAll();
        return ResponseEntity.ok(aboutList);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        this.aboutUsService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}


