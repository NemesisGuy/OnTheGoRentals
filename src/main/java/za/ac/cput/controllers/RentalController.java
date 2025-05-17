package za.ac.cput.controllers;

/**
 * RentalController.java
 * Handles Rental operations for users
 * Author: Peter Buckingham (220165289)
 * Date: 10 April 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Rental;
import za.ac.cput.domain.dto.RentalDTO;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.service.impl.RentalServiceImpl;

@RestController
@RequestMapping("/api/user/rentals")
public class RentalController {

    private final RentalServiceImpl rentalService;

    @Autowired
    public RentalController(RentalServiceImpl rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/create")
    public ResponseEntity<RentalDTO> createRental(@RequestBody Rental rental) {
        System.out.println("POST /api/user/rentals/create triggered");

        try {
            Rental newRental = rentalService.create(rental);
            return ResponseEntity.status(HttpStatus.CREATED).body(RentalMapper.toDto(newRental));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Customize as needed
        }
    }

    @GetMapping("/read/{rentalId}")
    public ResponseEntity<RentalDTO> readRental(@PathVariable Integer rentalId) {
        System.out.println("GET /api/user/rentals/read/" + rentalId + " triggered");

        Rental rental = rentalService.read(rentalId);

        if (rental == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(RentalMapper.toDto(rental));
    }
}
