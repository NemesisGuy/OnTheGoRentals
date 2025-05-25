package za.ac.cput.service.impl;
/**
 * DamageReportServiceImpl.java
 * Service Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.entity.DamageReport;
import za.ac.cput.repository.IDamageReportRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.service.IDamageReport;

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
        return this.repository.findByIdAndDeletedFalse(id);
    }



    @Override
    public DamageReport read(Integer integer) {
        //optional
        Optional<DamageReport> optionalDamageReport = this.repository.findByIdAndDeletedFalse(integer);
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
        DamageReport damageReport = this.repository.findById(id).orElse(null);
        if (damageReport != null && !damageReport.isDeleted()) {
            damageReport = new DamageReport.Builder().copy(damageReport).setDeleted(true).build();
            this.repository.save(damageReport);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Integer integer) {
        DamageReport damageReport = this.repository.findById(integer).orElse(null);
        if (damageReport != null && !damageReport.isDeleted()) {
            damageReport = new DamageReport.Builder().copy(damageReport).setDeleted(true).build();
            this.repository.save(damageReport);
            return true;
        }
        return false;
    }

    @Override
    public List<DamageReport> getAll() {
        return this.repository.findByDeletedFalse();
    }



}
