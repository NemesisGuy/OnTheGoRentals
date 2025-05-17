package za.ac.cput.controllers;
/**
 * Author: Peter Buckingham (220165289)
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.settings.Settings;
import za.ac.cput.service.SettingsService;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/read")
    public Settings getSettings() {
        return settingsService.read(1);
    }

}
