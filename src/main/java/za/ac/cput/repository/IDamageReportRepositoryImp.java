package za.ac.cput.repository;
/**
 * IDamageReportImp.java
 * Class for the Damage report repository
 * Author: Cwenga Dlova (214310671)
 * Date:  07 April 2023
 */
import za.ac.cput.domain.DamageReport;

import java.util.ArrayList;
import java.util.List;

public class IDamageReportRepositoryImp implements IDamageReportRepository{

    private List<DamageReport> reportDB;

    private static IDamageReportRepositoryImp repository = null;

    private IDamageReportRepositoryImp(){
        reportDB = new ArrayList<>();
    }

    public static IDamageReportRepositoryImp getRepository() {
        if (repository == null) {
            repository = new IDamageReportRepositoryImp();
        }
        return repository;
    }
    @Override
    public DamageReport create(DamageReport damageReport) {
        reportDB.add(damageReport);
        return damageReport;
    }

    @Override
    public DamageReport read(Integer id) {
        DamageReport damageReport = reportDB.stream().filter(r -> r.getId() == id).findAny().orElse(null) ;
        return damageReport;

    }

    @Override
    public DamageReport update(DamageReport report1) {
        DamageReport report = read(report1.getId());
        if (report != null) {
            reportDB.remove(report1);
            reportDB.add(report);
            return report;
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        DamageReport deleteReport = read(id);
        if (deleteReport != null)
            return false;
        reportDB.remove(deleteReport);
        return true;
    }
    @Override
    public DamageReport getDamageReportById(Integer id) {
        return null;
    }

}
