package za.ac.cput.controllers.admin;
/**AdminDamageReportController.java
 * Controller Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023*/
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.DamageReport;
import za.ac.cput.domain.Rental;
import za.ac.cput.factory.impl.DamageReportFactory;
import za.ac.cput.service.impl.DamageReportServiceImpl;
import za.ac.cput.service.impl.RentalServiceImpl;

import java.util.List;


@RestController
@RequestMapping("/api/admin/damageReport")
@Transactional
public class AdminDamageReportController {

    @Autowired
    private DamageReportServiceImpl damageReportService;
    @Autowired
    private RentalServiceImpl rentalService;

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/create")
    public ResponseEntity<DamageReport> create(@RequestBody DamageReport damageReport){

         /**DamageReport newDamageReport = DamageReportFactory.createReport(damageReport.getId(), damageReport.getRental(), damageReport.getDescription(), damageReport.getDateAndTime(), damageReport.getLocation(), damageReport.getRepairCost());
         DamageReport damageReportSaved = this.damageReportService.create(newDamageReport);
         return ResponseEntity.ok(damageReportSaved);*/
        // Check if the rental ID provided in the damage report already exists
        if (rentalService.existsById(damageReport.getRental().getId())) {
            // I am getting a detached entity error. Merge the detached Rental entity
            Rental attachedRental = entityManager.merge(damageReport.getRental());

            // Now use the attached Rental in the new DamageReport
            DamageReport newDamageReport = DamageReportFactory.createReport(
                    damageReport.getId(),
                    attachedRental,
                    damageReport.getDescription(),
                    damageReport.getDateAndTime(),
                    damageReport.getLocation(),
                    damageReport.getRepairCost()
            );

            DamageReport damageReportSaved = this.damageReportService.create(newDamageReport);
            return ResponseEntity.ok(damageReportSaved);
        } else {
            // Handle the case where the provided rental ID doesn't exist
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            //also need to handle an
        }
    }
    @GetMapping("/read/{id}")
    public ResponseEntity<DamageReport> read(@PathVariable("id") int id){
        DamageReport readDamageReport = this.damageReportService.read(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        return ResponseEntity.ok(readDamageReport);
    }

    @PutMapping("/update/{damageReportId}")
    public ResponseEntity<DamageReport> update(@PathVariable int damageReportId,@RequestBody DamageReport updatedReport){
        DamageReport updateReport = damageReportService.update(updatedReport);
        return new ResponseEntity<>(updateReport,HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DamageReport>> getAll(){
        List<DamageReport> damageReportList = this.damageReportService.getAll();
        return ResponseEntity.ok(damageReportList);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        this.damageReportService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

