//package za.ac.cput.controllers;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import za.ac.cput.domain.dto.response.SettingsResponseDTO;
//import za.ac.cput.domain.entity.settings.Settings;
//import za.ac.cput.domain.mapper.SettingsMapper;
//import za.ac.cput.service.ISettingsService;
//import za.ac.cput.utils.SecurityUtils;
//
/// **
// * SettingsController.java
// * Controller for retrieving application-wide settings.
// * This endpoint is typically public or accessible to authenticated users to configure frontend behavior.
// *
// * @author Peter Buckingham (220165289)
// * @version 2.0
// */
//@RestController
//@RequestMapping("/api/v1/settings")
//@Tag(name = "Application Settings", description = "Endpoint for retrieving public application settings.")
//public class SettingsController {
//
//    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
//    private final ISettingsService settingsService;
//
//    /**
//     * Constructs a SettingsController with the necessary Settings service.
//     *
//     * @param settingsService The service implementation for settings operations.
//     */
//    @Autowired
//    public SettingsController(ISettingsService settingsService) {
//        this.settingsService = settingsService;
//        log.info("SettingsController initialized.");
//    }
//
//    /**
//     * Retrieves the application's public settings.
//     * This implementation assumes there is a single settings entry in the database, identified by a fixed ID.
//     *
//     * @return A ResponseEntity containing the {@link SettingsResponseDTO} if found, or 404 Not Found if not configured.
//     */
//    @Operation(summary = "Get application settings", description = "Retrieves the public application settings, assuming a single configuration entry.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Settings retrieved successfully",
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingsResponseDTO.class))),
//            @ApiResponse(responseCode = "404", description = "Application settings have not been configured in the database")
//    })
//    @GetMapping
//    public ResponseEntity<SettingsResponseDTO> getSettings() {
//        String requesterId = SecurityUtils.getRequesterIdentifier();
//        final int SETTINGS_ID = 1; // Assuming a fixed ID for the singleton settings record
//        log.info("Requester [{}]: Request to get application settings (ID: {}).", requesterId, SETTINGS_ID);
//
//        Settings settings = settingsService.read(SETTINGS_ID);
//        if (settings == null) {
//            log.warn("Application settings (ID: {}) not found. The settings table might be empty.", SETTINGS_ID);
//            return ResponseEntity.notFound().build();
//        }
//
//        log.info("Requester [{}]: Successfully retrieved application settings (ID: {}).", requesterId, SETTINGS_ID);
//        return ResponseEntity.ok(SettingsMapper.toDto(settings));
//    }
//
//    // Note: An admin-level endpoint for updating settings would typically be in a separate AdminSettingsController
//    // and would accept a SettingsUpdateDTO.
//}