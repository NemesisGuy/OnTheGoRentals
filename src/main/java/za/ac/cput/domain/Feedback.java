package za.ac.cput.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.util.Objects;
import java.util.UUID;

@Entity

public class Feedback {
    @Id
    private int id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();
    private String name;
    private String comment;
    private boolean deleted = false;
    @PrePersist
    protected  void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }
    protected Feedback() {

    }

    private Feedback(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.comment = builder.comment;
        this.deleted = builder.deleted;

    }


    public int getId() {
        return id;
    }
    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return id == feedback.id && deleted == feedback.deleted && Objects.equals(uuid, feedback.uuid) && Objects.equals(name, feedback.name) && Objects.equals(comment, feedback.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, name, comment, deleted);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    public static class Builder {
        private int id;
        private UUID uuid;
        private String name;
        private String comment;
        private boolean deleted;

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
        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder copy(Feedback feedback) {
            this.name = feedback.name;
            this.comment = feedback.comment;
            this.id = feedback.id;
            this.deleted = feedback.deleted;
            return this;
        }

        public Feedback build() {


            return new Feedback(this);
        }

    }
}