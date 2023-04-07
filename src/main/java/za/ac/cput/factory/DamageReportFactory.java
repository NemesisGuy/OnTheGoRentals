package za.ac.cput.factory;
/**
 * DamageReportFactory.java
 * Class for the Damage report factory
 * Author: Cwenga Dlova (214310671)
 * Date:  06 April 2023
 */
import za.ac.cput.domain.DamageReport;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class DamageReportFactory implements IFactory<DamageReport> {

    @Override
    public DamageReport create() {
        return new DamageReport.Builder()
                .setId(new Random().nextInt(1000000))
                .setReportDate(LocalDate.parse("2023-01-05"))
                .setDamageLocation("Cape Town")
                .setDescription("Drunk driving recklessness.")
                .build();
    }

    @Override
    public DamageReport getById(long id) {
        return null;
    }

    @Override
    public DamageReport update(DamageReport entity) {
        return null;
    }

    @Override
    public boolean delete(DamageReport entity) {
        return false;
    }

    @Override
    public List<DamageReport> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<DamageReport> getType() {
        return null;
    }
}
