package za.ac.cput.domain.entity;
/**
 * AboutUs.java
 * Domain Class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 */

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
public class AboutUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    private String address;
    private String officeHours;
    @Column(unique = true) // Assuming email should be unique for AboutUs if it represents a single contact point
    private String email;
    private String telephone;
    private String whatsApp;
    @Column(nullable = false)
    private boolean deleted;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false)
    private LocalDateTime updatedAt;

    protected AboutUs() {
    }

    public AboutUs(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.address = builder.address;
        this.officeHours = builder.officeHours;
        this.email = builder.email;
        this.telephone = builder.telephone;
        this.whatsApp = builder.whatsApp;
        this.deleted = builder.deleted;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : LocalDateTime.now();

    }

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        this.deleted = false; // Default
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AboutUs aboutUs = (AboutUs) o;
        return id == aboutUs.id && deleted == aboutUs.deleted && Objects.equals(uuid, aboutUs.uuid) && Objects.equals(address, aboutUs.address) && Objects.equals(officeHours, aboutUs.officeHours) && Objects.equals(email, aboutUs.email) && Objects.equals(telephone, aboutUs.telephone) && Objects.equals(whatsApp, aboutUs.whatsApp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, address, officeHours, email, telephone, whatsApp, deleted);
    }

    @Override
    public String toString() {
        return "AboutUs{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", address='" + address + '\'' +
                ", officeHours='" + officeHours + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", whatsApp='" + whatsApp + '\'' +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static class Builder {

        private int id;
        private UUID uuid;
        private String address;
        private String officeHours;
        private String email;
        private String telephone;
        private String whatsApp;
        private boolean deleted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setOfficeHours(String officeHours) {
            this.officeHours = officeHours;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setTelephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public Builder setWhatsApp(String whatsApp) {
            this.whatsApp = whatsApp;
            return this;
        }

        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder copy(AboutUs aboutUs) {
            this.id = aboutUs.id;
            this.uuid = aboutUs.uuid;
            this.address = aboutUs.address;
            this.officeHours = aboutUs.officeHours;
            this.email = aboutUs.email;
            this.telephone = aboutUs.telephone;
            this.whatsApp = aboutUs.whatsApp;
            this.deleted = aboutUs.deleted;
            this.createdAt = aboutUs.createdAt;
            this.updatedAt = aboutUs.updatedAt;
            return this;
        }

        public AboutUs build() {
            return new AboutUs(this);
        }
    }
}
