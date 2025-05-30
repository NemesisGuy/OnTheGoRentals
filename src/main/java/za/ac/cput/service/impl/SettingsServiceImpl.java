package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Spring's Transactional
import za.ac.cput.domain.settings.Settings;
import za.ac.cput.factory.impl.SettingsFactory; // If used for validation/defaulting
import za.ac.cput.repository.SettingsRepository;
import za.ac.cput.exception.ResourceNotFoundException; // For consistency
import za.ac.cput.service.ISettingsService;

import java.util.Optional;

/**
 * SettingsServiceImpl.java
 * Implementation of the {@link ISettingsService} interface.
 * Manages application settings, typically a singleton-like configuration entity.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 *
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service("settingsServiceImpl") // Explicit bean name
public class SettingsServiceImpl implements ISettingsService { // Renamed class for convention

    private static final Logger log = LoggerFactory.getLogger(SettingsServiceImpl.class);

    private final SettingsRepository settingsRepository;
    // private final SettingsFactory settingsFactory; // Factory might not be needed if builder handles creation

    /**
     * Constructs the SettingsServiceImpl.
     *
     * @param settingsRepository The repository for settings persistence.
     */
    @Autowired
    public SettingsServiceImpl(SettingsRepository settingsRepository /*, SettingsFactory settingsFactory */) {
        this.settingsRepository = settingsRepository;
        // this.settingsFactory = settingsFactory;
        log.info("SettingsServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     * Creates new application settings. This typically happens once.
     * Uses {@link SettingsFactory} if provided for initial creation logic,
     * otherwise directly saves the provided settings entity.
     */
    @Override
    @Transactional // Spring's Transactional
    public Settings create(Settings settings) {
        log.info("Attempting to create new settings. Currency: {}, Symbol: {}",
                settings.getCurrencyName(), settings.getCurrencySymbol());
        // If SettingsFactory is used for complex creation/validation:
        // Settings newSettings = SettingsFactory.createSettings(settings.getCurrencyName(), settings.getCurrencySymbol(), ...);
        // For simple creation with builder, ensure defaults are set if 'settings' is partial.
        // Assuming 'settings' passed in is the desired state.
        if (settings.getId() != 0) {
            log.warn("Attempting to create settings with a pre-existing ID: {}. This might lead to an update if ID exists, or error.", settings.getId());
        }
        settings = new Settings.Builder().copy(settings).deleted(false).build(); // Ensure not deleted

        Settings savedSettings = settingsRepository.save(settings);
        log.info("Successfully created settings. ID: {}", savedSettings.getId());
        return savedSettings;
    }

    /**
     * {@inheritDoc}
     * Reads application settings by a fixed ID (typically ID 1 for a singleton settings record).
     */
    @Override
    public Settings read(Integer id) {
        log.debug("Attempting to read settings by ID: {}", id);
        Settings settings = settingsRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (settings == null) {
            log.warn("Settings not found or are deleted for ID: {}", id);
        } else {
            log.debug("Settings found for ID: {}. Currency: {}", id, settings.getCurrencyName());
        }
        return settings;
    }

    /**
     * {@inheritDoc}
     * Updates existing application settings. The input {@code settings} entity
     * should be the complete new state, typically built using the Builder pattern.
     * @throws ResourceNotFoundException if settings with the given ID do not exist.
     */
    @Override
    @Transactional // Spring's Transactional
    public Settings update(Settings settingsWithUpdates) { // Parameter name implies it's the desired new state
        Integer settingsId = settingsWithUpdates.getId();
        log.info("Attempting to update settings with ID: {}", settingsId);

        if (settingsId == null) {
            log.error("Update failed: Settings ID cannot be null.");
            throw new IllegalArgumentException("Settings ID cannot be null for update.");
        }
        // Ensure the settings record exists before attempting an update
        if (!settingsRepository.existsByIdAndDeletedFalse(settingsId)) {
            log.warn("Update failed: Settings not found or are deleted for ID: {}", settingsId);
            throw new ResourceNotFoundException("Settings not found with ID: " + settingsId + " for update.");
        }

        // 'settingsWithUpdates' IS the new state. Controller should have built it.
        // Example: existingSettings = service.read(id);
        //          settingsWithUpdates = new Settings.Builder().copy(existingSettings)...build();
        //          service.update(settingsWithUpdates);
        log.debug("Updating settings ID: {} with Currency: {}, Symbol: {}",
                settingsId, settingsWithUpdates.getCurrencyName(), settingsWithUpdates.getCurrencySymbol());

        Settings savedSettings = settingsRepository.save(settingsWithUpdates); // save() will perform update if ID exists
        log.info("Successfully updated settings. ID: {}", savedSettings.getId());
        return savedSettings;
    }

    /**
     * {@inheritDoc}
     * Soft-deletes application settings by ID.
     */
    @Override
    @Transactional // Spring's Transactional
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete settings with ID: {}", id);
        Optional<Settings> settingsOpt = settingsRepository.findByIdAndDeletedFalse(id);
        if (settingsOpt.isPresent()) {
            Settings settings = settingsOpt.get();
            Settings deletedSettings = new Settings.Builder().copy(settings).deleted(true).build();
            settingsRepository.save(deletedSettings);
            log.info("Successfully soft-deleted settings with ID: {}", id);
            return true;
        }
        log.warn("Soft-delete failed: Settings not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     * Retrieves all non-deleted settings entries (typically there's only one).
     */
    @Override
    public Iterable<Settings> getAll() {
        log.debug("Fetching all non-deleted settings.");
        Iterable<Settings> settingsList = settingsRepository.findByDeletedFalse();
        if (log.isDebugEnabled()) {
            long count = 0;
            for (Settings s : settingsList) count++;
            log.debug("Found {} non-deleted settings entries.", count);
        }
        return settingsList;
    }
}