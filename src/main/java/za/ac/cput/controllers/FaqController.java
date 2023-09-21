package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.Faq;
import za.ac.cput.service.impl.IFaqServiceImpl;

import java.util.ArrayList;

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

    @GetMapping("/get-all")//lets drop the dashes and get in the url look at my examples please
    public ArrayList<Faq> getAll() {
        ArrayList<Faq> allFaq = new ArrayList<>(faqService.getAll());
        return allFaq;
    }
}
