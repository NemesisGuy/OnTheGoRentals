package za.ac.cput.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
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
    private UUID uuid = UUID.randomUUID();
    private String title;
    private String content;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted = false;

    @PrePersist
    protected  void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }
    public HelpCenter() {
    }

    public HelpCenter(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.category = builder.category;
        this.title = builder.title;
        this.content = builder.content;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.deleted = builder.deleted;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HelpCenter that = (HelpCenter) o;
        return id == that.id && deleted == that.deleted && Objects.equals(uuid, that.uuid) && Objects.equals(title, that.title) && Objects.equals(content, that.content) && Objects.equals(category, that.category) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, title, content, category, createdAt, updatedAt, deleted);
    }

    @Override
    public String toString() {
        return "HelpCenter{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deleted=" + deleted +
                '}';
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
