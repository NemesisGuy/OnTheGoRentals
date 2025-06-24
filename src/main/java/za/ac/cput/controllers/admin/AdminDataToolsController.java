package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.api.response.ApiResponseWrapper;
import za.ac.cput.service.IStorageManagementService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AdminDataToolsController.java
 * Controller for administrative tools related to data integrity and file storage management.
 * Provides endpoints for finding orphaned files, broken links, and viewing storage statistics.
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/data-tools")
@Tag(name = "Admin: Data Tools", description = "Endpoints for administrators to manage data integrity and file storage operations.")
@SecurityRequirement(name = "bearerAuth")
public class AdminDataToolsController {

    private static final Logger log = LoggerFactory.getLogger(AdminDataToolsController.class);

    private final IStorageManagementService storageManagementService;

    /**
     * Constructs the controller with the required storage management service.
     *
     * @param storageManagementService The service for handling high-level storage and data integrity tasks.
     */
    @Autowired
    public AdminDataToolsController(IStorageManagementService storageManagementService) {
        this.storageManagementService = storageManagementService;
        log.info("AdminDataToolsController initialized.");
    }

    /**
     * Retrieves statistics about the file storage system.
     * Note: This operation can be slow on cloud storage backends.
     *
     * @return A map containing file counts and total size.
     */
    @Operation(summary = "Get file storage statistics", description = "Retrieves statistics about file storage, including total file count and size. WARNING: This can be a slow operation on cloud storage.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"))
    @GetMapping("/files/stats")
    public ResponseEntity<Map<String, Object>> getFileSystemStats() {
        log.info("Admin request for file system stats.");
        Map<String, Object> stats = storageManagementService.getFileSystemStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Retrieves storage usage statistics per folder for chart visualization.
     * Note: This operation can be slow on cloud storage backends.
     *
     * @return A map of folder names to their total size in bytes.
     */
    @Operation(summary = "Get storage usage per folder", description = "Retrieves storage usage statistics per folder for chart visualization. WARNING: This can be a slow operation on cloud storage.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Storage usage data retrieved successfully"))
    @GetMapping("/files/usage-chart")
    public ResponseEntity<Map<String, Long>> getStorageUsageChartData() {
        log.info("Admin request for storage usage chart data.");
        Map<String, Long> usageData = storageManagementService.getStorageUsagePerFolder();
        return ResponseEntity.ok(usageData);
    }

    /**
     * Finds files in storage that do not have a corresponding record in the database.
     * Note: This is a high-cost operation not recommended for real-time use on cloud storage.
     *
     * @return A map where the key is the directory and the value is a list of orphaned filenames.
     */
    @Operation(summary = "Find orphaned files", description = "Identifies files in storage that are not associated with any database records. WARNING: This is a high-cost operation not recommended for real-time API use on cloud storage.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Operation completed"))
    @GetMapping("/files/orphaned")
    public ResponseEntity<ApiResponseWrapper<Map<String, List<String>>>> findOrphanedFiles() {
        log.info("Admin request to find all orphaned files.");
        Map<String, List<String>> orphanedFiles = storageManagementService.findOrphanedFiles();
        return ResponseEntity.ok(new ApiResponseWrapper<>(orphanedFiles));
    }

    /**
     * Finds CarImage records in the database that point to a non-existent file in storage.
     *
     * @return A list of objects representing the records with broken links.
     */
    @Operation(summary = "Find broken image links", description = "Identifies database records (CarImage) that reference non-existent image files.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successfully retrieved list of broken links"))
    @GetMapping("/associations/broken-image-links")
    public ResponseEntity<ApiResponseWrapper<List<Object>>> findBrokenImageLinks() {
        log.info("Admin request to find all broken CarImage links.");
        List<Object> brokenImageLinks = storageManagementService.findBrokenImageLinks();
        return ResponseEntity.ok(new ApiResponseWrapper<>(brokenImageLinks));
    }

    /**
     * Permanently deletes a physical file from the underlying storage system.
     *
     * @param folder   The directory containing the file.
     * @param filename The name of the file to delete.
     * @return A success or failure message.
     */
    @Operation(summary = "Delete physical file", description = "Permanently deletes a file from the underlying storage system (e.g., local disk or MinIO bucket).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Failed to delete the file due to an error")
    })
    @DeleteMapping("/files/{folder}/{filename:.+}")
    public ResponseEntity<ApiResponseWrapper<String>> deletePhysicalFile(
            @Parameter(description = "Folder containing the file (e.g., 'cars')", required = true) @PathVariable String folder,
            @Parameter(description = "Name of the file to delete", required = true) @PathVariable String filename) {
        log.warn("ADMIN ACTION: Request to permanently delete physical file: {}/{}", folder, filename);

        boolean deleted = storageManagementService.deletePhysicalFile(folder, filename);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponseWrapper<>("Successfully deleted file: " + filename));
        } else {
            String errorMessage = "Failed to delete file: " + filename + ". It may not exist or an error occurred.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseWrapper<>(errorMessage));
        }
    }

    /**
     * Deletes a car image database association, typically one identified as having a broken link.
     *
     * @param imageUuid The UUID of the CarImage database record to delete.
     * @return A success message.
     */
    @Operation(summary = "Delete car image association", description = "Deletes a car image database record, typically one identified via the 'broken-image-links' endpoint.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Car image association deleted successfully"))
    @DeleteMapping("/associations/car-image/{imageUuid}")
    public ResponseEntity<ApiResponseWrapper<String>> deleteBrokenCarImageAssociation(
            @Parameter(description = "UUID of the car image record to delete", required = true) @PathVariable UUID imageUuid) {
        log.warn("ADMIN ACTION: Request to delete CarImage record with UUID: {}", imageUuid);

        storageManagementService.deleteCarImageAssociation(imageUuid);
        return ResponseEntity.ok(new ApiResponseWrapper<>("Successfully deleted CarImage record: " + imageUuid));
    }
}