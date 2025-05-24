package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Feedback;
import za.ac.cput.domain.dto.request.FeedbackRequestDTO;
import za.ac.cput.domain.dto.response.FeedbackResponseDTO;

import java.util.UUID;

public class FeedbackMapper {
    public static FeedbackResponseDTO toDto(Feedback feedback) {
        if (feedback == null) return null;
        return FeedbackResponseDTO.builder()
                .uuid(feedback.getUuid())
                .name(feedback.getName())
                .comment(feedback.getComment())
                .build();
    }

    public static Feedback toEntity(FeedbackRequestDTO dto) {
        if (dto == null) return null;
        return new Feedback.Builder()
                .setUuid(UUID.randomUUID())
                .setName(dto.getName())
                .setComment(dto.getComment())
                .build();

    }
}