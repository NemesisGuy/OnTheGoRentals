package za.ac.cput.domain.mapper;

import za.ac.cput.domain.entity.ContactUs;
import za.ac.cput.domain.dto.request.ContactUsCreateDTO;
import za.ac.cput.domain.dto.request.AdminContactUsUpdateDTO; // Using admin update DTO
import za.ac.cput.domain.dto.response.ContactUsResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ContactUsMapper {

    public static ContactUsResponseDTO toDto(ContactUs contactUs) {
        if (contactUs == null) return null;
        return ContactUsResponseDTO.builder()
                .uuid(contactUs.getUuid())
                .title(contactUs.getTitle())
                .firstName(contactUs.getFirstName())
                .lastName(contactUs.getLastName())
                .email(contactUs.getEmail())
                .subject(contactUs.getSubject())
                .message(contactUs.getMessage())
                // .createdAt(contactUs.getCreatedAt()) // Add if in ResponseDTO
                .build();
    }

    public static List<ContactUsResponseDTO> toDtoList(List<ContactUs> contactUsList) {
        if (contactUsList == null) return null;
        return contactUsList.stream().map(ContactUsMapper::toDto).collect(Collectors.toList());
    }

    // For public creation via ContactUsController
    public static ContactUs toEntity(ContactUsCreateDTO createDto) {
        if (createDto == null) return null;
        return new ContactUs.Builder()
                .setTitle(createDto.getTitle())
                .setFirstName(createDto.getFirstName())
                .setLastName(createDto.getLastName())
                .setEmail(createDto.getEmail())
                .setSubject(createDto.getSubject())
                .setMessage(createDto.getMessage())
                // uuid, id, createdAt, deleted handled by entity/JPA
                .build();
    }

    // For Admin update
    public static ContactUs applyAdminUpdateDtoToEntity(AdminContactUsUpdateDTO updateDto, ContactUs existingContactUs) {
        if (updateDto == null || existingContactUs == null) {
            throw new IllegalArgumentException("Update DTO and existing ContactUs entity must not be null.");
        }
        ContactUs.Builder builder = new ContactUs.Builder().copy(existingContactUs);
        if (updateDto.getTitle() != null) builder.setTitle(updateDto.getTitle());
        if (updateDto.getFirstName() != null) builder.setFirstName(updateDto.getFirstName());
        if (updateDto.getLastName() != null) builder.setLastName(updateDto.getLastName());
        if (updateDto.getEmail() != null) builder.setEmail(updateDto.getEmail());
        if (updateDto.getSubject() != null) builder.setSubject(updateDto.getSubject());
        if (updateDto.getMessage() != null) builder.setMessage(updateDto.getMessage());
        // if (updateDto.getRespondedTo() != null) builder.respondedTo(updateDto.getRespondedTo()); // If you add such a field
        return builder.build();
    }
}