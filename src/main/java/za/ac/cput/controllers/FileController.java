package za.ac.cput.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.service.IFileStorageService;
import za.ac.cput.utils.SecurityUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * FileController.java
 * Controller responsible for serving static files like images from the configured storage system.
 * It provides a public endpoint to retrieve resources based on a folder and filename.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Serving", description = "Provides public endpoints for serving static files like images.")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final IFileStorageService fileStorageService;

    /**
     * Constructs the FileController with the required FileStorageService.
     *
     * @param fileStorageService The service used to load files from the active storage backend.
     */
    @Autowired
    public FileController(IFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        log.info("FileController initialized.");
    }

    /**
     * Serves a file from the storage system using a combined key.
     * This endpoint is publicly accessible to allow browsers and clients to load images and other files.
     * It includes aggressive browser caching headers for performance.
     *
     * @param folder   The sub-directory within the base storage (e.g., "cars", "selfies").
     * @param filename The name of the file to be served, including its extension.
     * @return A {@link ResponseEntity} containing the file {@link Resource} if found,
     * or a 404 Not Found status if the file does not exist.
     */
    @Operation(summary = "Serve a file by path", description = "Serves a file (e.g., an image) from the storage system. The path combines a folder and filename.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File served successfully",
                    content = @Content(mediaType = "application/octet-stream")), // Use a generic content type for binary data
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping("/{folder}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @Parameter(description = "The folder where the file is located (e.g., 'cars', 'selfies')", required = true) @PathVariable String folder,
            @Parameter(description = "The name of the file including its extension (e.g., 'image.jpg')", required = true) @PathVariable String filename) {

        String requesterId = SecurityUtils.getRequesterIdentifier();
        String key = folder + "/" + filename;
        log.info("Requester [{}]: Request received to serve file with key '{}'.", requesterId, key);

        Optional<Resource> resourceOptional = fileStorageService.loadAsResource(key);

        if (resourceOptional.isEmpty()) {
            log.warn("File not found for key: {}", key);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + filename);
        }

        Resource resource = resourceOptional.get();
        String contentType = determineContentType(filename);
        log.debug("Determined content type for key '{}' is '{}'.", key, contentType);

        // Set aggressive browser caching instructions for static assets.
        CacheControl cacheControl = CacheControl
                .maxAge(365, TimeUnit.DAYS)
                .noTransform()
                .mustRevalidate();

        log.info("Successfully serving file with key '{}' and content type '{}'.", key, contentType);

        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Determines the MIME type of a file based on its extension.
     *
     * @param filename The name of the file.
     * @return A string representing the MIME type, or a default value for unknown types.
     */
    private String determineContentType(String filename) {
        if (filename == null || filename.isBlank()) {
            return "application/octet-stream";
        }
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";
        // Default fallback for any other file type.
        return "application/octet-stream";
    }
}