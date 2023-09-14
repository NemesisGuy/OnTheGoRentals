package za.ac.cput.repository;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.settings.Settings;

public interface SettingsRepository extends JpaRepository<Settings, Integer> {
}
