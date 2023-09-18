package za.ac.cput.service;
/**IDamageReport.java
 * Interface for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 * */
import za.ac.cput.domain.DamageReport;

import java.util.List;
import java.util.Optional;

public interface IDamageReport extends IService <DamageReport, Integer>{

    Optional<DamageReport> read(int id);

    Boolean deleteById(int id);
    List<DamageReport> getAll();




}
