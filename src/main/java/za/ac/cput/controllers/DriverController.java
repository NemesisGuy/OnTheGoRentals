package za.ac.cput.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Driver;
import za.ac.cput.service.IDriverService;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private IDriverService driverService;
    @PostMapping("/create")
    public Driver create (@RequestBody Driver driver){
        return driverService.create(driver);
    }
    @GetMapping("/read/{id}")
    public Driver read(@PathVariable Integer id){
        return driverService.read(id);
    }
    @PostMapping("/update/")
    public Driver updated(@RequestBody Driver driver){
        return driverService.update(driver);
    }
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return driverService.delete(id);
    }
    @RequestMapping({"/getall"})
    public List<Driver> getall(){
        return driverService.getAll();
    }
}
