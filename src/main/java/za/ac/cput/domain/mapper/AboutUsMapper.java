package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.AboutUsCreateDTO;
import za.ac.cput.domain.dto.request.AboutUsUpdateDTO;
import za.ac.cput.domain.dto.response.AboutUsResponseDTO;
import za.ac.cput.domain.entity.AboutUs;

import java.util.List;
import java.util.stream.Collectors;

public class AboutUsMapper {

    public static AboutUsResponseDTO toDto(AboutUs aboutUs) {
        if (aboutUs == null) return null;
        return AboutUsResponseDTO.builder()
                .uuid(aboutUs.getUuid())
                .address(aboutUs.getAddress())
                .officeHours(aboutUs.getOfficeHours())
                .email(aboutUs.getEmail())
                .telephone(aboutUs.getTelephone())
                .whatsApp(aboutUs.getWhatsApp())
                .build();
    }

    public static List<AboutUsResponseDTO> toDtoList(List<AboutUs> aboutUsList) {
        if (aboutUsList == null) return null;
        return aboutUsList.stream().map(AboutUsMapper::toDto).collect(Collectors.toList());
    }

    public static AboutUs toEntity(AboutUsCreateDTO createDto) {
        if (createDto == null) return null;
        return new AboutUs.Builder()
                .setAddress(createDto.getAddress())
                .setOfficeHours(createDto.getOfficeHours())
                .setEmail(createDto.getEmail())
                .setTelephone(createDto.getTelephone())
                .setWhatsApp(createDto.getWhatsApp())
                // uuid is set by @PrePersist, deleted defaults to false
                .build();
    }

    public static AboutUs applyUpdateDtoToEntity(AboutUsUpdateDTO updateDto, AboutUs existingAboutUs) {
        if (updateDto == null || existingAboutUs == null) {
            throw new IllegalArgumentException("Update DTO and existing AboutUs entity must not be null.");
        }
        AboutUs.Builder builder = new AboutUs.Builder().copy(existingAboutUs);
        if (updateDto.getAddress() != null) builder.setAddress(updateDto.getAddress());
        if (updateDto.getOfficeHours() != null) builder.setOfficeHours(updateDto.getOfficeHours());
        if (updateDto.getEmail() != null) builder.setEmail(updateDto.getEmail());
        if (updateDto.getTelephone() != null) builder.setTelephone(updateDto.getTelephone());
        if (updateDto.getWhatsApp() != null) builder.setWhatsApp(updateDto.getWhatsApp());
        return builder.build();
    }
}