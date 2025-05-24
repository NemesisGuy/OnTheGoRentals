package za.ac.cput.domain.mapper;

import za.ac.cput.domain.HelpCenter;
import za.ac.cput.domain.dto.request.HelpCenterRequestDTO;
import za.ac.cput.domain.dto.response.HelpCenterResponseDTO;

import java.util.UUID;

public class HelpCenterMapper {
    public static HelpCenterResponseDTO toDto(HelpCenter helpCenter) {
        if (helpCenter == null) return null;
        return HelpCenterResponseDTO.builder()
                .uuid(helpCenter.getUuid())
                .title(helpCenter.getTitle())
                .content(helpCenter.getContent())
                .category(helpCenter.getCategory())
                .createdAt(helpCenter.getCreatedAt())
                .updatedAt(helpCenter.getUpdatedAt())
                .build();
    }

    public static HelpCenter toEntity(HelpCenterRequestDTO dto) {
        if (dto == null) return null;
        return new HelpCenter.Builder()
                .setUuid(UUID.randomUUID())
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setCategory(dto.getCategory())
                .setCreatedAt(dto.getCreatedAt())
                .setUpdatedAt(dto.getUpdatedAt())
                .build();
    }
}

