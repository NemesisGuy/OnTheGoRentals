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
 * An implementation of the FileStorageService that stores files on the local file system.
 * This service is designed with security as a primary concern, providing robust protection against
 * path traversal attacks and ensuring only allowed file types are stored.
 *
 * @author Peter Buckingham
 * @version 2.1
 */
@Service
public class FileSystemStorageService implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileSystemStorageService.class);

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );

    @Value("${app.storage.base-dir:uploads}")
    private String baseDir;

    private Path storageBasePath;

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

    @Override
    public String save(String folder, MultipartFile file) {
        // --- Input Validation ---
        if (file.getOriginalFilename() == null) {
            throw new SecurityException("File must have a valid name.");
        }
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }
        if (folder == null || folder.isBlank() || folder.contains("..") || folder.contains("/") || folder.contains("\\")) {
            throw new SecurityException("Folder name contains invalid characters or path sequences.");
        }

        // --- MIME Type Validation ---
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Security Warning: Disallowed file type uploaded. Provided Content-Type: '{}'", contentType);
            throw new SecurityException("File type not allowed. Please upload a valid image (JPEG, PNG, GIF).");
        }
        log.info("File type '{}' is allowed. Proceeding with storage.", contentType);

        // --- Prepare Secure Filename ---
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID() + (extension != null ? "." + extension : "");

        try {
            // --- Path Resolution and Traversal Check ---
            Path targetFolder = this.storageBasePath.resolve(folder).normalize();
            if (!targetFolder.startsWith(this.storageBasePath)) {
                log.error("CRITICAL: Path Traversal Attack Detected! Input folder '{}' resolved outside base directory.", folder);
                throw new SecurityException("Cannot store file outside designated directory.");
            }

            // --- Save the File ---
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
     * Checks if a file exists in a specified sub-folder.
     * This method is safe against path traversal attacks.
     *
     * @param folder   The sub-folder where the file is located.
     * @param filename The name of the file to check.
     * @return true if the file exists and is a regular file, false otherwise.
     */
    @Override
    public boolean fileExists(String folder, String filename) {
        // It's good practice to validate inputs even in internal-facing methods.
        if (folder == null || filename == null || folder.isBlank() || filename.isBlank()) {
            return false;
        }
        try {
            Path filePath = this.storageBasePath.resolve(folder).resolve(filename).normalize();

            // Security check: ensure the path is still within the base storage directory.
            if (!filePath.startsWith(this.storageBasePath)) {
                log.warn("Security Alert: Illegal file existence check for path: {}", filePath);
                return false; // Treat attempts to access outside the dir as "not found"
            }
            // Use Files.isRegularFile to ensure it's a file and not a directory.
            return Files.isRegularFile(filePath);
        } catch (InvalidPathException e) {
            log.error("Invalid path provided for file existence check. Folder: '{}', Filename: '{}'", folder, filename, e);
            return false;
        }
    }

    /**
     * Deletes a file from a specified sub-folder.
     * This method is safe against path traversal attacks and will not throw an error
     * if the file does not exist.
     *
     * @param folder   The sub-folder where the file is located.
     * @param filename The name of the file to delete.
     * @return true if the file was successfully deleted, false otherwise.
     */
    @Override
    public boolean delete(String folder, String filename) {
        // Validate inputs to prevent errors.
        if (folder == null || filename == null || folder.isBlank() || filename.isBlank()) {
            log.warn("Attempted to delete file with invalid folder or filename.");
            return false;
        }
        try {
            Path filePath = this.storageBasePath.resolve(folder).resolve(filename).normalize();

            // Security check is paramount for delete operations.
            if (!filePath.startsWith(this.storageBasePath)) {
                log.warn("Security Alert: Illegal file deletion attempt for path: {}", filePath);
                return false; // Do not proceed.
            }

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error occurred while trying to delete file {}/{}: {}", folder, filename, e.getMessage(), e);
            // In a production scenario, you might want to alert an admin here.
            return false;
        }
    }
}