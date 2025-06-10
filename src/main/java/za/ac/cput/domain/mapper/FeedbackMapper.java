package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.FeedbackCreateDTO;
import za.ac.cput.domain.dto.response.FeedbackResponseDTO;
import za.ac.cput.domain.entity.Feedback;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackMapper {

    /**
     * Converts a Feedback entity to a FeedbackResponseDTO.
     */
    public static FeedbackResponseDTO toDto(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        return FeedbackResponseDTO.builder()
                .uuid(feedback.getUuid())
                .name(feedback.getName())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt()) // Map createdAt
                .build();
    }

    /**
     * Converts a list of Feedback entities to a list of FeedbackResponseDTOs.
     */
    public static List<FeedbackResponseDTO> toDtoList(List<Feedback> feedbackList) {
        if (feedbackList == null) {
            return null;
        }
        return feedbackList.stream()
                .map(FeedbackMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a FeedbackCreateDTO to a new Feedback entity using the Feedback.Builder.
     * UUID, createdAt, and deleted flag are handled by the entity's @PrePersist or builder defaults.
     */
    public static Feedback toEntity(FeedbackCreateDTO createDto) {
        if (createDto == null) {
            return null;
        }
        return new Feedback.Builder()
                .setName(createDto.getName())
                .setComment(createDto.getComment())
                .setDeleted(false) // Default for new feedback
                // uuid and createdAt will be set by @PrePersist
                .build();
    }

    // If you add an update functionality for Feedback (e.g., for an admin to correct a typo)
    /*
    public static Feedback applyUpdateDtoToEntity(FeedbackUpdateDTO updateDto, Feedback existingFeedback) {
        if (updateDto == null || existingFeedback == null) {
            throw new IllegalArgumentException("FeedbackUpdateDTO and existing Feedback entity must not be null.");
        }
        Feedback.Builder builder = new Feedback.Builder().copy(existingFeedback);
        if (updateDto.getName() != null) {
            builder.name(updateDto.getName());
        }
        if (updateDto.getComment() != null) {
            builder.comment(updateDto.getComment());
        }
        return builder.build();
    }
    */
}