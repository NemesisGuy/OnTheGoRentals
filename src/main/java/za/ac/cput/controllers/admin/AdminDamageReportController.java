package za.ac.cput.controllers.admin;

/**
 * AdminDamageReportController.java
 * Controller Class for the Damage Report
 * Author: Cwenga Dlova (214310671)
 * Date: 08/09/2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.DamageReport;

import za.ac.cput.domain.dto.response.DamageReportResponseDTO;
import za.ac.cput.domain.mapper.DamageReportMapper;
import za.ac.cput.service.impl.DamageReportServiceImpl;
import za.ac.cput.service.impl.RentalServiceImpl;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/damageReport")
@Transactional
public class AdminDamageReportController {

    @Autowired
    private DamageReportServiceImpl damageReportService;

    @Autowired
    private RentalServiceImpl rentalService;

    @PostMapping("/create")
    public ResponseEntity<DamageReport> create(@RequestBody DamageReport damageReport) {

        DamageReport saved = damageReportService.create(damageReport);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<DamageReportResponseDTO> read(@PathVariable int id) {
        DamageReport report = damageReportService.read(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        return ResponseEntity.ok(DamageReportMapper.toDto(report));
    }

    @PutMapping("/update/{damageReportId}")
    public ResponseEntity<DamageReportResponseDTO> update(@PathVariable int damageReportId, @RequestBody DamageReport updatedReport) {
        DamageReport updated = damageReportService.update(updatedReport);
        return ResponseEntity.ok(DamageReportMapper.toDto(updated));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DamageReportResponseDTO>> getAll() {
        List<DamageReportResponseDTO> dtoList = new ArrayList<>();
        List<DamageReport> reportList = damageReportService.getAll();
        if (reportList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No reports found");
        }else {
            for (DamageReport report : reportList) {
                dtoList.add(DamageReportMapper.toDto(report));
            }
        }
        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        damageReportService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
