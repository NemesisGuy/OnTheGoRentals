package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.repository.ICarImageRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.IFileStorageService;
import za.ac.cput.service.IStorageManagementService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing the consistency and statistics of the file storage system.
 * This service coordinates between database records (e.g., CarImage, User) and the physical
 * files managed by the active {@link IFileStorageService} implementation (local or MinIO).
 * It provides higher-level operations for cleanup, reporting, and maintenance.
 */
@Service
public class StorageManagementServiceImpl implements IStorageManagementService {

    private static final Logger log = LoggerFactory.getLogger(StorageManagementServiceImpl.class);

    private final ICarImageRepository carImageRepository;
    private final UserRepository userRepository;
    private final IFileStorageService fileStorageService;

    /**
     * Constructs the StorageManagementServiceImpl with its required dependencies.
     *
     * @param carImageRepository The repository for car image metadata.
     * @param userRepository     The repository for user data (for future use, e.g., user profile pictures).
     * @param fileStorageService The active file storage service implementation (local or MinIO).
     */
    @Autowired
    public StorageManagementServiceImpl(ICarImageRepository carImageRepository, UserRepository userRepository, IFileStorageService fileStorageService) {
        this.carImageRepository = carImageRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Finds files that exist in the storage system but have no corresponding record in the database.
     * <p>
     * <strong>ARCHITECTURAL NOTE:</strong> This operation is extremely expensive and slow on cloud object storage
     * (like MinIO/S3) because it requires listing every single file in the bucket. For this reason,
     * this method is intentionally not implemented and serves as a placeholder. True orphan detection
     * should be handled by a dedicated, offline batch process.
     * </p>
     *
     * @return A map indicating that the operation is unsupported.
     */
    @Override
    public Map<String, List<String>> findOrphanedFiles() {
        log.error("findOrphanedFiles is not supported in this abstract architecture due to the high cost of listing all objects in a cloud store. This method is a placeholder.");
        Map<String, List<String>> orphanedFiles = new HashMap<>();
        orphanedFiles.put("unsupported_operation", List.of("This operation is too expensive for cloud storage and should be run as an offline batch job."));
        return orphanedFiles;
    }

    /**
     * Finds broken image links by checking for {@code CarImage} records in the database
     * whose corresponding physical files do not exist in the storage system.
     *
     * @return A list of {@code CarImage} entities that point to non-existent files.
     */
    @Override
    public List<Object> findBrokenImageLinks() {
        return carImageRepository.findAll().stream()
                .filter(carImage -> !fileStorageService.fileExists("cars/" + carImage.getFileName()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes a physical file from the active storage system.
     * This method only removes the file itself; it does not affect any database records that might reference it.
     *
     * @param folder   The folder (prefix) where the file is located (e.g., "cars").
     * @param filename The name of the file to delete.
     * @return {@code true} if the deletion was successful, {@code false} otherwise.
     */
    @Override
    public boolean deletePhysicalFile(String folder, String filename) {
        String key = folder + "/" + filename;
        return fileStorageService.delete(key);
    }

    /**
     * Deletes the database association for a car image, identified by its UUID.
     * This method only removes the {@code CarImage} record from the database; it does not delete the physical file.
     *
     * @param imageUuid The UUID of the {@code CarImage} database record to delete.
     */
    @Override
    public void deleteCarImageAssociation(UUID imageUuid) {
        carImageRepository.findById(imageUuid).ifPresent(carImage -> {
            carImageRepository.deleteById(imageUuid);
            log.info("Successfully deleted CarImage record with UUID: {}", imageUuid);
        });
    }

    /**
     * Retrieves overall storage statistics (total file count, total size) from the active storage service.
     * This method dynamically checks the runtime type of the {@link IFileStorageService} and calls
     * the appropriate implementation-specific method.
     *
     * @return A map containing storage statistics, or an empty map if the active service type is unsupported.
     */
    @Override
    public Map<String, Object> getFileSystemStats() {
        if (fileStorageService instanceof LocalFileStorageService) {
            return ((LocalFileStorageService) fileStorageService).getStats();
        }
        if (fileStorageService instanceof MinioStorageService) {
            return ((MinioStorageService) fileStorageService).getStats();
        }
        log.error("getFileSystemStats not implemented for the current storage service type: {}", fileStorageService.getClass().getName());
        return Collections.emptyMap();
    }

    /**
     * Retrieves storage usage broken down by folder from the active storage service.
     * This method dynamically checks the runtime type of the {@link IFileStorageService} and calls
     * the appropriate implementation-specific method.
     *
     * @return A map where keys are folder names and values are their total size in bytes. Returns an empty map
     * if the active service type is unsupported.
     */
    @Override
    public Map<String, Long> getStorageUsagePerFolder() {
        if (fileStorageService instanceof LocalFileStorageService) {
            return ((LocalFileStorageService) fileStorageService).getUsagePerFolder();
        }
        if (fileStorageService instanceof MinioStorageService) {
            return ((MinioStorageService) fileStorageService).getUsagePerFolder();
        }
        log.error("getStorageUsagePerFolder not implemented for the current storage service type: {}", fileStorageService.getClass().getName());
        return Collections.emptyMap();
    }
}