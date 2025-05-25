package za.ac.cput.domain.mapper;

import za.ac.cput.domain.entity.HelpCenter; // Entity
import za.ac.cput.domain.dto.request.HelpCenterCreateDTO;
import za.ac.cput.domain.dto.request.HelpCenterUpdateDTO;
import za.ac.cput.domain.dto.response.HelpCenterResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class HelpCenterMapper {

    public static HelpCenterResponseDTO toDto(HelpCenter helpCenter) {
        if (helpCenter == null) {
            return null;
        }
        // Using Lombok @Builder on HelpCenterResponseDTO
        return HelpCenterResponseDTO.builder()
                .uuid(helpCenter.getUuid())
                .title(helpCenter.getTitle())
                .content(helpCenter.getContent())
                .category(helpCenter.getCategory())
                .createdAt(helpCenter.getCreatedAt())
                .updatedAt(helpCenter.getUpdatedAt())
                .build();
    }

    public static List<HelpCenterResponseDTO> toDtoList(List<HelpCenter> helpCenterList) {
        if (helpCenterList == null) {
            return null;
        }
        return helpCenterList.stream()
                .map(HelpCenterMapper::toDto)
                .collect(Collectors.toList());
    }

    public static HelpCenter toEntity(HelpCenterCreateDTO createDto) {
        if (createDto == null) {
            return null;
        }
        // Uses the entity's static Builder class
        return new HelpCenter.Builder()
                .setTitle(createDto.getTitle())
                .setContent(createDto.getContent())
                .setCategory(createDto.getCategory())
                // uuid, id, createdAt, updatedAt, deleted are handled by entity's @PrePersist or defaults
                .build();
    }

    public static HelpCenter applyUpdateDtoToEntity(HelpCenterUpdateDTO updateDto, HelpCenter existingHelpCenter) {
        if (updateDto == null || existingHelpCenter == null) {
            throw new IllegalArgumentException("Update DTO and existing HelpCenter entity must not be null.");
        }

        HelpCenter.Builder builder = new HelpCenter.Builder().copy(existingHelpCenter);

        if (updateDto.getTitle() != null) {
            builder.setTitle(updateDto.getTitle());
        }
        if (updateDto.getContent() != null) {
            builder.setContent(updateDto.getContent());
        }
        if (updateDto.getCategory() != null) {
            builder.setCategory(updateDto.getCategory());
        }
        // id, uuid, createdAt, deleted are preserved from existingHelpCenter by .copy()
        // updatedAt will be handled by @PreUpdate in the entity when the new instance is saved.

        return builder.build(); // Returns a new HelpCenter instance
    }
}