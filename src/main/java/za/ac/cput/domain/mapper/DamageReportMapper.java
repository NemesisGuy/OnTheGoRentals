package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.DamageReportCreateDTO;
import za.ac.cput.domain.dto.request.DamageReportUpdateDTO;
import za.ac.cput.domain.dto.response.DamageReportResponseDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO; // For nested DTO
import za.ac.cput.domain.entity.DamageReport;
import za.ac.cput.domain.entity.Rental;

import java.util.List;
import java.util.stream.Collectors;

public class DamageReportMapper {

    public static DamageReportResponseDTO toDto(DamageReport damageReport) {
        if (damageReport == null) return null;

        RentalResponseDTO rentalDto = (damageReport.getRental() != null) ?
                RentalMapper.toDto(damageReport.getRental()) : null;

        return DamageReportResponseDTO.builder()
                .uuid(damageReport.getUuid()) // Use DamageReport's UUID
                .rental(rentalDto)
                .description(damageReport.getDescription())
                .dateAndTime(damageReport.getDateAndTime())
                .location(damageReport.getLocation())
                .repairCost(damageReport.getRepairCost())
                // .createdAt(damageReport.getCreatedAt()) // Add if in ResponseDTO
                .build();
    }

    public static List<DamageReportResponseDTO> toDtoList(List<DamageReport> damageReports) {
        if (damageReports == null) return null;
        return damageReports.stream().map(DamageReportMapper::toDto).collect(Collectors.toList());
    }

    public static DamageReport toEntity(DamageReportCreateDTO createDto, Rental rentalEntity) {
        if (createDto == null) return null;
        if (rentalEntity == null) {
            throw new IllegalArgumentException("Rental entity is required to create a damage report.");
        }
        // Using DamageReport's static Builder class
        return new DamageReport.Builder()
                .setRental(rentalEntity) // Set the fetched Rental entity
                .setDescription(createDto.getDescription())
                .setDateAndTime(createDto.getDateAndTime()) // Mapper takes what DTO provides
                .setLocation(createDto.getLocation())
                .setRepairCost(createDto.getRepairCost())
                // uuid, id, createdAt, deleted are handled by entity @PrePersist or defaults
                .build();
    }

    public static DamageReport applyUpdateDtoToEntity(DamageReportUpdateDTO updateDto, DamageReport existingReport) {
        if (updateDto == null || existingReport == null) {
            throw new IllegalArgumentException("Update DTO and existing DamageReport entity must not be null.");
        }

        DamageReport.Builder builder = new DamageReport.Builder().copy(existingReport);

        // Only update fields if they are provided in the DTO
        if (updateDto.getDescription() != null) builder.setDescription(updateDto.getDescription());
        if (updateDto.getDateAndTime() != null) builder.setDateAndTime(updateDto.getDateAndTime());
        if (updateDto.getLocation() != null) builder.setLocation(updateDto.getLocation());
        if (updateDto.getRepairCost() != null) builder.setRepairCost(updateDto.getRepairCost());
        // The associated Rental usually doesn't change for an existing damage report.
        // id, uuid, createdAt, deleted are preserved from existingReport by .copy()

        return builder.build(); // Returns a new DamageReport instance
    }
}