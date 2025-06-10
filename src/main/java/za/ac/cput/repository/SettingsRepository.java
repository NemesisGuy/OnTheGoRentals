package za.ac.cput.repository;
/**
 * Author: Peter Buckingham (220165289)
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.settings.Settings;

import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, Integer> {
    Iterable<Settings> findByDeletedFalse();

    Optional<Settings> findByIdAndDeletedFalse(Integer id);

    boolean existsByIdAndDeletedFalse(Integer settingsId);
}
