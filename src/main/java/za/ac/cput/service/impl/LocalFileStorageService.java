package za.ac.cput.service.impl;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import za.ac.cput.service.IFileStorageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service implementation for storing files on the local filesystem.
 * This service is activated only when the 'storage-local' Spring profile is active.
 * It handles file operations within a configured base directory and includes security
 * checks to prevent directory traversal and disallowed file types.
 */
@Service
@Profile("storage-local")
public class LocalFileStorageService implements IFileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageService.class);
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/gif");

    @Value("${app.storage.base-dir:uploads}")
    private String baseDir;

    private Path storageBasePath;

    /**
     * Initializes the service after construction. It resolves the base storage directory
     * from the application properties and creates it if it doesn't already exist.
     *
     * @throws RuntimeException if the storage directory cannot be created.
     */
    @PostConstruct
    public void init() {
        storageBasePath = Paths.get(baseDir).toAbsolutePath().normalize();
        log.info("LOCAL STORAGE: Base directory initialized at: {}", storageBasePath);
        try {
            Files.createDirectories(storageBasePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create local storage directory", e);
        }
    }

    /**
     * Saves a multipart file to a specified subdirectory on the local filesystem.
     * This method performs several security checks:
     * - Rejects empty files.
     * - Validates the directory name against invalid characters.
     * - Enforces a whitelist of allowed MIME types.
     * - Prevents directory traversal attacks.
     * A unique filename is generated using UUID to avoid collisions.
     *
     * @param file      The {@link MultipartFile} to be saved.
     * @param directory The target subdirectory (e.g., "cars", "selfies") under the base storage path.
     * @return The key (relative path, e.g., "cars/uuid.jpg") under which the file was saved.
     * @throws RuntimeException  if the file is empty or cannot be stored.
     * @throws SecurityException if the directory name is invalid or the file type is not allowed.
     */
    @Override
    public String save(MultipartFile file, String directory) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new RuntimeException("Cannot store empty file or file with no name.");
        }
        if (directory == null || directory.isBlank() || directory.contains("..") || directory.contains("/") || directory.contains("\\")) {
            throw new SecurityException("Directory name contains invalid characters.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new SecurityException("File type not allowed. Provided: " + contentType);
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID() + (extension != null ? "." + extension : "");
        String key = directory + "/" + filename;

        try {
            Path targetFolder = this.storageBasePath.resolve(directory).normalize();
            if (!targetFolder.startsWith(this.storageBasePath)) {
                throw new SecurityException("Cannot store file outside base directory.");
            }

            Files.createDirectories(targetFolder);
            Path targetFile = targetFolder.resolve(filename);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file with key: " + key, e);
        }
    }

    /**
     * Loads a file from the local filesystem as a Spring {@link Resource}.
     * This is the primary method used by the `FileController` to stream file content to the client.
     * It includes a check to ensure the requested file path is within the base storage directory.
     *
     * @param key The key (relative path) of the file to load (e.g., "cars/uuid.jpg").
     * @return An {@link Optional} containing the {@link Resource} if the file exists and is readable,
     * or an empty Optional otherwise.
     */
    @Override
    public Optional<Resource> loadAsResource(String key) {
        try {
            Path filePath = this.storageBasePath.resolve(key).normalize();
            if (!filePath.startsWith(this.storageBasePath)) {
                log.warn("Attempt to access file outside base directory blocked: {}", key);
                return Optional.empty();
            }
            Resource resource = new UrlResource(filePath.toUri());
            return (resource.exists() && resource.isReadable()) ? Optional.of(resource) : Optional.empty();
        } catch (MalformedURLException | InvalidPathException e) {
            return Optional.empty();
        }
    }

    /**
     * Checks if a file with the given key exists on the local filesystem.
     *
     * @param key The key (relative path) of the file to check.
     * @return {@code true} if the file exists as a regular file, {@code false} otherwise.
     */
    @Override
    public boolean fileExists(String key) {
        if (key == null || key.isBlank()) return false;
        try {
            Path filePath = this.storageBasePath.resolve(key).normalize();
            return filePath.startsWith(this.storageBasePath) && Files.isRegularFile(filePath);
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /**
     * Deletes a file from the local filesystem based on its key.
     * This operation is idempotent; if the file doesn't exist, it succeeds without error.
     *
     * @param key The key (relative path) of the file to delete.
     * @return {@code true} if the deletion was successful or the file didn't exist; {@code false} on error or if access is denied.
     */
    @Override
    public boolean delete(String key) {
        if (key == null || key.isBlank()) return true;
        try {
            Path filePath = this.storageBasePath.resolve(key).normalize();
            if (!filePath.startsWith(this.storageBasePath)) {
                log.warn("Attempt to delete file outside base directory blocked: {}", key);
                return false;
            }
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file with key {}: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Constructs a URL that points to the application's own {@code FileController}.
     * This follows the "backend as a file proxy" pattern, ensuring that all file access
     * goes through the application's security and logic layer, rather than exposing direct
     * filesystem links.
     *
     * @param key The key (relative path) of the file.
     * @return A {@link URL} pointing to the API endpoint for serving the file (e.g., "http://host/api/v1/files/cars/uuid.jpg").
     * @throws RuntimeException if the URL cannot be constructed.
     */
    @Override
    public URL getUrl(String key) {
        try {
            String urlString = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/")
                    .path(key)
                    .toUriString();
            return new URI(urlString).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException("Could not create URL for key: " + key, e);
        }
    }

    /**
     * Calculates statistics for the local storage directory, including total file count and size
     * across a predefined set of subdirectories.
     *
     * @return A {@link Map} containing 'totalFileCount' and 'totalSizeFormatted'.
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalFileCount = 0;
        long totalSizeInBytes = 0;
        List<String> foldersToScan = List.of("cars", "selfies", "docs");

        for (String folder : foldersToScan) {
            Path folderPath = storageBasePath.resolve(folder);
            if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
                try (Stream<Path> walk = Files.walk(folderPath)) {
                    List<Path> files = walk.filter(Files::isRegularFile).collect(Collectors.toList());
                    totalFileCount += files.size();
                    totalSizeInBytes += files.stream().mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    }).sum();
                } catch (IOException e) {
                    log.error("Could not scan folder '{}' for stats", folder, e);
                }
            }
        }
        stats.put("totalFileCount", totalFileCount);
        stats.put("totalSizeFormatted", formatSize(totalSizeInBytes));
        return stats;
    }

    /**
     * Calculates the total storage usage for a predefined set of subdirectories.
     *
     * @return A {@link Map} where keys are folder names and values are their total size in bytes.
     */
    public Map<String, Long> getUsagePerFolder() {
        Map<String, Long> usageMap = new HashMap<>();
        List<String> foldersToScan = List.of("cars", "selfies", "docs");
        for (String folder : foldersToScan) {
            long folderSize = 0;
            Path folderPath = storageBasePath.resolve(folder);
            if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
                try (Stream<Path> walk = Files.walk(folderPath)) {
                    folderSize = walk.filter(Files::isRegularFile).mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    }).sum();
                } catch (IOException e) {
                    log.error("Could not scan folder '{}' for usage", folder, e);
                }
            }
            usageMap.put(folder, folderSize);
        }
        return usageMap;
    }

    /**
     * Formats a size in bytes into a human-readable string (e.g., "1.5 MB").
     *
     * @param size The size in bytes.
     * @return A formatted string representation of the size.
     */
    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}