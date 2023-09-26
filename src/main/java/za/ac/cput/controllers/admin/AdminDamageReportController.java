package za.ac.cput.controllers.admin;
/**AdminDamageReportController.java
 * Controller Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.DamageReport;
import za.ac.cput.factory.impl.DamageReportFactory;
import za.ac.cput.service.impl.DamageReportServiceImpl;

import java.util.List;


@RestController
@RequestMapping("/api/admin/damageReport")// no api? look at my examples please!
public class AdminDamageReportController {

    @Autowired
    private DamageReportServiceImpl damageReportService;

    @PostMapping("/create")
//lets use slashes in the url look at my examples please, too much use of the word report, think about it please @Cwenga
    //http://localhost:8080/admin/damageReport/createReport this is strange @Cwenga
    //http://localhost:8080/damageReport/getAllReports  this is strange @Cwenga

    public ResponseEntity<DamageReport> create(@RequestBody DamageReport damageReport){

        DamageReport newDamageReport = DamageReportFactory.createReport(damageReport.getId(), damageReport.getRental(), damageReport.getDescription(), damageReport.getDateAndTime(), damageReport.getLocation(), damageReport.getRepairCost());
        DamageReport damageReportSaved = this.damageReportService.create(newDamageReport);
        return ResponseEntity.ok(damageReportSaved);
    }
    @GetMapping("/read/{id}")
    public ResponseEntity<DamageReport> read(@PathVariable("id") int id){
        DamageReport readDamageReport = this.damageReportService.read(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        return ResponseEntity.ok(readDamageReport);
    }

    @PutMapping("/update")
    public ResponseEntity<DamageReport> update(@RequestBody DamageReport updatedReport){
        DamageReport updateReport = damageReportService.update(updatedReport);
        return new ResponseEntity<>(updateReport,HttpStatus.OK);
    }

    @GetMapping("/All")
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
