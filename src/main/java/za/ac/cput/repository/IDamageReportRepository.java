package za.ac.cput.repository;
/**
 * IDamageReportReposity.java
 * interface for the Damage report repository
 * Author: Cwenga Dlova (214310671)
 * Date:  07 April 2023
 */

import za.ac.cput.domain.impl.DamageReport;

public interface IDamageReportRepository extends IRepository<DamageReport, Integer> {

    DamageReport getDamageReportById(Integer id);
}
