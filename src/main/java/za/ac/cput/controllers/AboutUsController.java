package za.ac.cput.controllers;
/**AboutUsController.java
 * Controller class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 * */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.AboutUs;
import za.ac.cput.service.impl.AboutUsServiceImpl;

@RestController
@RequestMapping("/api/aboutUs")
public class AboutUsController {

    @Autowired
    private AboutUsServiceImpl service;

    @GetMapping("/read/{id}")
    public ResponseEntity<AboutUs> read(@PathVariable("id") int id) {
        AboutUs readAbout = this.service.read(id);
        return ResponseEntity.ok(readAbout);
    }
}

