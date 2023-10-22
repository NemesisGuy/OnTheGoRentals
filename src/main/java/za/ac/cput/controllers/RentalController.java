package za.ac.cput.controllers;

/**
 * AdminRentalController.java
 * This is the controller for the Rental entity
 * Author: Peter Buckingham (220165289)
 * Date: 10 April 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Rental;
import za.ac.cput.service.impl.RentalServiceImpl;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/user/rentals")
public class RentalController {
    @Autowired
    private RentalServiceImpl rentalService;

 /*   @GetMapping("/list/all")
    public ArrayList<Rental> getAll() {
        ArrayList<Rental> rentals = new ArrayList<>(rentalService.getAll());
        return rentals;
    }*/

    @PostMapping("/create")
    public Rental createRental(@RequestBody Rental rental) {
 /*       System.out.println("/api/admin/rentals/create was triggered");
        System.out.println("RentalService was created...attempting to create rental...");*/

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


}
