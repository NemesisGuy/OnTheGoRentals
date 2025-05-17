package za.ac.cput.controllers;
/**
 * AboutUsController.java
 * Controller class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.AboutUs;
import za.ac.cput.service.impl.AboutUsServiceImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * AboutUsController.java
 * This is the controller for the AboutUs class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 * 220165289@mycput.ac.za
 */

@RestController
@RequestMapping("/api/aboutUs")
// @CrossOrigin(origins = "http://localhost:5173") // Uncomment if needed for CORS
public class AboutUsController {

    @Autowired
    private AboutUsServiceImpl service;

    @GetMapping("/read/01")
    public ResponseEntity<AboutUs> read(@PathVariable("id") int id) {
        id = 01;
        AboutUs readAbout = this.service.read(id);
        return ResponseEntity.ok(readAbout);
    }

    @GetMapping("/list/all")
    public List<AboutUs> getAll() {
        List<AboutUs> allAboutUs = service.getAll();
        return allAboutUs;
    }

    @GetMapping("/latest")
    public ResponseEntity<AboutUs> getLatest() {
        List<AboutUs> allAboutUs = service.getAll();
        // Fetch the AboutUs entry with the highest id (most recent)
        Optional<AboutUs> newestAboutUs = allAboutUs.stream()
                .max(Comparator.comparingInt(AboutUs::getId));

        return newestAboutUs.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}