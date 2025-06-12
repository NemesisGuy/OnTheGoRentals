package za.ac.cput.controllers.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.api.response.ApiResponse;
import za.ac.cput.service.IDataIntegrityService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/data-tools")
public class AdminDataToolsController {

    private static final Logger log = LoggerFactory.getLogger(AdminDataToolsController.class);
    private final IDataIntegrityService dataIntegrityService;

    @Autowired
    public AdminDataToolsController(IDataIntegrityService dataIntegrityService) {
        this.dataIntegrityService = dataIntegrityService;
    }

    /**
     * Retrieves statistics about the file system usage.
     *
     * @return A map containing file counts and total size.
     */
    @GetMapping("/files/stats")
    public ResponseEntity<Map<String, Object>> getFileSystemStats() {
        log.info("Admin request for file system stats.");
        Map<String, Object> stats = dataIntegrityService.getFileSystemStats();
        return ResponseEntity.ok(stats);
    }

    // These GET methods now must also return a full ApiResponse
    @GetMapping("/files/orphaned")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> findOrphanedFiles() {
        log.info("Admin request to find all orphaned files.");
        Map<String, List<String>> orphanedFiles = dataIntegrityService.findOrphanedFiles();
        return ResponseEntity.ok(new ApiResponse<>(orphanedFiles));
    }

    @GetMapping("/associations/broken-image-links")
    public ResponseEntity<ApiResponse<List<Object>>> findBrokenImageLinks() {
        log.info("Admin request to find all broken CarImage links.");
        List<Object> brokenImageLinks = dataIntegrityService.findBrokenImageLinks();
        return ResponseEntity.ok(new ApiResponse<>(brokenImageLinks));
    }

    /**
     * **THE FIX IS HERE**
     * This method now manually constructs the full response, as it is no longer
     * being processed by the wrapper advice.
     */
    @DeleteMapping("/files/{folder}/{filename:.+}")
    public ResponseEntity<ApiResponse<String>> deletePhysicalFile(@PathVariable String folder, @PathVariable String filename) {
        log.warn("ADMIN ACTION: Request to permanently delete physical file: {}/{}", folder, filename);
        dataIntegrityService.deletePhysicalFile(folder, filename);
        String successMessage = "Successfully deleted file: " + filename;
        return ResponseEntity.ok(new ApiResponse<>(successMessage));
    }

    /**
     * **THE FIX IS HERE**
     * This method also now manually constructs the full response.
     */
    @DeleteMapping("/associations/car-image/{imageUuid}")
    public ResponseEntity<ApiResponse<String>> deleteBrokenCarImageAssociation(@PathVariable UUID imageUuid) {
        log.warn("ADMIN ACTION: Request to delete CarImage record with UUID: {}", imageUuid);
        dataIntegrityService.deleteCarImageAssociation(imageUuid);
        String successMessage = "Successfully deleted CarImage record: " + imageUuid;
        return ResponseEntity.ok(new ApiResponse<>(successMessage));
    }


    /**
     * Retrieves storage usage statistics per folder for chart visualization.
     *
     * @return A map of folder names to their total size in bytes.
     */
    @GetMapping("/files/usage-chart")
    public ResponseEntity<Map<String, Long>> getStorageUsageChartData() {
        log.info("Admin request for storage usage chart data.");
        Map<String, Long> usageData = dataIntegrityService.getStorageUsagePerFolder();
        return ResponseEntity.ok(usageData);
    }
}