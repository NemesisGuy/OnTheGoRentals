package za.ac.cput.service.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.service.FileStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.ac.cput.exception.StorageException; // Assuming this exception exists

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
// Removed java.util.UUID as it's not used in the new save method from the prompt

@Service
public class FileSystemStorageService implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileSystemStorageService.class);

    @Value("${app.storage.base-dir:uploads}")
    private String baseDir;

    private Path storageBasePath;

    @PostConstruct
    public void init() {
        storageBasePath = Paths.get(baseDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageBasePath);
        } catch (IOException e) {
            // Using StorageException as per prompt's implied usage, but RuntimeException was here.
            // For consistency with prompt's new save method, let's assume StorageException is preferred.
            throw new StorageException("Could not create base storage directory", e);
        }
    }

    @Override
    public String save(String folder, MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        // Sanitize the provided folder name by cleaning the path.
        String sanitizedFolder = StringUtils.cleanPath(folder);

        // Sanitize the original filename from the multipart file.
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new StorageException("File has no valid original name.");
        }
        String sanitizedFilename = StringUtils.cleanPath(originalFilename);

        // Prevent storing files with ".." in the sanitized name or folder,
        // which could still be problematic.
        if (sanitizedFilename.contains("..")) {
            throw new StorageException("Cannot store file with relative path component: " + sanitizedFilename);
        }
        if (sanitizedFolder.contains("..")) {
            throw new StorageException("Cannot store file with relative path component in folder: " + sanitizedFolder);
        }

        try {
            // Resolve the target folder relative to the base storage path.
            Path targetFolder = this.storageBasePath.resolve(sanitizedFolder).normalize();

            // SECURITY CHECK: Ensure the target folder is within the base storage path.
            if (!targetFolder.startsWith(this.storageBasePath)) {
                log.warn("Path traversal attempt detected for folder: {}. Resolved target: {}", sanitizedFolder, targetFolder);
                throw new StorageException("Path traversal attempt: Cannot save to folder outside base directory.");
            }

            Files.createDirectories(targetFolder); // Create directories if they don't exist.

            // Resolve the final target file path.
            Path targetFile = targetFolder.resolve(sanitizedFilename).normalize();

            // SECURITY CHECK: Ensure the final target file path is also within the resolved target folder.
            // This prevents the filename itself from trying to escape the (now validated) targetFolder.
            if (!targetFile.startsWith(targetFolder)) {
                log.warn("Path traversal attempt detected for filename relative to folder. Filename: {}, Folder: {}", sanitizedFilename, targetFolder);
                throw new StorageException("Path traversal attempt: Filename cannot escape target folder.");
            }
            // An additional check against storageBasePath can be done for defense in depth,
            // but if targetFolder is validated and targetFile.startsWith(targetFolder) is true,
            // then targetFile must also be within storageBasePath.
            // if (!targetFile.startsWith(this.storageBasePath)) { // This would be redundant but harmless.
            //     log.warn("Path traversal attempt detected for filename: {}. Resolved target: {}", sanitizedFilename, targetFile);
            //     throw new StorageException("Path traversal attempt: Cannot save file outside base directory.");
            // }

            // Copy the file to the target location, replacing if it already exists.
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
                log.info("Successfully stored file: {} in folder: {}", sanitizedFilename, sanitizedFolder);
            }

            // Return the path relative to the storage base, including the folder.
            return this.storageBasePath.relativize(targetFile).toString().replace("\\", "/"); // Ensure consistent path separators

        } catch (IOException e) {
            log.error("Failed to store file {} in folder {}: {}", sanitizedFilename, sanitizedFolder, e.getMessage(), e);
            throw new StorageException("Failed to store file " + sanitizedFilename, e);
        }
    }

    @Override
    public Resource load(String folder, String filename) {
        try {
            Path filePath = storageBasePath.resolve(folder).resolve(filename).normalize();
            if (!filePath.startsWith(this.storageBasePath)) { // Changed to this.storageBasePath for consistency
                // Using StorageException for consistency if SecurityException is not a custom project exception.
                // The prompt indicates StorageException is used. Load/delete had SecurityException.
                // For now, I will leave SecurityException as it was in load/delete,
                // assuming it's a deliberate choice or a standard Spring exception.
                // If StorageException is the project's standard, these should also change.
                // The subtask is focused on "save", so I won't alter load/delete unless instructed.
                throw new SecurityException("Illegal file access attempt");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or unreadable: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file path", e);
        }
    }

    @Override
    public void delete(String fileType, String filename) {
        try {
            Path filePath = storageBasePath.resolve(fileType).resolve(filename).normalize();
            if (!filePath.startsWith(this.storageBasePath)) { // Changed to this.storageBasePath for consistency
                throw new SecurityException("Illegal file access attempt");
            }
             Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filename, e);
        }
    }
}
