package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.service.impl.IHelpCenterServiceImpl;

import java.util.List;

/**
 * HelpCenterController.java
 * Controller for Help Center FAQs and topics
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@RestController
@RequestMapping("/api/help-center")
@CrossOrigin(origins = "http://localhost:8080") // or your actual frontend domain
public class HelpCenterController {

    @Autowired
    private IHelpCenterServiceImpl helpCenterService;

    @GetMapping("/get-all")
    public ResponseEntity<List<HelpCenter>> getAll() {
        List<HelpCenter> helpCenterList = helpCenterService.getAll();
        if (helpCenterList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(helpCenterList);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<HelpCenter>> getAllByCategory(@PathVariable String category) {
        List<HelpCenter> filteredList = helpCenterService.getAllByCategory(category);
        if (filteredList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(filteredList);
    }
}
