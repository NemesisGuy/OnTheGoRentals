package za.ac.cput.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<HelpCenter> create(@RequestBody HelpCenter helpCenter) {
        HelpCenter created = helpCenterService.create(helpCenter);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<HelpCenter> read(@PathVariable int id) {
        HelpCenter helpCenter = helpCenterService.read(id);
        if (helpCenter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(helpCenter);
    }

    @PostMapping("/update")
    public ResponseEntity<HelpCenter> update(@RequestBody HelpCenter helpCenter) {
        HelpCenter updated = helpCenterService.update(helpCenter);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = helpCenterService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<ArrayList<HelpCenter>> getAll() {
        ArrayList<HelpCenter> helpCenterList = new ArrayList<>(helpCenterService.getAll());
        return ResponseEntity.ok(helpCenterList);
    }
}
