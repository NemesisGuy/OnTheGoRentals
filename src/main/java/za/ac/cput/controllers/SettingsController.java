package za.ac.cput.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.entity.settings.Settings;
import za.ac.cput.service.ISettingsService;
import za.ac.cput.utils.SecurityUtils;

/**
 * SettingsController.java
 * Controller for retrieving application settings.
 * Currently provides an endpoint to read a specific settings entry (e.g., by ID 1).
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - If known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/settings") // Standardized API path
public class SettingsController {

    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private final ISettingsService settingsService; // Use interface

    /**
     * Constructs a SettingsController with the necessary Settings service.
     *
     * @param settingsService The service implementation for settings operations.
     */
    @Autowired
    public SettingsController(ISettingsService settingsService) {
        this.settingsService = settingsService;
        log.info("SettingsController initialized.");
    }

    /**
     * Retrieves the application settings.
     * Assumes there's a single settings entry identified by a fixed ID (e.g., 1).
     * This endpoint is typically public or accessible to authenticated users.
     *
     * @return A ResponseEntity containing the {@link Settings} object if found, or 404 Not Found.
     */
    @GetMapping // Changed from /read to be more RESTful for a singleton resource
    public ResponseEntity<Settings> getSettings() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        final int SETTINGS_ID = 1; // Assuming a fixed ID for the settings record
        log.info("Requester [{}]: Request to get application settings (ID: {}).", requesterId, SETTINGS_ID);

        Settings settings = settingsService.read(SETTINGS_ID);
        if (settings == null) {
            log.warn("Requester [{}]: Application settings (ID: {}) not found.", requesterId, SETTINGS_ID);
            // throw new ResourceNotFoundException("Application settings not found with ID: " + SETTINGS_ID);
            return ResponseEntity.notFound().build(); // Return 404 if settings are not configured
        }

        log.info("Requester [{}]: Successfully retrieved application settings (ID: {}).", requesterId, SETTINGS_ID);
        return ResponseEntity.ok(settings);
    }

    // If settings can be updated, you would add a PUT endpoint:
    /*
    @PutMapping
    // @PreAuthorize("hasRole('ADMIN')") // Typically admin-only
    public ResponseEntity<Settings> updateSettings(@RequestBody Settings newSettings) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        final int SETTINGS_ID = 1;
        log.info("Requester [{}]: Attempting to update application settings (ID: {}) with: {}", requesterId, SETTINGS_ID, newSettings);
        // Ensure newSettings doesn't try to change the ID if it's fixed.
        newSettings.setId(SETTINGS_ID); // Or however your Settings entity manages its ID
        Settings updatedSettings = settingsService.update(newSettings); // Assuming service.update(Settings)
        log.info("Requester [{}]: Successfully updated application settings (ID: {}).", requesterId, SETTINGS_ID);
        return ResponseEntity.ok(updatedSettings);
    }
    */
}