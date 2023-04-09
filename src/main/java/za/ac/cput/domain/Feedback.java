package za.ac.cput.domain;

/**
 * Feedback.java
 * POJO Class for Feedback
 * Author: Shamiso Moyo Chaka (220365393)
 * Date: 01 April 2023
 */

public class Feedback {
    private String name;
    private String comment;
    private int id;

    private Feedback(String name, String comment, int id) {
        this.name = name;
        this.comment = comment;
        this.id = id;
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

    public String toString() {
        return this.name + ": " + this.comment + "; " + this.id;
    }

    public static class FeedbackBuilder {
        private String name;
        private String comment;
        private int id;

        public FeedbackBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public FeedbackBuilder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public FeedbackBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public Feedback build() {
            return new Feedback(name, comment, id);
        }

        @Override
        public String toString() {
            return "FeedbackBuilder{" +
                    "name='" + name + '\'' +
                    ", comment='" + comment + '\'' +
                    ", id=" + id +
                    '}';
        }

    }
}
