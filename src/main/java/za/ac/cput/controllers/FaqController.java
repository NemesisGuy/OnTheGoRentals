package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.Faq;
import za.ac.cput.service.impl.IFaqServiceImpl;

import java.util.List;

/**
 * FaqController.java
 * This is the controller for the Faq entity
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@RestController
@RequestMapping("/api/faq")
public class FaqController {

    @Autowired
    private IFaqServiceImpl faqService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Faq>> getAll() {
        List<Faq> allFaq = faqService.getAll();

        if (allFaq.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }

        return ResponseEntity.ok(allFaq); // HTTP 200
    }
}
