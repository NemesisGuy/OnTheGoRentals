package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Insurance;
import za.ac.cput.service.IInsuranceService;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {

    @Autowired
    private IInsuranceService service;

    @PostMapping("/create")
    public Insurance processInsurance(@RequestBody Insurance insurance) {
//        Insurance newInsurance = InsuranceFactory.createInsurance(insurance.getInsuranceType(), insurance.getInsuranceAmount(), insurance.getInsuranceCoverageStartDate(), insurance.getInsuranceCoverageEndDate(), null);
        return service.create(insurance);
    }

    @GetMapping("/read/{id}")
    public Insurance getInsurance(@PathVariable int id) {
        return service.read(id);
    }

    @GetMapping("/get-all")
    public List<Insurance> getAllInsurances() {
        return service.getAllInsurancePolicies();
    }

    @PutMapping("/update")
    public Insurance updateInsurance(@RequestBody Insurance insurance) {
        return service.update(insurance);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteInsurance(@PathVariable int id) {
        return service.delete(id);
    }
}
