package za.ac.cput.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.service.IFileStorageService;
import za.ac.cput.utils.SecurityUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/files")
@Api(value = "File Serving", tags = "File Serving", description = "Provides endpoints for serving static files like images.")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final IFileStorageService fileStorageService;

    @Autowired
    public FileController(IFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        log.info("FileController initialized.");
    }

    /**
     * Serves a file from the storage system using a combined key.
     * This endpoint is designed to be publicly accessible to allow browsers to load images.
     *
     * @param folder   The sub-directory within the base storage (e.g., "cars", "selfies").
     * @param filename The name of the file to be served, including its extension.
     * @return A {@link ResponseEntity} containing the file {@link Resource} if found,
     * or a 404 Not Found status if the file does not exist.
     */
    @GetMapping("/{folder}/{filename:.+}")
    @ApiOperation(value = "Serve a file",
            notes = "Serves a file (e.g., image) from the storage system. The path combines a folder and filename.",
            response = Resource.class)
    public ResponseEntity<Resource> serveFile(
            @ApiParam(value = "The folder where the file is located (e.g., 'cars', 'selfies')", required = true) @PathVariable String folder,
            @ApiParam(value = "The name of the file including its extension (e.g., 'image.jpg')", required = true) @PathVariable String filename) {
        String requesterId = SecurityUtils.getRequesterIdentifier();

        // --- THE FIX IS HERE ---
        // 1. Combine folder and filename into a single key.
        String key = folder + "/" + filename;
        log.info("Requester [{}]: Request received to serve file with key '{}'.", requesterId, key);

        // 2. Use the single key to load the resource, which now returns an Optional.
        Optional<Resource> resourceOptional = fileStorageService.loadAsResource(key);

        if (resourceOptional.isEmpty()) {
            // 3. Handle the case where the file is not found.
            log.warn("File not found for key: {}", key);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + filename);
        }

        Resource resource = resourceOptional.get();

        // 4. Determine content type from filename as a robust fallback for all storage types.
        String contentType = determineContentType(filename);
        log.debug("Determined content type for key '{}' is '{}'.", key, contentType);

        // Set browser caching instructions.
        CacheControl cacheControl = CacheControl
                .maxAge(365, TimeUnit.DAYS)
                .noTransform()
                .mustRevalidate();

        log.info("Successfully serving file with key '{}' and content type '{}'.", key, contentType);

        // Build and return the successful response.
        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Determines the MIME type of a file based on its extension.
     * @param filename The name of the file.
     * @return A string representing the MIME type, or a default value.
     */
    private String determineContentType(String filename) {
        if (filename == null || filename.isBlank()) {
            return "application/octet-stream";
        }
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}