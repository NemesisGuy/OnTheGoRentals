package za.ac.cput.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * IStorageManagementService.java
 * A service for managing the integrity and statistics of the application's file storage.
 * This service uses the core IFileStorageService to interact with the underlying storage.
 *
 * Author: Peter Buckingham (refactored by AI)
 * Version: 2.0
 */
public interface IStorageManagementService {

    /**
     * Finds files that exist in storage but have no corresponding database record.
     * Note: This can be an expensive operation on cloud object storage.
     * @return A map where the key is the directory and the value is a list of orphaned filenames.
     */
    Map<String, List<String>> findOrphanedFiles();

    /**
     * Finds CarImage records that point to a file that does not exist in storage.
     * @return A list of objects representing broken links.
     */
    List<Object> findBrokenImageLinks();

    /**
     * Deletes a physical file by its folder and filename.
     * @param folder The directory of the file.
     * @param filename The name of the file.
     * @return true if deletion was successful, false otherwise.
     */
    boolean deletePhysicalFile(String folder, String filename);

    /**
     * Deletes a CarImage database record by its UUID.
     * @param imageUuid The UUID of the CarImage to delete.
     */
    void deleteCarImageAssociation(UUID imageUuid);

    /**
     * Calculates statistics about the file storage system.
     * Note: This can be an expensive operation on cloud object storage.
     * @return A map containing file count and formatted total size.
     */
    Map<String, Object> getFileSystemStats();

    /**
     * Calculates the storage size used by each major folder.
     * Note: This can be an expensive operation on cloud object storage.
     * @return A map where the key is the folder name and the value is the size in bytes.
     */
    Map<String, Long> getStorageUsagePerFolder();
}