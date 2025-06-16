package za.ac.cput.service;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

/**
 * FileStorageService.java
 * A service interface for handling file storage operations.
 * This interface defines methods for saving, loading, deleting files,
 * and checking file existence in a storage system.
 * <p>
 * Author: Peter Buckingham
 * Version: 1.0
 * Date: 2025-05-29
 * This service can be implemented for various storage backends such as local disk, MinIO, AWS S3, etc.
 */

public interface FileStorageService {

    /**
     * Saves a file to the storage system within a specified logical directory.
     *
     * @param folder The subdirectory or prefix for the file (e.g., "cars", "selfies").
     * @param file   The multipart file to save.
     * @return The unique key identifying the saved file (e.g., "cars/uuid.jpg").
     */
    String save(String folder, MultipartFile file);

    /**
     * Loads a file as a Spring Resource.
     *
     * @param folder   The subdirectory or prefix for the file (e.g., "cars", "selfies").
     * @param filename The name of the file to load.
     * @return The Resource representing the file, or null if not found.
     */
    Resource load(String folder, String filename);

    /**
     * Deletes a file from the storage system.
     *
     * @param fileType The type of file (e.g., "cars", "selfies").
     * @param filename The name of the file to delete.
     * @return true if the file was deleted or did not exist; false if an error occurred.
     */
    boolean delete(String fileType, String filename);

    /**
     * Checks if a file exists in the storage system.
     *
     * @param cars     The type of file (e.g., "cars", "selfies").
     * @param fileName The name of the file to check.
     * @return true if the file exists, false otherwise.
     */
    boolean fileExists(String cars, String fileName);

    /**
     * Gets a publicly accessible URL for a file.
     * <p>
     * For local storage, this might point to a controller endpoint (e.g., /api/v1/files/...).
     * For object storage like MinIO, this could be a direct or a temporary pre-signed URL.
     *
     * @param key The unique key of the file.
     * @return A URL object that can be used to access the file.
     */
    URL getUrl(String key);
}

