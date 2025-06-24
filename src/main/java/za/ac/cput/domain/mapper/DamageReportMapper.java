package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.DamageReportCreateDTO;
import za.ac.cput.domain.dto.request.DamageReportUpdateDTO;
import za.ac.cput.domain.dto.response.DamageReportResponseDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.entity.DamageReport;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.service.IFileStorageService;

import java.util.List;
import java.util.stream.Collectors;

public class DamageReportMapper {

    /**
     * Converts a DamageReport entity to its DTO representation.
     * This now requires an IFileStorageService to correctly map the nested Rental DTO.
     *
     * @param damageReport       The DamageReport entity.
     * @param fileStorageService The service for generating image URLs.
     * @return A DamageReportResponseDTO.
     */
    public static DamageReportResponseDTO toDto(DamageReport damageReport, IFileStorageService fileStorageService, String publicApiUrl) {
        if (damageReport == null) return null;

        // === THE FIX IS HERE ===
        // Pass the fileStorageService down to the RentalMapper
        RentalResponseDTO rentalDto = (damageReport.getRental() != null) ?
                RentalMapper.toDto(damageReport.getRental(), fileStorageService, publicApiUrl) : null;

        return DamageReportResponseDTO.builder()
                .uuid(damageReport.getUuid())
                .rental(rentalDto)
                .description(damageReport.getDescription())
                .dateAndTime(damageReport.getDateAndTime())
                .location(damageReport.getLocation())
                .repairCost(damageReport.getRepairCost())
                .build();
    }

    /**
     * Converts a list of DamageReport entities to a list of DTOs.
     *
     * @param damageReports      The list of DamageReport entities.
     * @param fileStorageService The service for generating image URLs.
     * @return A list of DamageReportResponseDTOs.
     */
    public static List<DamageReportResponseDTO> toDtoList(List<DamageReport> damageReports, IFileStorageService fileStorageService, String publicApiUrl) {
        if (damageReports == null) return null;
        // Use a lambda to pass the service to each toDto call
        return damageReports.stream()
                .map(report -> DamageReportMapper.toDto(report, fileStorageService, publicApiUrl))
                .collect(Collectors.toList());
    }

    // These methods do not need the file service, so their signatures remain unchanged.
    public static DamageReport toEntity(DamageReportCreateDTO createDto, Rental rentalEntity) {
        if (createDto == null) return null;
        if (rentalEntity == null) {
            throw new IllegalArgumentException("Rental entity is required to create a damage report.");
        }
        return new DamageReport.Builder()
                .setRental(rentalEntity)
                .setDescription(createDto.getDescription())
                .setDateAndTime(createDto.getDateAndTime())
                .setLocation(createDto.getLocation())
                .setRepairCost(createDto.getRepairCost())
                .build();
    }

    public static DamageReport applyUpdateDtoToEntity(DamageReportUpdateDTO updateDto, DamageReport existingReport) {
        if (updateDto == null || existingReport == null) {
            throw new IllegalArgumentException("Update DTO and existing DamageReport entity must not be null.");
        }

        DamageReport.Builder builder = new DamageReport.Builder().copy(existingReport);

        if (updateDto.getDescription() != null) builder.setDescription(updateDto.getDescription());
        if (updateDto.getDateAndTime() != null) builder.setDateAndTime(updateDto.getDateAndTime());
        if (updateDto.getLocation() != null) builder.setLocation(updateDto.getLocation());
        if (updateDto.getRepairCost() != null) builder.setRepairCost(updateDto.getRepairCost());

        return builder.build();
    }
}