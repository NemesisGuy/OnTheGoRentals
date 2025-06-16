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

@Service
@Profile("storage-local")
public class LocalFileStorageService implements IFileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageService.class);
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/gif");

    @Value("${app.storage.base-dir:uploads}")
    private String baseDir;

    private Path storageBasePath;

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
            throw new SecurityException("File type not allowed.");
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

    @Override
    public Optional<Resource> loadAsResource(String key) {
        try {
            Path filePath = this.storageBasePath.resolve(key).normalize();
            if (!filePath.startsWith(this.storageBasePath)) {
                return Optional.empty();
            }
            Resource resource = new UrlResource(filePath.toUri());
            return (resource.exists() && resource.isReadable()) ? Optional.of(resource) : Optional.empty();
        } catch (MalformedURLException | InvalidPathException e) {
            return Optional.empty();
        }
    }

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

    @Override
    public boolean delete(String key) {
        if (key == null || key.isBlank()) return true;
        try {
            Path filePath = this.storageBasePath.resolve(key).normalize();
            if (!filePath.startsWith(this.storageBasePath)) {
                return false;
            }
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file with key {}: {}", key, e.getMessage());
            return false;
        }
    }

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

    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}