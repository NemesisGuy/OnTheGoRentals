package za.ac.cput.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Faq.java
 * Entity for the Faq
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */
@Getter
@Entity
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();
    @Column(columnDefinition = "TEXT")

    private String question;
    @Column(columnDefinition = "TEXT")

    private String answer;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now; // Set createdAt only on persist
        this.updatedAt = now; // Set updatedAt on persist as well
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(); // Update updatedAt on any update
    }


    public Faq() {
    }

    public Faq(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.question = builder.question;
        this.answer = builder.answer;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.deleted = builder.deleted;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Faq faq = (Faq) o;
        return id == faq.id && deleted == faq.deleted && Objects.equals(uuid, faq.uuid) && Objects.equals(question, faq.question) && Objects.equals(answer, faq.answer) && Objects.equals(createdAt, faq.createdAt) && Objects.equals(updatedAt, faq.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, question, answer, createdAt, updatedAt, deleted);
    }

    @Override
    public String toString() {
        return "Faq{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deleted=" + deleted +
                '}';
    }

    public static class Builder {
        private int id;
        private UUID uuid;
        private String question;
        private String answer;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean deleted;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }
        public Builder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setQuestion(String question) {
            this.question = question;
            return this;
        }

        public Builder setAnswer(String answer) {
            this.answer = answer;
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

        public Builder copy(Faq faq) {
            this.id = faq.id;
            this.uuid = faq.uuid;
            this.question = faq.question;
            this.answer = faq.answer;
            this.createdAt = faq.createdAt;
            this.updatedAt = faq.updatedAt;
            this.deleted = faq.deleted;
            return this;
        }

        public Faq build() {
            return new Faq(this);
        }


    }
}
