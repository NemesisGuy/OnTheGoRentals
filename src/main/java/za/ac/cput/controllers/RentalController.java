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
        // Retrieve user and car based on their IDs
    /*    System.out.println(rental.getUser());
        System.out.println(rental.getCar());
        System.out.println(rental.getIssuedDate());
        System.out.println(rental.getReturnedDate());*/

        //user + car + times == rental
            //User
                //1 receive the token - header
                //2 find the user by their email from the token (extract the email from the token)//jwtutils
                //3 find user via repository and return user object

            //Car
                //4 find the car by its id - car service -> car repo = car object

            //Rental
                //5 create a rental object with the user, car and times

       /* return rentalService.create(rental);*/
         return null;

    }

    @GetMapping("/read/{rentalId}")
    public Rental readRental(@PathVariable Integer rentalId) {
        System.out.println("/api/admin/rentals/read was triggered");
        System.out.println("RentalService was created...attempting to read rental...");
        Rental readRental = rentalService.read(rentalId);
        return readRental;
    }


}
