package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.entity.CarImage;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.repository.ICarImageRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.FileStorageService;
import za.ac.cput.service.IDataIntegrityService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataIntegrityServiceImpl implements IDataIntegrityService {

    private static final Logger log = LoggerFactory.getLogger(DataIntegrityServiceImpl.class);

    private final ICarImageRepository carImageRepository; // <-- Changed
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final Path storageBasePath;

    @Autowired
    public DataIntegrityServiceImpl(ICarImageRepository carImageRepository, UserRepository userRepository, FileStorageService fileStorageService, @Value("${app.storage.base-dir:uploads}") String baseDir) {
        this.carImageRepository = carImageRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.storageBasePath = Paths.get(baseDir).toAbsolutePath();
    }

    @Override
    public Map<String, List<String>> findOrphanedFiles() {
        Map<String, List<String>> orphanedFiles = new HashMap<>();

        // Get all known filenames from the database
        Set<String> carImageFileNames = carImageRepository.findAll().stream()
                .map(CarImage::getFileName).collect(Collectors.toSet());
        Set<String> userImageFileNames = userRepository.findAll().stream()
                .map(User::getProfileImageFileName).filter(Objects::nonNull).collect(Collectors.toSet());

        // Scan folders and find files not in the known sets
        orphanedFiles.put("cars", findOrphansInFolder("cars", carImageFileNames));
        orphanedFiles.put("selfies", findOrphansInFolder("selfies", userImageFileNames));

        return orphanedFiles;
    }

    private List<String> findOrphansInFolder(String folderName, Set<String> knownDbFiles) {
        Path folderPath = storageBasePath.resolve(folderName);
        if (!Files.exists(folderPath)) return Collections.emptyList();
        try (Stream<Path> pathStream = Files.walk(folderPath, 1)) {
            return pathStream
                    .filter(path -> !Files.isDirectory(path))
                    .map(path -> path.getFileName().toString())
                    .filter(fileName -> !knownDbFiles.contains(fileName))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Could not scan folder '{}' for orphaned files.", folderName, e);
            return List.of("Error scanning folder: " + e.getMessage());
        }
    }

    @Override
    public List<Object> findBrokenImageLinks() {
        // Find CarImage records that point to a file that doesn't exist
        return carImageRepository.findAll().stream()
                .filter(carImage -> !fileStorageService.fileExists("cars", carImage.getFileName()))
                .collect(Collectors.toList());
    }

    @Override
    public void deletePhysicalFile(String folder, String filename) {
        fileStorageService.delete(folder, filename);
    }

    @Override
    public void deleteCarImageAssociation(UUID imageUuid) {
        // Deleting the CarImage record will automatically remove it from the Car's list
        // thanks to the JPA relationship management.
        if (carImageRepository.existsById(imageUuid)) {
            carImageRepository.deleteById(imageUuid);
            log.info("Successfully deleted CarImage record with UUID: {}", imageUuid);
        } else {
            log.warn("Attempted to delete a non-existent CarImage record with UUID: {}", imageUuid);
        }
    }


    /**
     * Calculates statistics about the application's file storage.
     *
     * @return A map containing total file counts and total storage size.
     */
    @Override
    public Map<String, Object> getFileSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalFileCount = 0;
        long totalSizeInBytes = 0;

        // Gracefully handle if the base path itself doesn't exist.
        if (storageBasePath == null || !Files.exists(storageBasePath)) {
            log.warn("Storage base path does not exist. Cannot calculate file system stats.");
            stats.put("totalFileCount", 0L);
            stats.put("totalSizeInBytes", 0L);
            stats.put("totalSizeFormatted", "0 B");
            return stats;
        }

        List<String> foldersToScan = List.of("cars", "selfies", "docs");

        for (String folder : foldersToScan) {
            Path folderPath = storageBasePath.resolve(folder);
            if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
                try (Stream<Path> walk = Files.walk(folderPath, 1)) {
                    // This stream is now safer
                    List<Path> files = walk.filter(p -> p != null && Files.isRegularFile(p)).collect(Collectors.toList());
                    totalFileCount += files.size();
                    totalSizeInBytes += files.stream().mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    }).sum();
                } catch (IOException e) {
                    log.error("Could not scan folder '{}' for stats due to an IO error.", folder, e);
                }
            }
        }

        stats.put("totalFileCount", totalFileCount);
        stats.put("totalSizeInBytes", totalSizeInBytes);
        stats.put("totalSizeFormatted", formatSize(totalSizeInBytes));

        return stats;
    }

    /**
     * Calculates the total storage size used by each sub-folder in the uploads directory.
     *
     * @return A Map where the key is the folder name (e.g., "cars") and the value is the total size in bytes.
     */
    @Override
    public Map<String, Long> getStorageUsagePerFolder() {
        Map<String, Long> usageMap = new HashMap<>();
        List<String> foldersToScan = List.of("cars", "selfies", "docs"); // Your managed folders

        for (String folder : foldersToScan) {
            long folderSize = 0;
            Path folderPath = storageBasePath.resolve(folder);
            if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
                try (Stream<Path> walk = Files.walk(folderPath)) {
                    folderSize = walk.filter(Files::isRegularFile)
                            .mapToLong(p -> {
                                try {
                                    return Files.size(p);
                                } catch (IOException e) {
                                    log.warn("Could not read size of file: {}", p, e);
                                    return 0L;
                                }
                            }).sum();
                } catch (IOException e) {
                    log.error("Could not scan folder '{}' for storage usage.", folder, e);
                }
            }
            usageMap.put(folder, folderSize);
        }
        return usageMap;
    }

    /**
     * Helper method to format bytes into KB, MB, GB, etc.
     */
    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}