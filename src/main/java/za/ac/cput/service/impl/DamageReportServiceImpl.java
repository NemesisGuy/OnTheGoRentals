package za.ac.cput.service.impl;

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

    private static IDamageReportService getService() {
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
    public boolean delete(Integer id) {
        return false;
    }
    @Override
    public List<DamageReport> getAll() {
        return null;
    }

}
