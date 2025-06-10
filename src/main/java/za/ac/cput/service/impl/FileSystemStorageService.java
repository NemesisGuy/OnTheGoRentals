package za.ac.cput.service.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.service.FileStorageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileSystemStorageService implements FileStorageService {

    @Value("${app.storage.base-dir:uploads}")
    private String baseDir;

    private Path storageBasePath;

    @PostConstruct
    public void init() {
        storageBasePath = Paths.get(baseDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageBasePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create base storage directory", e);
        }
    }

    @Override
    public String save(String folder, MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID() + (extension != null ? "." + extension : "");

        try {
            Path targetFolder = storageBasePath.resolve(folder).normalize();
            Files.createDirectories(targetFolder);
            Path targetFile = targetFolder.resolve(filename);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    @Override
    public Resource load(String folder, String filename) {
        try {
            Path filePath = storageBasePath.resolve(folder).resolve(filename).normalize();
            if (!filePath.startsWith(storageBasePath)) {
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
            if (!filePath.startsWith(storageBasePath)) {
                throw new SecurityException("Illegal file access attempt");
            }
             Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filename, e);
        }
    }
}
