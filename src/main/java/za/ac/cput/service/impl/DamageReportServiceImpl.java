package za.ac.cput.service.impl;

/**
 * DamageReportImplt.java
 * Class for the Damage Report Implementation
 * Author: Cwenga Dlova (214310671)
 * Date:  11 June 2023
 * 12 June 2023: changes made by Cwenga
 */

import za.ac.cput.domain.impl.DamageReport;
import za.ac.cput.repository.impl.DamageReportRepositoryImpl;
import za.ac.cput.service.IDamageReportService;

import java.util.List;

public class DamageReportServiceImpl implements IDamageReportService {

    private static DamageReportServiceImpl service = null;
    private static DamageReportRepositoryImpl repository = null;

    private DamageReportServiceImpl() {

        repository = DamageReportRepositoryImpl.getRepository();
    }

    public static DamageReportServiceImpl getService() {
        if (service == null) {
            service = new DamageReportServiceImpl();
        }
        return service;
    }

    @Override
    public DamageReport create(DamageReport damageReport) {
        DamageReport created = repository.create(damageReport);
        return created;
    }

    @Override
    public boolean delete(int id) {

        return repository.delete(id);
    }
    @Override
    public List<DamageReport> getAll() {

        return getAll();
    }

}
