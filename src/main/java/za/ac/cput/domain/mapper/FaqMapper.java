package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Faq;
import za.ac.cput.domain.dto.request.FaqRequestDTO;
import za.ac.cput.domain.dto.response.FaqResponseDTO;

import java.util.UUID;

public class FaqMapper {
    public static FaqResponseDTO toDto(Faq faq) {
        if (faq == null) return null;
        return FaqResponseDTO.builder()
                .uuid(faq.getUuid())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }

    public static Faq toEntity(FaqRequestDTO dto) {
        if (dto == null) return null;
        return new Faq.Builder()
                .setUuid(UUID.randomUUID())
                .setAnswer(dto.getAnswer())
                .setQuestion(dto.getQuestion())
                .setCreatedAt(dto.getCreatedAt())
                .setUpdatedAt(dto.getUpdatedAt())
                .build();

    }
}