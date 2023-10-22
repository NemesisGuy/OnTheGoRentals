package za.ac.cput.controllers.admin;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.settings.Settings;
import za.ac.cput.service.SettingsService;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/list/all")
    public ArrayList<Settings> getAll() {

        return (ArrayList<Settings>) settingsService.getAll();
    }
    @PostMapping("/create")
    public Settings createSettings(@RequestBody Settings settings) {
        Settings createdSettings = settingsService.create(settings);
        return createdSettings;
    }

    @GetMapping("/read")
    public Settings getSettings() {
        return settingsService.read(1);
    }

    @PutMapping("/update")
    public void setCurrency(@RequestBody Settings settings) {
        settingsService.update(settings);
        System.out.println("Settings updated");
    }
    @DeleteMapping("/delete/{settingsId}")
    public boolean deleteSettings(@PathVariable Integer settingsId) {
        System.out.println("/api/admin/settings/delete was triggered");
        System.out.println("SettingsService was created...attempting to delete settings...");
        return settingsService.delete(settingsId);
    }
}
