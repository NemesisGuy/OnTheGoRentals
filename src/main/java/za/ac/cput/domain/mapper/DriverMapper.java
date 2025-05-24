package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Driver;
import za.ac.cput.domain.dto.request.DriverCreateDTO;
import za.ac.cput.domain.dto.request.DriverRequestDTO;
import za.ac.cput.domain.dto.request.DriverUpdateDTO;
import za.ac.cput.domain.dto.response.DriverResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public static List<DriverResponseDTO> toDtoList(List<Driver> drivers) {
        if (drivers == null) return null;
        return drivers.stream().map(DriverMapper::toDto).collect(Collectors.toList());
    }

    // Maps DriverCreateDTO to a new Driver entity for creation
    public static Driver toEntity(DriverCreateDTO createDto) {
        if (createDto == null) return null;
        Driver driver = new Driver.Builder()
                .setFirstName(createDto.getFirstName())
                .setLastName(createDto.getLastName())
                .setLicenseCode(createDto.getLicenseCode())
                .setDeleted(false)
                .build();
        // UUID will be set by @PrePersist in Driver entity
       // Default for new entities
        return driver;
    }
    /**
     * Creates a new Driver instance by applying non-null values from a DriverUpdateDTO to an existing Driver.
     *
     * @param updateDto     The DTO containing updated field values.
     * @param existingDriver The existing Driver to base the new instance on.
     * @return A new Driver instance with updated values.
     * @throws IllegalArgumentException If updateDto or existingDriver is null.
     */
    public static Driver updateEntityFromDto(DriverUpdateDTO updateDto, Driver existingDriver) {
        if (updateDto == null || existingDriver == null) {
            throw new IllegalArgumentException("DriverUpdateDTO and Driver must not be null");
        }

        Driver.Builder builder = new Driver.Builder()
                .setUuid(existingDriver.getUuid())
                .setDeleted(existingDriver.isDeleted())
                .setFirstName(existingDriver.getFirstName())
                .setLastName(existingDriver.getLastName())
                .setLicenseCode(existingDriver.getLicenseCode());

        if (updateDto.getFirstName() != null) {
            builder.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            builder.setLastName(updateDto.getLastName());
        }
        if (updateDto.getLicenseCode() != null) {
            builder.setLicenseCode(updateDto.getLicenseCode());
        }

        return builder.build();
    }


}