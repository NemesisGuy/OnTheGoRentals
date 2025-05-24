package za.ac.cput.domain.mapper;



import za.ac.cput.domain.*;

import za.ac.cput.domain.dto.request.AboutUsRequestDTO;
import za.ac.cput.domain.dto.response.AboutUsResponseDTO;

import java.util.UUID;
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

    public static AboutUs toEntity(AboutUsRequestDTO dto) {
        if (dto == null) return null;
        return new  AboutUs.Builder()
                .setUuid(UUID.randomUUID())
                .setAddress(dto.getAddress())
                .setOfficeHours(dto.getOfficeHours())
                .setEmail(dto.getEmail())
                .setTelephone(dto.getTelephone())
                .setWhatsApp(dto.getWhatsApp())

                .build();

    }
}
