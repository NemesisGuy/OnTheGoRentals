package za.ac.cput.service;

/**
 * ReservationsFactoryTest.java
 * Interface for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date:  10 June 2023
 */

import za.ac.cput.domain.impl.DamageReport;

import java.util.List;

public interface IDamageReportService {
    DamageReport create(DamageReport damageReport);
    boolean delete(Integer integer);

    List<DamageReport> getAll();
}
