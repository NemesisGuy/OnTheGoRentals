package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.settings.Settings;
import za.ac.cput.service.ISettingsService;

import java.util.List;

/**
 * AdminSettingsController.java
 * Controller for administrators to manage application settings.
 * Allows admins to create, retrieve, update, and delete settings entries.
 * It's assumed that settings might be keyed by an ID, with a primary settings
 * record often being ID 1.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date, if known, otherwise current date]
 */
@RestController
@RequestMapping("/api/v1/admin/settings") // Standardized path
@Tag(name = "Admin Settings Management", description = "Endpoints for administrators to manage application settings.")
public class AdminSettingsController {

    private static final Logger log = LoggerFactory.getLogger(AdminSettingsController.class);
    private final ISettingsService settingsService; // Corrected naming convention

    /**
     * Constructs an AdminSettingsController with the necessary Settings service.
     *
     * @param settingsService The service implementation for settings operations.
     */
    @Autowired
    public AdminSettingsController(ISettingsService settingsService) {
        this.settingsService = settingsService;
        log.info("AdminSettingsController initialized.");
    }

    /**
     * Retrieves all settings entries.
     *
     * @return A ResponseEntity containing a list of all Settings, or an empty list if none.
     */
    @GetMapping("/list/all")
    @Operation(summary = "Get all settings entries", description = "Retrieves a list of all settings entries in the system.")
    public ResponseEntity<List<Settings>> getAll() {
        log.info("Admin request to get all settings entries.");
        List<Settings> allSettings = settingsService.getAll(); // Service directly returns List<Settings>
        return ResponseEntity.ok(allSettings);
    }

    /**
     * Creates a new settings entry.
     *
     * @param settings The Settings object to create.
     * @return A ResponseEntity containing the created Settings object, or Bad Request if creation fails.
     */
    @PostMapping("/create")
    @Operation(summary = "Create a new settings entry", description = "Allows an administrator to create a new settings entry.")
    public ResponseEntity<Settings> createSettings(
            @Parameter(description = "Settings object to create", required = true) @RequestBody Settings settings) {
        log.info("Admin request to create new settings: {}", settings);
        Settings created = settingsService.create(settings);
        if (created == null) {
            log.error("Failed to create settings: {}", settings);
            return ResponseEntity.badRequest().build();
        }
        log.info("Successfully created settings with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves the primary application settings (assumed to be ID 1).
     *
     * @return A ResponseEntity containing the Settings object if found, or Not Found.
     */
    @GetMapping("/read")
    @Operation(summary = "Get primary settings", description = "Retrieves the primary application settings, typically identified by ID 1.")
    public ResponseEntity<Settings> getSettings() {
        final int PRIMARY_SETTINGS_ID = 1;
        log.info("Admin request to get primary settings (ID: {}).", PRIMARY_SETTINGS_ID);
        Settings settings = settingsService.read(PRIMARY_SETTINGS_ID);
        if (settings == null) {
            log.warn("Primary settings (ID: {}) not found.", PRIMARY_SETTINGS_ID);
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully retrieved primary settings (ID: {}).", PRIMARY_SETTINGS_ID);
        return ResponseEntity.ok(settings);
    }

    /**
     * Updates an existing settings entry. The ID of the settings to update
     * should be present in the request body's Settings object.
     *
     * @param settings The Settings object with updated values and the ID of the entry to update.
     * @return A ResponseEntity indicating success (200 OK) or an error if the update fails.
     */
    @PutMapping("/update")
    @Operation(summary = "Update settings entry", description = "Allows an administrator to update an existing settings entry. The ID must be provided in the settings object.")
    public ResponseEntity<Settings> updateSettings(
            @Parameter(description = "Settings object with updated values and ID", required = true) @RequestBody Settings settings) {
        log.info("Admin request to update settings for ID: {}", settings.getId());
        Settings updatedSettings = settingsService.update(settings);
        if (updatedSettings == null) {
            log.error("Failed to update settings for ID: {}. Settings might not exist or update failed.", settings.getId());
            // Consider returning 404 if settings.getId() was not found by the service.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        log.info("Settings updated successfully for ID: {}", updatedSettings.getId());
        return ResponseEntity.ok(updatedSettings);
    }

    /**
     * Deletes a settings entry by its ID.
     *
     * @param settingsId The ID of the settings entry to delete.
     * @return A ResponseEntity indicating success (200 OK) or Not Found if the entry doesn't exist.
     */
    @DeleteMapping("/delete/{settingsId}")
    @Operation(summary = "Delete settings entry", description = "Allows an administrator to delete a settings entry by its ID.")
    public ResponseEntity<Void> deleteSettings(
            @Parameter(description = "ID of the settings entry to delete", required = true) @PathVariable Integer settingsId) {
        log.info("Admin request to delete settings with ID: {}", settingsId);
        boolean deleted = settingsService.delete(settingsId);
        if (!deleted) {
            log.warn("Failed to delete settings with ID: {}. Entry not found.", settingsId);
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully deleted settings with ID: {}", settingsId);
        return ResponseEntity.ok().build();
    }
}
