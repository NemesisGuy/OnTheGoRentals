package za.ac.cput.domain.mapper;

import za.ac.cput.domain.DamageReport;
import za.ac.cput.domain.dto.DamageReportDTO;
import za.ac.cput.domain.dto.RentalDTO;
import za.ac.cput.factory.impl.DamageReportFactory;

public class DamageReportMapper {

    public static DamageReportDTO toDto(DamageReport damageReport) {
        if (damageReport == null) return null;

        return DamageReportDTO.builder()
                .id(damageReport.getId())
                .rental(RentalMapper.toDto(damageReport.getRental()))
                .description(damageReport.getDescription())
                .dateAndTime(damageReport.getDateAndTime())
                .location(damageReport.getLocation())
                .repairCost(damageReport.getRepairCost())
                .build();
    }

    public static DamageReport toEntity(DamageReportDTO dto) {
        if (dto == null) return null;
//    public static DamageReport createReport(int id, Rental rental, String description, LocalDateTime dateAndTime, String location, double repairCost) {
        DamageReport damageReport = DamageReportFactory.createReport(
                dto.getId(),
                RentalMapper.toEntity(dto.getRental()),
                dto.getDescription(),
                dto.getDateAndTime(),
                dto.getLocation(),
                dto.getRepairCost()
        );
        return damageReport;
    }
}
