package za.ac.cput.domain.entity;

import jakarta.persistence.*;
import lombok.Getter; // Assuming you want public getters

import java.io.Serializable; // HelpCenter is an entity, Serializable is often included
import java.time.LocalDateTime;
import java.util.Objects; // For equals and hashCode
import java.util.UUID;

/**
 * HelpCenter.java
 * Represents a single topic, article, or entry in the Help Center/FAQ section of the application.
 * Each entry typically has a title (or question), content (or answer), and a category for organization.
 *
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@Getter
@Entity
public class HelpCenter implements Serializable { // Added Serializable for completeness

    private static final long serialVersionUID = 1L; // Recommended for Serializable classes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * The title or question of the help topic.
     */
    private String title;

    /**
     * The main content or answer for the help topic.
     * Stored as TEXT to accommodate longer content.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * The category to which this help topic belongs (e.g., "Account", "Bookings", "Payments").
     * Used for filtering and organization.
     */
    private String category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false; // Default to false

    /**
     * Default constructor for JPA.
     */
    protected HelpCenter() {}

    /**
     * Private constructor used by the static inner Builder class.
     *
     * @param builder The builder instance containing the state for the new HelpCenter entry.
     */
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

    /**
     * JPA callback executed before a new entity is persisted.
     * Sets default values for uuid, createdAt, updatedAt, and deleted flag.
     */
    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now; // For new entity, createdAt and updatedAt are same
        this.deleted = false; // Ensure default
    }

    /**
     * JPA callback executed before an existing entity is updated.
     * Sets the updatedAt timestamp to the current time.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // No public setters to maintain immutability after creation via Builder

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HelpCenter that = (HelpCenter) o;
        if (id != 0 && that.id != 0) { // If both have been persisted
            return id == that.id;
        }
        return Objects.equals(uuid, that.uuid); // Fallback to UUID
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "HelpCenter{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                // Avoid printing full content in toString for brevity
                ", content='" + (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                '}';
    }

    /**
     * Builder class for creating {@link HelpCenter} instances.
     * Follows the builder pattern to facilitate the construction of HelpCenter objects.
     */
    public static class Builder {
        private int id;
        private UUID uuid;
        private String category;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean deleted = false; // Default for builder

        /**
         * Default constructor for the Builder.
         */
        public Builder() {}

        public Builder setId(int id) { this.id = id; return this; }
        public Builder setUuid(UUID uuid) { this.uuid = uuid; return this; }
        public Builder setCategory(String category) { this.category = category; return this; }
        public Builder setTitle(String title) { this.title = title; return this; }
        public Builder setContent(String content) { this.content = content; return this; }
        public Builder setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder setDeleted(boolean deleted) { this.deleted = deleted; return this; }

        /**
         * Copies the state from an existing {@link HelpCenter} object into this builder.
         *
         * @param helpCenter The HelpCenter instance to copy from.
         * @return This builder instance for chaining.
         */
        public Builder copy(HelpCenter helpCenter) {
            if (helpCenter == null) return this;
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

        /**
         * Builds a new {@link HelpCenter} instance from the current state of this builder.
         * Sets `createdAt`, `updatedAt`, and `uuid` if they are not already set (for new entities).
         *
         * @return A new HelpCenter instance.
         */
        public HelpCenter build() {
            // Timestamps and UUID are handled by @PrePersist for new entities
            // or should be copied for existing ones. Builder can ensure they are present.
            // If this builder is used to create a NEW entity from scratch (not copy),
            // then @PrePersist will handle defaults.
            return new HelpCenter(this);
        }
    }
}