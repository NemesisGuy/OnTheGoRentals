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
import java.net.URI; // <-- Import the modern URI class
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Profile("storage-minio")
public class MinioStorageService implements IFileStorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);
    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Autowired
    public MinioStorageService(MinioClient minioClient) {
        // The constructor should just accept dependencies.
        this.minioClient = minioClient;
    }

    /**
     * PostConstruct method to validate dependencies and configuration after they have been injected.
     * This is a safer pattern than putting validation logic in the constructor.
     */
    @PostConstruct
    public void init() {
        if (minioClient == null) {
            throw new IllegalStateException("MinioClient has not been injected correctly.");
        }
        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalStateException("Bucket name ('minio.bucket.name') must be set in application properties.");
        }
        log.info("MINIO STORAGE: Service initialized for bucket '{}'", bucketName);
    }

    @Override
    public String save(MultipartFile file, String directory) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
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
            throw new RuntimeException("MinIO error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error loading file from MinIO", e);
        }
    }

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

    @Override
    public URL getUrl(String key) {
        try {
            String urlString = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(key)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
            // Use the modern, non-deprecated way to create a URL
            return new URI(urlString).toURL();
        } catch (Exception e) {
            throw new RuntimeException("Error getting pre-signed URL from MinIO", e);
        }
    }

    public Map<String, Object> getStats() {
        log.warn("PERFORMANCE WARNING: Calculating stats on MinIO requires listing all objects, which can be slow.");
        Map<String, Object> stats = new HashMap<>();
        long totalFileCount = 0;
        long totalSizeInBytes = 0;

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).recursive(true).build()
            );
            for (Result<Item> result : results) {
                Item item = result.get();
                totalFileCount++;
                totalSizeInBytes += item.size();
            }
        } catch (Exception e) {
            log.error("Failed to calculate stats from MinIO", e);
            throw new RuntimeException("Could not get stats from MinIO", e);
        }
        stats.put("totalFileCount", totalFileCount);
        stats.put("totalSizeFormatted", formatSize(totalSizeInBytes));
        return stats;
    }

    public Map<String, Long> getUsagePerFolder() {
        log.warn("PERFORMANCE WARNING: Calculating usage per folder on MinIO requires listing objects, which can be slow.");
        Map<String, Long> usageMap = new HashMap<>();
        List<String> foldersToScan = List.of("cars", "selfies", "docs");

        for (String folder : foldersToScan) {
            long folderSize = 0;
            try {
                Iterable<Result<Item>> results = minioClient.listObjects(
                        ListObjectsArgs.builder().bucket(bucketName).prefix(folder + "/").recursive(true).build()
                );
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

    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}