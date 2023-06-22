package za.ac.cput.controllers.admin;

/**
 * AdminRentalController.java
 * This is the controller for the Rental entity
 * Author: [Author Name]
 * Date: [Date]
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.Rental;
import za.ac.cput.domain.impl.User;
import za.ac.cput.service.impl.ICarServiceImpl;
import za.ac.cput.service.impl.IRentalServiceImpl;
import za.ac.cput.service.impl.IUserServiceImpl;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/rentals")
public class AdminRentalController {
    @Autowired
    private IRentalServiceImpl rentalService;

    @Autowired
    private IUserServiceImpl userService;

    @Autowired
    private ICarServiceImpl carService;

    @GetMapping("/list/all")
    public ArrayList<Rental> getAll() {
        ArrayList<Rental> rentals = new ArrayList<>(rentalService.getAll());
        return rentals;
    }

    @PostMapping("/create")
    public Rental createRental(@RequestBody Rental rental) {
        System.out.println("/api/admin/rentals/create was triggered");
        System.out.println("RentalService was created...attempting to create rental...");

        // Retrieve user and car based on their IDs
        User user = userService.read(rental.getUser().getId());
        Car car = carService.read(rental.getCar().getId());

        rental.setUser(user);
        rental.setCar(car);

        Rental createdRental = rentalService.create(rental);
        return createdRental;
    }

    @GetMapping("/read/{rentalId}")
    public Rental readRental(@PathVariable Integer rentalId) {
        System.out.println("/api/admin/rentals/read was triggered");
        System.out.println("RentalService was created...attempting to read rental...");
        Rental readRental = rentalService.read(rentalId);
        return readRental;
    }

    @PutMapping("/update/{rentalId}")
    public Rental updateRental(@PathVariable int rentalId, @RequestBody Rental updatedRental) {
        Rental updated = rentalService.update(updatedRental);
        return updated;
    }

    @DeleteMapping("/delete/{rentalId}")
    public boolean deleteRental(@PathVariable Integer rentalId) {
        System.out.println("/api/admin/rentals/delete was triggered");
        System.out.println("RentalService was created...attempting to delete rental...");
        return rentalService.delete(rentalId);
    }
}
