package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Feedback;
import za.ac.cput.service.IFeedbackService;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@CrossOrigin(origins = "http://localhost:8080") // Allow requests from your Vue.js frontend's domain
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    @PostMapping("/create")
    public Feedback create(@RequestBody Feedback feedback) {
        return feedbackService.create(feedback);
    }

    @GetMapping("/read/{id}")
    public Feedback read(@PathVariable Integer id) {
        return feedbackService.read(id);
    }

    @PostMapping("/update/")
    public Feedback updated(@RequestBody Feedback feedback) {
        return feedbackService.update(feedback);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id) {
        return feedbackService.delete(id);
    }

    @GetMapping("/getAll")
    public List<Feedback> getAll() {
        return feedbackService.getAll();
    }
}
