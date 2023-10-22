package za.ac.cput.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Driver;
import za.ac.cput.service.impl.DriverServiceImpl;
import za.ac.cput.factory.impl.DriverFactory;

import java.util.List;

@RestController
@RequestMapping("/api/admin/drivers")
public class AdminDriverController {
    @Autowired
    private DriverServiceImpl driverService;

    @PostMapping("/create")
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver){
        Driver newDriver = DriverFactory.createDriver(driver.getId(), driver.getFirstName(), driver.getLastName(), driver.getLicenseCode());
        Driver driverSaved = this.driverService.create(newDriver);
        return ResponseEntity.ok(driverSaved);
    }
    @GetMapping("/read/{id}")
    public ResponseEntity<Driver> read(@PathVariable("id") int id){
        Driver readDriver = this.driverService.read(id);
        //new ResponseStatusException(HttpStatus.NOT_FOUND,"Driver not found");
        return ResponseEntity.ok(readDriver);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Driver> updateDriver(@RequestBody Driver updatedDriver){
        Driver updateDriver = driverService.update(updatedDriver);
        return new ResponseEntity<>(updateDriver,HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Driver>> getAll(){
        List<Driver> driverList = this.driverService.getAll();
        return ResponseEntity.ok(driverList);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        this.driverService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}