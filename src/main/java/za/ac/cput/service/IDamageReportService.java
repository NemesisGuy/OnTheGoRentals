package za.ac.cput.service;
/**
 * IDamageReport.java
 * Interface for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 */

import za.ac.cput.domain.entity.DamageReport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDamageReportService extends IService<DamageReport, Integer> {

    Optional<DamageReport> read(int id);
    DamageReport read(UUID uuid);

    Boolean deleteById(int id);

    List<DamageReport> getAll();


}
