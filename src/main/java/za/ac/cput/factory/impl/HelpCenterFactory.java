package za.ac.cput.factory.impl;

import org.springframework.stereotype.Component;
import za.ac.cput.domain.HelpCenter;
import za.ac.cput.factory.IFactory;

import java.time.LocalDateTime;

/*
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 * File: HelpCenter.java
 * */

@Component
public class HelpCenterFactory implements IFactory<HelpCenter> {
    public static HelpCenter helpCenterCreated(int id, String category, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new HelpCenter.Builder()
                .setId(id)
                .setCategory(category)
                .setTitle(title)
                .setContent(content)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .build();
    }

    public static HelpCenter createHelpCenter(String category, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new HelpCenter.Builder()
                .setCategory(category)
                .setTitle(title)
                .setContent(content)
                .setCreatedAt(createdAt)
                .setUpdatedAt(updatedAt)
                .build();
    }

    @Override
    public HelpCenter create() {
        return HelpCenter.builder().build();
    }

    public HelpCenter create(HelpCenter helpCenter) {
        return HelpCenter.builder().copy(helpCenter).build();
    }
}
