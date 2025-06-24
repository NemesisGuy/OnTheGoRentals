package za.ac.cput.factory.impl;

import org.springframework.stereotype.Component;
import za.ac.cput.domain.entity.Faq;
import za.ac.cput.factory.IFactory;

import java.time.LocalDateTime;

/*
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 * File: FaqFactory.java
 * */

@Component
public class FaqFactory implements IFactory<Faq> {
    public static Faq faqCreated(int id, String question, String answer, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Faq.Builder()
                .setId(id)
                .setQuestion(question)
                .setAnswer(answer)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .build();
    }

    public static Faq createFaq(String question, String answer, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Faq.Builder()
                .setQuestion(question)
                .setAnswer(answer)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .build();
    }

    @Override
    public Faq create() {
        return Faq.builder().build();
    }

    public Faq create(Faq faq) {
        return Faq.builder().copy(faq).build();
    }
}
