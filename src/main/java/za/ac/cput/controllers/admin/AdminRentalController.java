package za.ac.cput.controllers.admin;

/**
 * AdminRentalController.java
 * This is the controller for the Rental entity
 * Author: Peter Buckingham (220165289)
 * Date: 10 April 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Rental;

import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.service.impl.RentalServiceImpl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/rentals")
public class AdminRentalController {

    @Autowired
    private RentalServiceImpl rentalService;

    @GetMapping("/list/all")
    public ResponseEntity<List<RentalResponseDTO>> getAll() {
        List<Rental> rentals = rentalService.getAll();
        List<RentalResponseDTO> rentalDTOs = rentals.stream()
                .map(RentalMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rentalDTOs);
    }

    @PostMapping("/create")
    public ResponseEntity<RentalResponseDTO> createRental(@RequestBody Rental rental) {
        Rental created = rentalService.create(rental);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(RentalMapper.toDto(created));
    }

    @GetMapping("/read/{rentalId}")
    public ResponseEntity<RentalResponseDTO> readRental(@PathVariable UUID rentalId) {
        if (rentalId == null) {
            return ResponseEntity.badRequest().build();
        }
        Rental rental = rentalService.findByUuid(rentalId);
        Rental readRental = rentalService.read(rental.getId());
        if (readRental == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(RentalMapper.toDto(readRental));
    }

    @PutMapping("/update/{rentalId}")
    public ResponseEntity<RentalResponseDTO> updateRental(@PathVariable Integer rentalId, @RequestBody Rental rental) {
        Rental existing = rentalService.read(rentalId);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        Rental updatedRental = new Rental.Builder()
                .setId(existing.getId())
                .setUser(existing.getUser())
                .setCar(existing.getCar())
                .setIssuer(rental.getIssuer())
                .setReceiver(rental.getReceiver())
                .setFine(rental.getFine())
                .setIssuedDate(rental.getIssuedDate())
                .setReturnedDate(rental.getReturnedDate())
                .build();

        Rental result = rentalService.update(rentalId, updatedRental);
        return ResponseEntity.ok(RentalMapper.toDto(result));
    }

    @DeleteMapping("/delete/{rentalId}")
    public ResponseEntity<Void> deleteRental(@PathVariable Integer rentalId) {
        rentalService.delete(rentalId);
        return ResponseEntity.ok().build();
    }
}
