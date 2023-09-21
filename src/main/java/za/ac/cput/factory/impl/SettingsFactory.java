package za.ac.cput.factory.impl;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */

import za.ac.cput.domain.settings.Settings;

public class SettingsFactory {

    public static Settings createSettings(int id, String currencyName, String currencySymbol) {
        return new Settings.Builder()
                .id(id)
                .currencyName(currencyName)
                .currencySymbol(currencySymbol)
                .build();
    }

    public static Settings createSettings(Settings settings) {
        return new Settings.Builder()

                .currencyName(settings.getCurrencyName())
                .currencySymbol(settings.getCurrencySymbol())
                .build();
    }
    //takes a Settings object and returns a copy of it
    public static Settings copy(Settings settings) {
        return new Settings.Builder()
                .id(settings.getId())
                .currencyName(settings.getCurrencyName())
                .currencySymbol(settings.getCurrencySymbol())
                .build();
    }
}
