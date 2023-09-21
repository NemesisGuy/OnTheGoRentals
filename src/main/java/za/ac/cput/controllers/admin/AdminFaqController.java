package za.ac.cput.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Faq createFaq(@RequestBody Faq faq) {
        return faqService.create(faq);
    }

    @GetMapping("/read/{id}")
    public Faq readFaq(@PathVariable int id) {
        return faqService.read(id);
    }

    @PostMapping("/update")
    public Faq updateFaq(@RequestBody Faq faq) {
        return faqService.update(faq);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id) {
        return faqService.delete(id);
    }

    @GetMapping("/get-all")//lets drop the dashes and the word get in the url look at my examples please
    public ArrayList<Faq> getAll() {
        ArrayList<Faq> allFaq = new ArrayList<>(faqService.getAll());
        return allFaq;
    }
}
