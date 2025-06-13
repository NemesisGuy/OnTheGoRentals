package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.api.response.ApiResponseWrapper;
import za.ac.cput.service.IStorageManagementService; // <-- Import the new management service

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/data-tools")
@Tag(name = "Admin Data Tools", description = "Endpoints for administrators to manage data integrity and file system operations.")
@SecurityRequirement(name = "bearerAuth")
public class AdminDataToolsController {

    private static final Logger log = LoggerFactory.getLogger(AdminDataToolsController.class);

    // Inject the new Storage Management Service
    private final IStorageManagementService storageManagementService;

    @Autowired
    public AdminDataToolsController(IStorageManagementService storageManagementService) {
        // We inject the management service, which will in turn use the core file storage service.
        this.storageManagementService = storageManagementService;
    }

    @Operation(
            summary = "Get file system statistics",
            description = "Retrieves statistics about the file system usage including file counts and total size. Note: This is only fully supported for local storage profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    @GetMapping("/files/stats")
    public ResponseEntity<Map<String, Object>> getFileSystemStats() {
        log.info("Admin request for file system stats.");
        // Call the method on the correct service
        Map<String, Object> stats = storageManagementService.getFileSystemStats();
        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "Find orphaned files",
            description = "Identifies files in the file system that are not associated with any database records. Note: This is only fully supported for local storage profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orphaned files found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @GetMapping("/files/orphaned")
    public ResponseEntity<ApiResponseWrapper<Map<String, List<String>>>> findOrphanedFiles() {
        log.info("Admin request to find all orphaned files.");
        // Call the method on the correct service
        Map<String, List<String>> orphanedFiles = storageManagementService.findOrphanedFiles();
        return ResponseEntity.ok(new ApiResponseWrapper<>(orphanedFiles));
    }

    @Operation(
            summary = "Find broken image links",
            description = "Identifies database records that reference non-existent image files."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Broken image links found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @GetMapping("/associations/broken-image-links")
    public ResponseEntity<ApiResponseWrapper<List<Object>>> findBrokenImageLinks() {
        log.info("Admin request to find all broken CarImage links.");
        // Call the method on the correct service
        List<Object> brokenImageLinks = storageManagementService.findBrokenImageLinks();
        return ResponseEntity.ok(new ApiResponseWrapper<>(brokenImageLinks));
    }

    @Operation(
            summary = "Delete physical file",
            description = "Permanently deletes a file from the underlying storage system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deletion initiated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @DeleteMapping("/files/{folder}/{filename:.+}")
    public ResponseEntity<ApiResponseWrapper<String>> deletePhysicalFile(
            @Parameter(description = "Folder containing the file") @PathVariable String folder,
            @Parameter(description = "Name of the file to delete") @PathVariable String filename) {
        log.warn("ADMIN ACTION: Request to permanently delete physical file: {}/{}", folder, filename);

        // Call the method on the correct service
        boolean deleted = storageManagementService.deletePhysicalFile(folder, filename);

        if (deleted) {
            String successMessage = "Successfully deleted file: " + filename;
            return ResponseEntity.ok(new ApiResponseWrapper<>(successMessage));
        } else {
            // It's better to return a specific error response if deletion fails
            String errorMessage = "Failed to delete file: " + filename + ". It may not exist or an error occurred.";
            // Using a generic server error, but you could use a more specific status
            return ResponseEntity.status(500).body(new ApiResponseWrapper<>(errorMessage));
        }
    }

    @Operation(
            summary = "Delete car image association",
            description = "Deletes a car image database record, typically one with a broken file reference."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car image association deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @DeleteMapping("/associations/car-image/{imageUuid}")
    public ResponseEntity<ApiResponseWrapper<String>> deleteBrokenCarImageAssociation(
            @Parameter(description = "UUID of the car image record to delete") @PathVariable UUID imageUuid) {
        log.warn("ADMIN ACTION: Request to delete CarImage record with UUID: {}", imageUuid);

        // Call the method on the correct service
        storageManagementService.deleteCarImageAssociation(imageUuid);

        String successMessage = "Successfully deleted CarImage record: " + imageUuid;
        return ResponseEntity.ok(new ApiResponseWrapper<>(successMessage));
    }

    @Operation(
            summary = "Get storage usage chart data",
            description = "Retrieves storage usage statistics per folder for chart visualization. Note: This is only fully supported for local storage profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage usage data retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    @GetMapping("/files/usage-chart")
    public ResponseEntity<Map<String, Long>> getStorageUsageChartData() {
        log.info("Admin request for storage usage chart data.");
        // Call the method on the correct service
        Map<String, Long> usageData = storageManagementService.getStorageUsagePerFolder();
        return ResponseEntity.ok(usageData);
    }
}