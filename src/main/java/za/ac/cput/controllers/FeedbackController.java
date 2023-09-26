package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Feedback;
import za.ac.cput.service.IFeedbackService;

import java.util.List;

@RestController
@RequestMapping("/feedback") // no api? look at my examples please!
@CrossOrigin(origins = "http://localhost:8080")
// Allow requests from your Vue.js frontend's domain *****this is a lie*****
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

    @GetMapping("/getAll")////lets drop the get in the url look at my examples please
    public List<Feedback> getAll() {
        return feedbackService.getAll();
    }
}
