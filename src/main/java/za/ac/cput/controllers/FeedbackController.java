package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Feedback;
import za.ac.cput.service.IFeedbackService;

import java.util.List;

/**
 * FeedbackController.java
 * Controller for managing Feedback entities.
 * Author: Peter Buckingham
 * Date: 2025-05-15
 */

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:8080") // Vue.js frontend
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> create(@RequestBody Feedback feedback) {
        Feedback created = feedbackService.create(feedback);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> read(@PathVariable Integer id) {
        Feedback feedback = feedbackService.read(id);
        if (feedback == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feedback);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> update(@PathVariable Integer id, @RequestBody Feedback feedback) {

        Feedback updated = feedbackService.update(feedback);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = feedbackService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAll() {
        List<Feedback> feedbackList = feedbackService.getAll();
        if (feedbackList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(feedbackList);
    }
}
