package za.ac.cput.controllers;

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
import za.ac.cput.service.FileStorageService;
import za.ac.cput.utils.SecurityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * FileController.java
 * Controller responsible for serving static files like images from the file system.
 * It provides a public endpoint to retrieve resources based on a folder and filename.
 *
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final FileStorageService fileStorageService;

    /**
     * Constructs the FileController with the required FileStorageService.
     *
     * @param fileStorageService The service used to load files from storage.
     */
    @Autowired
    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        log.info("FileController initialized.");
    }

    /**
     * Serves a file from the specified folder.
     * This endpoint is designed to be publicly accessible to allow browsers to load images.
     *
     * @param folder   The sub-directory within the base storage directory (e.g., "cars", "selfies").
     * @param filename The name of the file to be served, including its extension.
     * @return A {@link ResponseEntity} containing the file {@link Resource} if found,
     *         or a 404 Not Found status if the file does not exist or cannot be read.
     */
    @GetMapping("/{folder}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String folder, @PathVariable String filename) {
        // Log who is making the request for better context, especially if debugging security issues.
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request received to serve file '{}' from folder '{}'.", requesterId, filename, folder);

        try {
            // Attempt to load the file using the storage service
            Resource resource = fileStorageService.load(folder, filename);
            log.debug("File resource loaded successfully from service for: {}/{}", folder, filename);

            Path path = resource.getFile().toPath();

            // Probe the file to determine its MIME type (e.g., "image/jpeg")
            String contentType = Files.probeContentType(path);

            if (contentType == null) {
                log.warn("Could not determine content type for file: {}. Falling back to octet-stream.", filename);
                contentType = "application/octet-stream"; // Default fallback MIME type
            } else {
                log.debug("Determined content type for '{}' is '{}'.", filename, contentType);
            }
            //Add caching instructions for the browser.**
            CacheControl cacheControl = CacheControl
                    .maxAge(365, TimeUnit.DAYS) // Tell the browser to cache this for 1 year
                    .noTransform()
                    .mustRevalidate();


            log.info("Successfully serving file '{}' with content type '{}' with Cache-Control header.", filename, contentType);

            // Build and return the successful response with the file resource
            return ResponseEntity.ok()
                    .cacheControl(cacheControl) // <-- Setting the caching header
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"") // "inline" tells browser to display it
                    .body(resource);

        } catch (RuntimeException | IOException e) {
            // This block catches exceptions from fileStorageService.load() (e.g., file not found)
            // or from Files.probeContentType().
            log.error("Error serving file {}/{}: {}", folder, filename, e.getMessage(), e);

            // Throw a ResponseStatusException, which Spring Boot will handle and convert
            // into a proper HTTP 404 response with the given message.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or could not be read: " + filename, e);
        }
    }
}