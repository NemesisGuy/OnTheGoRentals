package za.ac.cput.controllers.admin;
/**
 * Author: Peter Buckingham (220165289)
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.settings.Settings;
import za.ac.cput.service.SettingsService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/list/all")
    public ResponseEntity<List<Settings>> getAll() {
        List<Settings> allSettings = (List<Settings>) settingsService.getAll();
        return ResponseEntity.ok(allSettings);
    }

    @PostMapping("/create")
    public ResponseEntity<Settings> createSettings(@RequestBody Settings settings) {
        Settings created = settingsService.create(settings);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(created);
    }

    @GetMapping("/read")
    public ResponseEntity<Settings> getSettings() {
        Settings settings = settingsService.read(1);
        if (settings == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateSettings(@RequestBody Settings settings) {
        settingsService.update(settings);
        System.out.println("Settings updated");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{settingsId}")
    public ResponseEntity<Void> deleteSettings(@PathVariable Integer settingsId) {
        boolean deleted = settingsService.delete(settingsId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("/api/admin/settings/delete was triggered");
        System.out.println("SettingsService was created...attempting to delete settings...");
        return ResponseEntity.ok().build();
    }
}
