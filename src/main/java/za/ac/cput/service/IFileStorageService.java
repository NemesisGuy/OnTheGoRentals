package za.ac.cput.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Optional;

/**
 * IFileStorageService.java
 * A generic interface for core file storage operations, designed to be implementation-agnostic.
 * Can be implemented for local disk, MinIO, AWS S3, etc.
 * <p>
 * Author: Peter Buckingham (refactored by AI)
 * Version: 4.0
 */
public interface IFileStorageService {

    /**
     * Saves a file to the storage system within a specified logical directory.
     *
     * @param file      The multipart file to save.
     * @param directory The subdirectory or prefix for the file (e.g., "cars", "selfies").
     * @return The unique key identifying the saved file (e.g., "cars/uuid.jpg").
     */
    String save(MultipartFile file, String directory);

    /**
     * Retrieves a file as a Spring Resource, wrapped in an Optional.
     *
     * @param key The unique key of the file (e.g., "cars/uuid.jpg").
     * @return An Optional containing the Resource, or empty if not found.
     */
    Optional<Resource> loadAsResource(String key);

    /**
     * Deletes a file from the storage system.
     *
     * @param key The unique key of the file to delete.
     * @return true if the file was deleted or did not exist; false if an error occurred.
     */
    boolean delete(String key);

    /**
     * Checks if a file exists in the storage system.
     *
     * @param key The unique key of the file to check.
     * @return true if the file exists, false otherwise.
     */
    boolean fileExists(String key);

    /**
     * Gets a publicly accessible URL for a file.
     *
     * @param key The unique key of the file.
     * @return A URL to access the file (could be a direct or pre-signed URL).
     */
    URL getUrl(String key);
}