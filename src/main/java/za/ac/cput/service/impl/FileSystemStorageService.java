package za.ac.cput.service.impl;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.service.FileStorageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

/**
 * FileSystemStorageService.java
 *
 * An implementation of the {@link FileStorageService} that stores files on the local file system.
 * This service is designed with security as a primary concern, providing robust protection against
 * path traversal attacks and ensuring only allowed file types are stored.
 *
 * It handles the creation of the storage directory on startup and provides methods to
 * save, load, and delete files within designated sub-folders.
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@Service
public class FileSystemStorageService implements FileStorageService {

    /**
     * Logger for this service. Used to record operational events, warnings, and errors.
     */
    private static final Logger log = LoggerFactory.getLogger(FileSystemStorageService.class);

    /**
     * A strict whitelist of allowed MIME types for file uploads.
     * This is a critical security measure to prevent the upload of potentially executable
     * or malicious files (e.g., .jsp, .html, .js).
     */
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );

    /**
     * The base directory for file storage, configurable in `application.properties`.
     * Defaults to "uploads" if not specified.
     */
    @Value("${app.storage.base-dir:uploads}")
    private String baseDir;

    /**
     * The resolved, absolute path of the base storage directory.
     * All file operations are constrained to this path.
     */
    private Path storageBasePath;

    /**
     * Initializes the service after construction.
     * This method resolves the absolute path of the storage directory and creates it
     * if it does not already exist. It is run once when the Spring application starts.
     */
    @PostConstruct
    public void init() {
        storageBasePath = Paths.get(baseDir).toAbsolutePath().normalize();
        log.info("File storage base directory initialized at: {}", storageBasePath);
        try {
            Files.createDirectories(storageBasePath);
        } catch (IOException e) {
            log.error("Could not create base storage directory at: {}", storageBasePath, e);
            throw new RuntimeException("Could not create base storage directory", e);
        }
    }

    /**
     * Saves a file to a specified sub-folder within the storage directory.
     * This method performs multiple layers of validation:
     * 1. Checks for empty files or invalid folder names.
     * 2. Validates the file's MIME type against a strict whitelist.
     * 3. Prevents path traversal attacks by ensuring the final save location is within the base storage directory.
     *
     * @param folder The sub-folder to save the file in (e.g., "cars", "selfies").
     * @param file   The MultipartFile to be saved.
     * @return The unique, randomly generated filename of the saved file.
     * @throws RuntimeException if the file is empty or an IO error occurs.
     * @throws SecurityException if a path traversal attempt is detected or the file type is disallowed.
     */
    @Override
    public String save(String folder, MultipartFile file) {
        // --- 1. Proactive Input Validation ---
        if (file.getOriginalFilename() == null) {
            log.warn("Security Warning: File has no original filename. Cannot proceed with storage.");
            throw new SecurityException("File must have a valid name.");
        }
        if (file.isEmpty()) {
            log.warn("Attempted to save an empty file.");
            throw new RuntimeException("Failed to store empty file.");
        }
        if (folder == null || folder.isBlank() || folder.contains("..") || folder.contains("/") || folder.contains("\\")) {
            log.warn("Security Warning: Folder name is invalid or contains path traversal sequences: '{}'", folder);
            throw new SecurityException("Folder name contains invalid characters or path sequences.");
        }

        // --- 2. MIME Type Validation (Critical Security Check) ---
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Security Warning: Disallowed file type uploaded. Provided Content-Type: '{}'", contentType);
            throw new SecurityException("File type not allowed. Please upload a valid image (JPEG, PNG, GIF).");
        }
        log.info("File type '{}' is allowed. Proceeding with storage.", contentType);


        // --- 3. Prepare Secure Filename ---
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID() + (extension != null ? "." + extension : "");

        try {
            // --- 4. Path Resolution and Traversal Check (Critical Security Check) ---
            Path targetFolder = this.storageBasePath.resolve(folder).normalize();

            if (!targetFolder.startsWith(this.storageBasePath)) {
                log.error("CRITICAL: Path Traversal Attack Detected! Input folder '{}' resolved outside base directory.", folder);
                throw new SecurityException("Cannot store file outside designated directory.");
            }

            // --- 5. Save the File ---
            Files.createDirectories(targetFolder);
            Path targetFile = targetFolder.resolve(filename);

            log.info("Saving file to: {}", targetFile);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return filename;

        } catch (IOException e) {
            log.error("Failed to store file {} in folder {}: {}", filename, folder, e.getMessage(), e);
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    /**
     * Loads a file as a resource from a specified sub-folder.
     * Includes security checks to prevent reading files outside the storage directory.
     *
     * @param folder   The sub-folder where the file is located.
     * @param filename The name of the file to load.
     * @return The file as a readable {@link Resource}.
     * @throws RuntimeException if the file is not found or cannot be read.
     * @throws SecurityException if a path traversal attempt is detected.
     */
    @Override
    public Resource load(String folder, String filename) {
        try {
            log.debug("Attempting to load file: {}/{}", folder, filename);
            Path filePath = this.storageBasePath.resolve(folder).resolve(filename).normalize();

            if (!filePath.startsWith(this.storageBasePath)) {
                log.warn("Security Alert: Illegal file access attempt for path: {}", filePath);
                throw new SecurityException("Illegal file access attempt");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                log.info("Successfully loaded file: {}", filePath);
                return resource;
            } else {
                log.warn("File not found or is not readable: {}", filePath);
                throw new RuntimeException("File not found or unreadable: " + filename);
            }
        } catch (MalformedURLException e) {
            log.error("Could not load file due to malformed URL for filename: {}", filename, e);
            throw new RuntimeException("Invalid file path", e);
        }
    }

    /**
     * Deletes a file from a specified sub-folder.
     * Includes security checks to prevent deleting files outside the storage directory.
     *
     * @param folder   The sub-folder where the file is located.
     * @param filename The name of the file to delete.
     * @throws RuntimeException if an IO error occurs.
     * @throws SecurityException if a path traversal attempt is detected.
     */
    @Override
    public void delete(String folder, String filename) {
        try {
            log.debug("Attempting to delete file: {}/{}", folder, filename);
            Path filePath = this.storageBasePath.resolve(folder).resolve(filename).normalize();

            if (!filePath.startsWith(this.storageBasePath)) {
                log.warn("Security Alert: Illegal file deletion attempt for path: {}", filePath);
                throw new SecurityException("Illegal file access attempt for deletion");
            }

            Files.deleteIfExists(filePath);
            log.info("Successfully deleted file: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file {} from folder {}: {}", filename, folder, e.getMessage(), e);
            throw new RuntimeException("Failed to delete file: " + filename, e);
        }
    }
}