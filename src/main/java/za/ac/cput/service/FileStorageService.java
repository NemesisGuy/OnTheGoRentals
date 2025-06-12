package za.ac.cput.service;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String save(String folder, MultipartFile file);

    Resource load(String folder, String filename);

    boolean delete(String fileType, String filename);

    boolean fileExists(String cars, String fileName);
}

