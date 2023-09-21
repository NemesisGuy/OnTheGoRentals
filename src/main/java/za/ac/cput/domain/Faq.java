package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Faq.java
 * Entity for the Faq
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 */

@Entity
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Faq() {
    }

    public Faq(Builder builder) {
        this.id = builder.id;
        this.question = builder.question;
        this.answer = builder.answer;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faq faq = (Faq) o;
        return id == faq.id && Objects.equals(question, faq.question) && Objects.equals(answer, faq.answer) && Objects.equals(createdAt, faq.createdAt) && Objects.equals(updatedAt, faq.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, answer, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Faq{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static class Builder {
        private int id;
        private String question;
        private String answer;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder setId(int id) {
            this.id = id;
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

        public Builder copy(Faq faq) {
            this.id = faq.id;
            this.question = faq.question;
            this.answer = faq.answer;
            this.createdAt = faq.createdAt;
            this.updatedAt = faq.updatedAt;
            return this;
        }

        public Faq build() {
            return new Faq(this);
        }
    }
}
