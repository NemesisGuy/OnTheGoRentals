package za.ac.cput.domain.settings;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Settings {

    @Id
    private int id;

    private String currencyName;
    private String currencySymbol;

    public Settings() {

    }
    public Settings(Builder builder) {
    }

    public int getId() {
        return id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    // Builder Pattern
    public static class Builder {

        private int id;
        private String currencyName;
        private String currencySymbol;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder currencyName(String currencyName) {
            this.currencyName = currencyName;
            return this;
        }

        public Builder currencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
            return this;
        }

        public Builder copy(Settings settings) {
            this.id = settings.id;
            this.currencyName = settings.currencyName;
            this.currencySymbol = settings.currencySymbol;
            return this;
        }

        public Settings build() {
            return new Settings(this);
        }
    }

}

