package za.ac.cput.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IDataIntegrityService {
    Map<String, List<String>> findOrphanedFiles();

    List<Object> findBrokenImageLinks(); // Changed return type

    void deletePhysicalFile(String folder, String filename);

    void deleteCarImageAssociation(UUID imageUuid);

    Map<String, Object> getFileSystemStats();

    Map<String, Long> getStorageUsagePerFolder();
}