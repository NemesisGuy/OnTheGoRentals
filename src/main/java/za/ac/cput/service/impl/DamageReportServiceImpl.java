package za.ac.cput.service.impl;
/**
 * DamageReportServiceImpl.java
 * Service Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.DamageReport;
import za.ac.cput.domain.dto.DamageReportDTO;
import za.ac.cput.repository.IDamageReportRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.service.IDamageReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DamageReportServiceImpl implements IDamageReport {
    @Autowired
    private IDamageReportRepository repository;
    //rental repository
    @Autowired
    private RentalRepository rentalRepository;
    //rental service
    @Autowired
    private RentalServiceImpl rentalService;

    @Autowired
    private DamageReportServiceImpl(IDamageReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public DamageReport create(DamageReport damageReport) {
        return this.repository.save(damageReport);
    }
/*
* @Builder
public class DamageReportDTO {
    private int id;
    private RentalDTO rental; // safe wrapped DTO
    private String description;
    private LocalDateTime dateAndTime;
    private String location;
    private double repairCost;
}

*
* */
    @Override
    public Optional<DamageReport> read(int id) {
        return this.repository.findById(id);
    }
    public Optional<DamageReportDTO> readDTO(int id) {
        Optional<DamageReport> damageReport = this.repository.findById(id);
        if (damageReport.isPresent()) {
            DamageReportDTO damageReportDTO = DamageReportDTO.builder()
                    .id(damageReport.get().getId())
                    .rental(this.rentalService.readDTO(damageReport.get().getRental().getId()))
                    .description(damageReport.get().getDescription())
                    .dateAndTime(damageReport.get().getDateAndTime())
                    .location(damageReport.get().getLocation())
                    .repairCost(damageReport.get().getRepairCost())
                    .build();
            return Optional.of(damageReportDTO);
        }
        return Optional.empty();
    }


    @Override
    public DamageReport read(Integer integer) {
        //optional
        Optional<DamageReport> optionalDamageReport = this.repository.findById(integer);
        return optionalDamageReport.orElse(null);

    }

    @Override
    public DamageReport update(DamageReport damageReport) {
        if (this.repository.existsById(damageReport.getId()))
            this.repository.save(damageReport);
        return null;
    }

    @Override
    public Boolean deleteById(int id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    @Override
    public List<DamageReport> getAll() {
        return this.repository.findAll();
    }
    public List<DamageReportDTO> getAllDTO() {
        List<DamageReport> damageReports = this.repository.findAll();
        List<DamageReportDTO> damageReportDTOs = new ArrayList<>();
        for (DamageReport damageReport : damageReports) {
            DamageReportDTO damageReportDTO = DamageReportDTO.builder()
                    .id(damageReport.getId())
                    .rental(this.rentalService.readDTO(damageReport.getRental().getId()))
                    .description(damageReport.getDescription())
                    .dateAndTime(damageReport.getDateAndTime())
                    .location(damageReport.getLocation())
                    .repairCost(damageReport.getRepairCost())
                    .build();
            damageReportDTOs.add(damageReportDTO);
        }
        return damageReportDTOs;
    }


}
