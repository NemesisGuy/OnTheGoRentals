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

@Service
public class StorageManagementServiceImpl implements IStorageManagementService {

    private static final Logger log = LoggerFactory.getLogger(StorageManagementServiceImpl.class);

    private final ICarImageRepository carImageRepository;
    private final UserRepository userRepository;
    private final IFileStorageService fileStorageService;

    @Autowired
    public StorageManagementServiceImpl(ICarImageRepository carImageRepository, UserRepository userRepository, IFileStorageService fileStorageService) {
        this.carImageRepository = carImageRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Map<String, List<String>> findOrphanedFiles() {
        log.error("findOrphanedFiles is not supported in this abstract architecture due to the high cost of listing all objects in a cloud store. This method is a placeholder.");
        Map<String, List<String>> orphanedFiles = new HashMap<>();
        orphanedFiles.put("unsupported_operation", List.of("This operation is too expensive for cloud storage and should be run as an offline batch job."));
        return orphanedFiles;
    }

    @Override
    public List<Object> findBrokenImageLinks() {
        return carImageRepository.findAll().stream()
                .filter(carImage -> !fileStorageService.fileExists("cars/" + carImage.getFileName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deletePhysicalFile(String folder, String filename) {
        String key = folder + "/" + filename;
        return fileStorageService.delete(key);
    }

    @Override
    public void deleteCarImageAssociation(UUID imageUuid) {
        carImageRepository.findById(imageUuid).ifPresent(carImage -> {
            carImageRepository.deleteById(imageUuid);
            log.info("Successfully deleted CarImage record with UUID: {}", imageUuid);
        });
    }

    @Override
    public Map<String, Object> getFileSystemStats() {
        // Check the actual class of the injected service
        if (fileStorageService instanceof LocalFileStorageService) {
            return ((LocalFileStorageService) fileStorageService).getStats();
        }
        if (fileStorageService instanceof MinioStorageService) {
            return ((MinioStorageService) fileStorageService).getStats();
        }
        log.error("getFileSystemStats not implemented for the current storage service type: {}", fileStorageService.getClass().getName());
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Long> getStorageUsagePerFolder() {
        // Check the actual class of the injected service
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