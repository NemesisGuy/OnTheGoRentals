package za.ac.cput.domain.mapper;

import za.ac.cput.domain.ContactUs;
import za.ac.cput.domain.dto.request.ContactUsRequestDTO;
import za.ac.cput.domain.dto.response.ContactUsResponseDTO;

import java.util.UUID;

public class ContactUsMapper {
    public static ContactUsResponseDTO toDto(ContactUs contact) {
        if (contact == null) return null;
        return ContactUsResponseDTO.builder()
                .uuid(contact.getUuid())
                .title(contact.getTitle())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .subject(contact.getSubject())
                .message(contact.getMessage())
                .build();
    }

    public static ContactUs toEntity(ContactUsRequestDTO dto) {
        if (dto == null) return null;
        return new ContactUs.Builder()
                .setUuid(UUID.randomUUID())
                .setTitle(dto.getTitle())
                .setFirstName(dto.getFirstName())
                .setLastName(dto.getLastName())
                .setEmail(dto.getEmail())
                .setSubject(dto.getSubject())
                .setMessage(dto.getMessage())
                .build();

    }
}
