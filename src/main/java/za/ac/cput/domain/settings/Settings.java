package za.ac.cput.domain.settings;
/**
 * Author: Peter Buckingham (220165289)
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Settings {

    @Id
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();
    private String currencyName;
    private String currencySymbol;
    private boolean deleted = false;

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
    public boolean isDeleted() {
        return deleted;
    }

    // Builder Pattern
    public static class Builder {

        private int id;
        private String currencyName;
        private String currencySymbol;
        private boolean deleted = false;

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
        public Builder deleted(boolean deleted) {
            this.deleted = deleted;
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

