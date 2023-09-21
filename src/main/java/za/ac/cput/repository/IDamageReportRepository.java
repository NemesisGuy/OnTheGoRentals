package za.ac.cput.repository;
/**
 * IDamageReportRepository.java
 * Repository Interface for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 * */
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.DamageReport;
@Repository
public interface IDamageReportRepository extends JpaRepository<DamageReport, Integer> {

}
