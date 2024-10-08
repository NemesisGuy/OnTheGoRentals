package za.ac.cput.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.service.impl.IHelpCenterServiceImpl;

import java.util.ArrayList;

/**
 * AdminHelpCenterController.java
 * This is the controller for the Admin Help Center entity
 * Author: Aqeel Hanslo (219374422)
 * Date: 08 August 2023
 */

@RestController
@RequestMapping("/api/admin/help-center")
public class AdminHelpCenterController {
    @Autowired
    private IHelpCenterServiceImpl helpCenterService;

    @PostMapping("/create")
    public HelpCenter create(@RequestBody HelpCenter helpCenter) {
        return helpCenterService.create(helpCenter);
    }

    @GetMapping("/read/{id}")
    public HelpCenter read(@PathVariable int id) {
        HelpCenter helpCenter = helpCenterService.read(id);
        return helpCenter;
    }

    @PostMapping("/update")
    public HelpCenter update(@RequestBody HelpCenter helpCenter) {
        return helpCenterService.update(helpCenter);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id) {

        return helpCenterService.delete(id);
    }

    @GetMapping("/get-all")
    public ArrayList<HelpCenter> getAll() {
        ArrayList<HelpCenter> helpCenterList = new ArrayList<>(helpCenterService.getAll());
        return helpCenterList;
    }
}
