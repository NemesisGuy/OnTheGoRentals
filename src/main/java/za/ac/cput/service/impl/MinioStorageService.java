package za.ac.cput.service.impl;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.service.IFileStorageService;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service implementation for interacting with a MinIO S3-compatible object storage server.
 * This service is activated only when the 'storage-minio' Spring profile is active.
 * It handles all file operations, such as saving, loading, and deleting objects in a configured bucket.
 */
@Service
@Profile("storage-minio")
public class MinioStorageService implements IFileStorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);
    private final MinioClient minioClient;
    private final String bucketName;
    private final String minioPublicUrl;

    /**
     * Constructs the MinioStorageService with all necessary dependencies and configuration.
     * This constructor is used by Spring's dependency injection mechanism.
     *
     * @param minioClient    The configured MinioClient bean for communicating with the MinIO server.
     * @param bucketName     The name of the bucket to use, injected from the 'minio.bucket.name' property.
     * @param minioPublicUrl The public-facing base URL for MinIO, injected from 'minio.public.url'.
     *                       This is used for correcting pre-signed URLs if needed.
     */
    @Autowired
    public MinioStorageService(
            MinioClient minioClient,
            @Value("${minio.bucket.name}") String bucketName,
            @Value("${minio.public.url}") String minioPublicUrl
    ) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.minioPublicUrl = minioPublicUrl;
    }

    /**
     * Initializes the service after construction. It logs the configuration being used,
     * confirming that the service is ready.
     */
    @PostConstruct
    public void init() {
        log.info("MINIO STORAGE: Service initialized for bucket '{}' with public URL '{}'", bucketName, minioPublicUrl);
    }

    /**
     * Saves a multipart file to a specified directory within the MinIO bucket.
     * If the bucket does not exist, it will be created. The file is stored with a
     * unique name generated via UUID to prevent collisions.
     *
     * @param file      The {@link MultipartFile} to be saved.
     * @param directory The target directory (prefix) within the bucket (e.g., "cars", "selfies").
     * @return The full object key (e.g., "cars/uuid.jpg") which can be used to retrieve or delete the file.
     * @throws RuntimeException if the upload process fails.
     */
    @Override
    public String save(MultipartFile file, String directory) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MINIO STORAGE: Bucket '{}' created.", bucketName);
            }

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String objectName = UUID.randomUUID() + (extension != null ? "." + extension : "");
            String key = directory + "/" + objectName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    /**
     * Loads a file from MinIO as a Spring {@link Resource}.
     * This is the primary method used by the `FileController` to stream file content to the client.
     *
     * @param key The unique key of the object to load (e.g., "cars/uuid.jpg").
     * @return An {@link Optional} containing the {@link Resource} if the file exists, or an empty Optional otherwise.
     * @throws RuntimeException for any MinIO errors other than the key not being found.
     */
    @Override
    public Optional<Resource> loadAsResource(String key) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(key).build()
            );
            return Optional.of(new InputStreamResource(stream));
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return Optional.empty();
            }
            throw new RuntimeException("MinIO error loading file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error loading file from MinIO", e);
        }
    }

    /**
     * Checks if a file with the given key exists in the MinIO bucket.
     *
     * @param key The unique key of the object to check.
     * @return {@code true} if the file exists, {@code false} otherwise.
     * @throws RuntimeException for unexpected errors during the check.
     */
    @Override
    public boolean fileExists(String key) {
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(key).build());
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) return false;
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("Error checking file existence in MinIO", e);
        }
    }

    /**
     * Deletes a file from the MinIO bucket based on its key.
     * This operation is idempotent; if the file doesn't exist, it succeeds without error.
     *
     * @param key The unique key of the object to delete.
     * @return {@code true} if the deletion was successful or the file didn't exist, {@code false} on error.
     */
    @Override
    public boolean delete(String key) {
        try {
            if (!fileExists(key)) {
                return true;
            }
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(key).build()
            );
            return true;
        } catch (Exception e) {
            log.error("Error deleting file from MinIO with key {}: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Generates a publicly accessible, pre-signed URL for a MinIO object.
     * <p>
     * <strong>ARCHITECTURAL NOTE:</strong> This method is NOT used in the primary application flow
     * for serving images to users. We use a proxy (`FileController`) instead. This method is retained
     * for potential administrative use or scenarios where direct S3 access is desired.
     * It corrects the URL generated by MinIO (which might point to an internal Docker address)
     * to use the public-facing URL.
     * </p>
     *
     * @param key The unique key of the object.
     * @return A browser-accessible {@link URL} for the object.
     * @throws RuntimeException if the URL generation or correction fails.
     */
    @Override
    public URL getUrl(String key) {
        try {
            // Step 1: Generate the pre-signed URL. It may contain an incorrect internal base URL.
            String internalUrlString = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(key)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );

            // Step 2: Manually replace the incorrect internal base with the correct public one.
            URL internalUrl = new URL(internalUrlString);
            String correctUrlString = this.minioPublicUrl + internalUrl.getPath() + "?" + internalUrl.getQuery();

            // Step 3: Return the corrected, browser-friendly URL.
            return new URI(correctUrlString).toURL();

        } catch (Exception e) {
            throw new RuntimeException("Error getting or correcting pre-signed URL from MinIO", e);
        }
    }

    // --- Stats Methods ---

    /**
     * Calculates statistics for the entire MinIO bucket, including total file count and size.
     * <strong>Warning:</strong> This method lists all objects in the bucket and can be very
     * slow and resource-intensive on large buckets.
     *
     * @return A {@link Map} containing 'totalFileCount' and 'totalSizeFormatted'.
     * @throws RuntimeException if communication with MinIO fails.
     */
    public Map<String, Object> getStats() {
        log.warn("PERFORMANCE WARNING: Calculating stats on MinIO requires listing all objects, which can be slow.");
        Map<String, Object> stats = new HashMap<>();
        long totalFileCount = 0;
        long totalSizeInBytes = 0;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                totalFileCount++;
                totalSizeInBytes += item.size();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not get stats from MinIO", e);
        }
        stats.put("totalFileCount", totalFileCount);
        stats.put("totalSizeFormatted", formatSize(totalSizeInBytes));
        return stats;
    }

    /**
     * Calculates the total storage usage for a predefined set of folders (prefixes).
     * <strong>Warning:</strong> This method performs a separate listing operation for each folder
     * and can be slow if folders contain many objects.
     *
     * @return A {@link Map} where keys are folder names and values are their total size in bytes.
     */
    public Map<String, Long> getUsagePerFolder() {
        log.warn("PERFORMANCE WARNING: Calculating usage per folder on MinIO requires listing objects, which can be slow.");
        Map<String, Long> usageMap = new HashMap<>();
        List<String> foldersToScan = List.of("cars", "selfies", "docs");
        for (String folder : foldersToScan) {
            long folderSize = 0;
            try {
                Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(folder + "/").recursive(true).build());
                for (Result<Item> result : results) {
                    folderSize += result.get().size();
                }
            } catch (Exception e) {
                log.error("Failed to calculate usage for folder '{}' from MinIO", folder, e);
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