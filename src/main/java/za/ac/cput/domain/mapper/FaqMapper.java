package za.ac.cput.domain.mapper;

import za.ac.cput.domain.entity.Faq; // Your Faq Entity
import za.ac.cput.domain.dto.request.FaqCreateDTO;
import za.ac.cput.domain.dto.request.FaqUpdateDTO;
import za.ac.cput.domain.dto.response.FaqResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class FaqMapper {

    /**
     * Converts an Faq entity to an FaqResponseDTO.
     */
    public static FaqResponseDTO toDto(Faq faq) {
        if (faq == null) {
            return null;
        }
        // Using Lombok's @Builder on FaqResponseDTO
        return FaqResponseDTO.builder()
                .uuid(faq.getUuid())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }

    /**
     * Converts a list of Faq entities to a list of FaqResponseDTOs.
     */
    public static List<FaqResponseDTO> toDtoList(List<Faq> faqs) {
        if (faqs == null) {
            return null;
        }
        return faqs.stream()
                .map(FaqMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts an FaqCreateDTO to a new Faq entity using the Faq.Builder.
     * UUID, createdAt, updatedAt, and deleted flag are handled by the entity's
     * @PrePersist or builder defaults.
     *
     * @param createDto The FaqCreateDTO containing data for the new FAQ.
     * @return A new Faq entity.
     */
    public static Faq toEntity(FaqCreateDTO createDto) {
        if (createDto == null) {
            return null;
        }
        return new Faq.Builder()
                .setQuestion(createDto.getQuestion())
                .setAnswer(createDto.getAnswer())
                .setDeleted(false) // Default for new FAQs
                // uuid, createdAt, updatedAt will be set by @PrePersist
                .build();
    }

    /**
     * Creates a NEW Faq entity instance by applying updates from an FaqUpdateDTO
     * to the data from an existing Faq entity, using the Faq.Builder.
     * This is for immutable updates.
     *
     * @param updateDto     The FaqUpdateDTO containing fields to update.
     * @param existingFaq The current state of the Faq entity fetched from the database.
     * @return A new Faq instance with updated values, based on the existing Faq.
     */
    public static Faq applyUpdateDtoToEntity(FaqUpdateDTO updateDto, Faq existingFaq) {
        if (updateDto == null || existingFaq == null) {
            // Or handle more gracefully, perhaps return existingFaq if dto is null
            throw new IllegalArgumentException("FaqUpdateDTO and existing Faq entity must not be null.");
        }

        // Start building a new Faq instance, copying from the existing one
        Faq.Builder builder = new Faq.Builder().copy(existingFaq);

        // Apply updates from DTO if fields are provided
        if (updateDto.getQuestion() != null) {
            builder.setQuestion(updateDto.getQuestion());
        }
        if (updateDto.getAnswer() != null) {
            builder.setAnswer(updateDto.getAnswer());
        }
        // ID, UUID, createdAt, deleted status are preserved from existingFaq by .copy()
        // updatedAt will be handled by @PreUpdate in the Faq entity when the new instance is saved.

        return builder.build(); // Returns a new Faq instance with the merged data
    }
}