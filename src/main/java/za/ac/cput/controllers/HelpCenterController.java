package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.service.impl.IHelpCenterServiceImpl;

import java.util.ArrayList;

/**
 * HelpCenterController.java
 * This is the controller for the Help Center entity
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@RestController
@RequestMapping("/api/help-center")
public class HelpCenterController {
    @Autowired
    private IHelpCenterServiceImpl helpCenterService;

    @GetMapping("/get-all") //lets drop the dashes in the url
    public ArrayList<HelpCenter> getAll() {
        ArrayList<HelpCenter> helpCenterList = new ArrayList<>(helpCenterService.getAll());
        return helpCenterList;
    }

    @GetMapping("/get-all-by-category/{category}")//lets drop the dashes in the url look at my examples please
    public ArrayList<HelpCenter> getAllByCategory(@PathVariable String category) {
        return helpCenterService.getAllByCategory(category);
    }
}
