package za.ac.cput.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * HelpCenter.java
 * Entity for the HelpCenter
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@Getter
@Entity
public class HelpCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private boolean deleted;

    protected HelpCenter() {} // For JPA

    private HelpCenter(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.title = builder.title;
        this.content = builder.content;
        this.category = builder.category;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.deleted = builder.deleted;
    }

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) this.uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.deleted = false; // Default
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static class Builder {
        private int id;
        private UUID uuid;
        private String category;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean deleted ;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }
        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
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
        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder copy(HelpCenter helpCenter) {
            this.id = helpCenter.id;
            this.uuid = helpCenter.uuid;
            this.category = helpCenter.category;
            this.title = helpCenter.title;
            this.content = helpCenter.content;
            this.createdAt = helpCenter.createdAt;
            this.updatedAt = helpCenter.updatedAt;
            this.deleted = helpCenter.deleted;
            return this;
        }

        public HelpCenter build() {
            return new HelpCenter(this);
        }
    }
}
