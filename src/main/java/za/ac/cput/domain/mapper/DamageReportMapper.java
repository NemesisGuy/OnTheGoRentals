package za.ac.cput.domain.mapper;

import za.ac.cput.domain.DamageReport;
import za.ac.cput.domain.Rental; // Entity
import za.ac.cput.domain.dto.request.DamageReportRequestDTO;
import za.ac.cput.domain.dto.response.DamageReportResponseDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO; // For toDto method

import java.util.List;
import java.util.stream.Collectors;

public class DamageReportMapper {

    /**
     * Converts a DamageReport entity to a DamageReportResponseDTO.
     */
    public static DamageReportResponseDTO toDto(DamageReport damageReport) {
        if (damageReport == null) {
            return null;
        }

        RentalResponseDTO rentalDto = null;
        if (damageReport.getRental() != null) {
            rentalDto = RentalMapper.toDto(damageReport.getRental()); // Uses RentalMapper
        }

        return DamageReportResponseDTO.builder()
                .uuid(damageReport.getUuid()) // UUID of the DamageReport
                .rental(rentalDto)
                .description(damageReport.getDescription())
                .dateAndTime(damageReport.getDateAndTime())
                .location(damageReport.getLocation())
                .repairCost(damageReport.getRepairCost())
                .build();
    }

    /**
     * Converts a DamageReportRequestDTO (and the related fetched Rental entity)
     * to a new DamageReport entity.
     * Typically used for creating a new damage report.
     *
     * @param requestDto   The DTO containing data for the new damage report.
     * @param rentalEntity The fetched Rental entity this damage report is associated with.
     * @return A new DamageReport entity.
     */
    public static DamageReport toEntity(DamageReportRequestDTO requestDto, Rental rentalEntity) {
        if (requestDto == null) {
            return null;
        }
        if (rentalEntity == null) {
            throw new IllegalArgumentException("Rental entity cannot be null for creating a DamageReport.");
        }

        DamageReport damageReport = new DamageReport.Builder()
                .setRental(rentalEntity)
                .setDescription(requestDto.getDescription())
                .setDateAndTime(requestDto.getDateAndTime() != null ? requestDto.getDateAndTime() : java.time.LocalDateTime.now()) // Default if not provided
                .setLocation(requestDto.getLocation())
                .setRepairCost(requestDto.getRepairCost())
                .setDeleted(false) // Default for new reports
                .setUuid(null) // UUID will be set by @PrePersist in the entity
                .build();
        // UUID for the new DamageReport will be set by @PrePersist in the entity.

        // Note: The rentalEntity is set in the DamageReport entity.

        return damageReport;
    }

    /**
     * Updates an existing DamageReport entity from a DamageReportRequestDTO (or a specific UpdateDTO).
     * The DamageReport entity to update should be fetched from the database first.
     *
     * @param updateDto            The DTO with update information.
     * @param existingDamageReport The existing DamageReport entity to update.
     * @param updatedRentalEntity  Optional: The Rental entity if it can be changed (usually not for a damage report).
     */
    public static void updateEntityFromDto(DamageReportRequestDTO updateDto, DamageReport existingDamageReport, Rental updatedRentalEntity) {
        if (updateDto == null || existingDamageReport == null) {
            return;
        }

        // Typically, the associated Rental for a damage report does not change.
        // If it could, you'd update it here:
        // if (updatedRentalEntity != null) {
        //     existingDamageReport.setRental(updatedRentalEntity);
        // }

        if (updateDto.getDescription() != null) {

            DamageReport DamageReport = new DamageReport.Builder()
                    .copy(existingDamageReport)
                    .setDescription(updateDto.getDescription())
                    .build();
            existingDamageReport = DamageReport;
        }
        if (updateDto.getDateAndTime() != null) {
            DamageReport DamageReport = new DamageReport.Builder()
                    .copy(existingDamageReport)
                    .setDateAndTime(updateDto.getDateAndTime())
                    .build();
            existingDamageReport = DamageReport;
        }
        if (updateDto.getLocation() != null) {
            DamageReport DamageReport = new DamageReport.Builder()
                    .copy(existingDamageReport)
                    .setLocation(updateDto.getLocation())
                    .build();
            existingDamageReport = DamageReport;
        }
        // repairCost is a primitive double, so it will always have a value from DTO.
        // Consider if 0 is a valid update or if it should only update if > 0, etc.
        DamageReport DamageReport = new DamageReport.Builder()
                .copy(existingDamageReport)
                .setRepairCost(updateDto.getRepairCost())
                .build();
        existingDamageReport = DamageReport;

        // UUID and id are not updated from DTO.
        // 'deleted' flag handled by a separate delete endpoint.
    }

    public static List<DamageReportResponseDTO> toDtoList(List<DamageReport> damageReports) {
        if (damageReports == null) {
            return null;
        }
        return damageReports.stream()
                .map(DamageReportMapper::toDto)
                .collect(Collectors.toList());
    }
}