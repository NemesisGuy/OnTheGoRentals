package za.ac.cput.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Faq;
import za.ac.cput.service.impl.IFaqServiceImpl;

import java.util.ArrayList;

/**
 * AdminFaqController.java
 * This is the controller for the Admin Faq entity
 * Author: Aqeel Hanslo (219374422)
 * Date: 08 August 2023
 */

@RestController
@RequestMapping("/api/admin/faq")
public class AdminFaqController {
    @Autowired
    private IFaqServiceImpl faqService;

    @PostMapping("/create")
    public ResponseEntity<Faq> createFaq(@RequestBody Faq faq) {
        Faq created = faqService.create(faq);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<Faq> readFaq(@PathVariable int id) {
        Faq faq = faqService.read(id);
        if (faq == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faq);
    }

    @PostMapping("/update")
    public ResponseEntity<Faq> updateFaq(@RequestBody Faq faq) {
        Faq updated = faqService.update(faq);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = faqService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<ArrayList<Faq>> getAll() {
        ArrayList<Faq> allFaq = new ArrayList<>(faqService.getAll());
        return ResponseEntity.ok(allFaq);
    }
}
