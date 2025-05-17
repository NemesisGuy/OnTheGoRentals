package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Driver;
import za.ac.cput.service.IDriverService;

import java.util.List;

@RestController
//@RequestMapping("/api/driver")
@RequestMapping("/driver")
// @CrossOrigin(origins = "http://localhost:5173") // Optional
public class DriverController {

    @Autowired
    private IDriverService driverService;

    @PostMapping("/create")
    public ResponseEntity<Driver> create(@RequestBody Driver driver) {
        Driver created = driverService.create(driver);
        return ResponseEntity.ok(created); // Optionally return `created` status
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<Driver> read(@PathVariable Integer id) {
        Driver driver = driverService.read(id);
        if (driver == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(driver);
    }

    @PutMapping("/update")
    public ResponseEntity<Driver> update(@RequestBody Driver driver) {
        Driver updated = driverService.update(driver);
        if (updated == null) {
            return ResponseEntity.notFound().build(); // Optional, based on update logic
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = driverService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Driver>> getAll() {
        List<Driver> drivers = driverService.getAll();
        return ResponseEntity.ok(drivers);
    }
}
