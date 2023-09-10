package za.ac.cput.controllers;
/**DamageReportController.java
 * Controller Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 * */
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
@RequestMapping("/damageReport")
public class DamageReportController {
@Autowired
private DamageReportServiceImpl damageReportService;

@PostMapping("createReport")
    public ResponseEntity<DamageReport> create(@RequestBody DamageReport damageReport){

    DamageReport newDamageReport = DamageReportFactory.createReport(damageReport.getId(), damageReport.getRental(), damageReport.getDescription(), damageReport.getDateAndTime(), damageReport.getLocation(), damageReport.getRepairCost());
    DamageReport damageReportSaved = this.damageReportService.create(newDamageReport);
    return ResponseEntity.ok(damageReportSaved);
    }

    @GetMapping("readReport/{id}")
    public ResponseEntity<DamageReport> read(@PathVariable("id") int id){
        DamageReport readDamageReport = this.damageReportService.read(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        return ResponseEntity.ok(readDamageReport);
    }

    @PutMapping("updateReport")
    public ResponseEntity<DamageReport> update(@RequestBody DamageReport damageReport){
        DamageReport updateReport = damageReportService.update(damageReport);
        return new ResponseEntity<>(updateReport,HttpStatus.OK);
    }
    @DeleteMapping("deleteReport/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        this.damageReportService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("getAllReports")
    public ResponseEntity<List<DamageReport>> getAll(){
        List<DamageReport> damageReportList = this.damageReportService.getAll();
        return ResponseEntity.ok(damageReportList);
    }


}
