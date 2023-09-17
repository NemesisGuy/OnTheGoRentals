package za.ac.cput.service.impl;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.settings.Settings;
import za.ac.cput.factory.impl.SettingsFactory;
import za.ac.cput.repository.SettingsRepository;
import za.ac.cput.service.SettingsService;

@Service("settingsServiceImpl")
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    private SettingsFactory settingsFactory;

    @Override
    public Settings create(Settings settings) {
        Settings newSettings = settingsFactory.createSettings(settings);
        return settingsRepository.save(settings);
    }

    @Override
    public Settings read(Integer id) {
        Settings settings = settingsRepository.findById(id).orElse(null);
        return settings;
    }

    @Override
    public Settings update(Settings settings) {
        if (settingsRepository.existsById(settings.getId()))
        {
            System.out.println("Settings exists : " + settings.getId() + " " + settings.getCurrencyName() + " " + settings.getCurrencySymbol());
           // Settings updatedSettings = settingsFactory.createSettings(settings);
            return settingsRepository.save(settings);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (settingsRepository.existsById(id)) {
            settingsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Iterable<Settings> getAll() {
        return settingsRepository.findAll();
    }
}
