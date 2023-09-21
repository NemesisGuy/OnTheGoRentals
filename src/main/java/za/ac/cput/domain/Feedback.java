package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity

public class Feedback {
    private String name;
    private String comment;

    @Id
    private int id;

    protected Feedback() {

    }

    private Feedback(Builder builder) {
        this.name = builder.name;
        this.comment = builder.comment;
        this.id = builder.id;
    }


    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public int getId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return id == feedback.id && Objects.equals(name, feedback.name) && Objects.equals(comment, feedback.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, comment, id);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", id=" + id +
                '}';
    }

    public static class Builder {
        private String name;
        private String comment;
        private int id;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder copy(Feedback feedback) {
            this.name = feedback.name;
            this.comment = feedback.comment;
            this.id = feedback.id;
            return this;
        }

        public Feedback build() {


            return new Feedback(this);
        }

    }}