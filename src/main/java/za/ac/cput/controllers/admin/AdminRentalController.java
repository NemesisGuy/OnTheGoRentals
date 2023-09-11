package za.ac.cput.controllers.admin;

/**
 * AdminRentalController.java
 * This is the controller for the Rental entity
 * Author: [Author Name]
 * Date: [Date]
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Rental;
import za.ac.cput.service.impl.RentalServiceImpl;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/rentals")
public class AdminRentalController {
    @Autowired
    private RentalServiceImpl rentalService;

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
        System.out.println(rental.getUser());
        System.out.println(rental.getCar());
        System.out.println(rental.getIssuedDate());
        System.out.println(rental.getReturnedDate());
        return rentalService.create(rental);
    }

    @GetMapping("/read/{rentalId}")
    public Rental readRental(@PathVariable Integer rentalId) {
        System.out.println("/api/admin/rentals/read was triggered");
        System.out.println("RentalService was created...attempting to read rental...");
        Rental readRental = rentalService.read(rentalId);
        return readRental;
    }

    @PutMapping("/update/{rentalId}")
    public Rental updateRental(@PathVariable int rentalId, @RequestBody Rental rental) {

        System.out.println("/api/admin/rentals/update was triggered");
        System.out.println(rental.toString());
        System.out.println("issued by: " + rental.getIssuer());
        System.out.println("rental Id: " + rental.getRentalId());
        System.out.println("rental user: " + rental.getUser());
        System.out.println("rental car: " + rental.getCar());
        System.out.println("rental issued date: " + rental.getIssuedDate());
        System.out.println("rental returned date: " + rental.getReturnedDate());
        Rental updated = rentalService.update(rental);
        System.out.println("updated rental: " + updated);
        return updated;
    }
    @DeleteMapping("/delete/{rentalId}")
    public boolean deleteRental(@PathVariable Integer rentalId) {
        System.out.println("/api/admin/rentals/delete was triggered");
        System.out.println("RentalService was created...attempting to delete rental...");
        return rentalService.delete(rentalId);
    }
}
