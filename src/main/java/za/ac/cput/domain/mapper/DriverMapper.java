package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Driver;
import za.ac.cput.domain.dto.request.DriverRequestDTO;
import za.ac.cput.domain.dto.response.DriverResponseDTO;

import java.util.UUID;

public class DriverMapper {
    public static DriverResponseDTO toDto(Driver driver) {
        if (driver == null) return null;
        return DriverResponseDTO.builder()
                .uuid(driver.getUuid())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .licenseCode(driver.getLicenseCode())
                .build();
    }

    public static Driver toEntity(DriverRequestDTO dto) {
        if (dto == null) return null;
        return new Driver.Builder()
                .setUuid(UUID.randomUUID())
                .setFirstName(dto.getFirstName())
                .setLastName(dto.getLastName())
                .setLicenseCode(dto.getLicenseCode())
                .build();


    }
}