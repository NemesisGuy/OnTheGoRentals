package za.ac.cput.domain.entity; // Or your actual entity package

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    private String name; // Name of the person giving feedback

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private boolean deleted;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false)
    private LocalDateTime updatedAt;

    // Protected no-arg constructor for JPA
    protected Feedback() {
    }

    // Private constructor to force use of builder
    private Feedback(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.comment = builder.comment;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt; // Default to now if not set
        this.deleted = builder.deleted;
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
        this.deleted = false; // Default value for deleted
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Static Builder class
    public static class Builder {
        private int id;
        private UUID uuid;
        private String name;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt; // Optional, can be set by @PreUpdate
        private boolean deleted = false; // Default

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) { // Usually set by @PrePersist
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(LocalDateTime updatedAt) { // Optional, can be set by @PreUpdate
            this.updatedAt = updatedAt;
            return this;
        }


        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder copy(Feedback feedback) {
            this.id = feedback.getId();
            this.uuid = feedback.getUuid();
            this.name = feedback.getName();
            this.comment = feedback.getComment();
            this.createdAt = feedback.getCreatedAt();
            this.updatedAt = feedback.getUpdatedAt();
            this.deleted = feedback.isDeleted();
            return this;
        }

        public Feedback build() {
            if (name == null || name.trim().isEmpty()) {
                // throw new IllegalStateException("Name cannot be empty for feedback");
            }
            if (comment == null || comment.trim().isEmpty()) {
                // throw new IllegalStateException("Comment cannot be empty for feedback");
            }
            return new Feedback(this);
        }
    }
}