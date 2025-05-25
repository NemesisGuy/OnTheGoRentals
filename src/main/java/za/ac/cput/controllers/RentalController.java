package za.ac.cput.controllers; // Or a more specific package like za.ac.cput.controllers.user

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver; // Assuming driver can be part of a rental
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.dto.request.RentalRequestDTO;
import za.ac.cput.domain.dto.request.RentalUpdateDTO; // For PUT
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.domain.security.User;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IDriverService; // If driver is involved
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService;

import java.util.List;
import java.util.UUID;

/**
 * RentalController.java
 * Handles Rental operations, primarily for the authenticated user.
 * Author: Peter Buckingham (220165289) // Updated by: [Your Name]
 * Date: 10 April 2023 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/rentals") // Changed base path
// @CrossOrigin(...) // Prefer global CORS
public class RentalController {

    private final IRentalService rentalService;
    private final IUserService userService;
    private final ICarService carService;
    private final IDriverService driverService; // Optional, if rentals involve drivers

    @Autowired
    public RentalController(IRentalService rentalService, IUserService userService,
                            ICarService carService, IDriverService driverService) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.carService = carService;
        this.driverService = driverService;
    }

    /**
     * Creates a new rental for the currently authenticated user.
     */
    @PostMapping // POST /api/v1/rentals
    public ResponseEntity<RentalResponseDTO> createRental(@Valid @RequestBody RentalRequestDTO rentalRequestDTO) {
        System.out.println("Controller: POST /api/v1/rentals triggered with DTO: " + rentalRequestDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userService.read(userEmail);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Should be caught by security
        }

        Car carEntity = carService.read(rentalRequestDTO.getCarUuid());
        // Driver might be optional
        Driver driverEntity = null;
        if (rentalRequestDTO.getDriverUuid() != null) {
            driverEntity = driverService.read(rentalRequestDTO.getDriverUuid());
        }

        Rental rentalToCreate = RentalMapper.toEntity(rentalRequestDTO, currentUser, carEntity, driverEntity);
        Rental createdRentalEntity = rentalService.create(rentalToCreate); // Service method takes entity

        return ResponseEntity.status(HttpStatus.CREATED).body(RentalMapper.toDto(createdRentalEntity));
    }

    /**
     * Retrieves a specific rental by its UUID for the currently authenticated user.
     * (Or admin if authorization logic allows).
     */
    @GetMapping("/{rentalUuid}") // GET /api/v1/rentals/{uuid_value}
    public ResponseEntity<RentalResponseDTO> getRentalByUuid(@PathVariable UUID rentalUuid) {
        System.out.println("Controller: GET /api/v1/rentals/" + rentalUuid + " triggered");
        // Add authorization: ensure current user owns this rental or is admin
        Rental rentalEntity = rentalService.read(rentalUuid);
        // TODO: Add authorization check here if this isn't just for "my" rentals
        return ResponseEntity.ok(RentalMapper.toDto(rentalEntity));
    }

    /**
     * Retrieves all rentals for the currently authenticated user.
     */
    @GetMapping("/my-rentals") // GET /api/v1/rentals/my-rentals
    public ResponseEntity<List<RentalResponseDTO>> getCurrentUserRentals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userService.read(userEmail);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Rental> userRentals = rentalService.getRentalHistoryByUser(currentUser); // Service takes User entity
        if (userRentals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(userRentals));
    }


    /**
     * Updates an existing rental (e.g., change dates, if allowed by business logic).
     * Typically, only certain fields of a rental are updatable.
     */
    @PutMapping("/{rentalUuid}") // PUT /api/v1/rentals/{uuid_value}
    public ResponseEntity<RentalResponseDTO> updateRental(
            @PathVariable UUID rentalUuid,
            @Valid @RequestBody RentalUpdateDTO rentalUpdateDTO
    ) {
        System.out.println("Controller: PUT /api/v1/rentals/" + rentalUuid + " triggered with DTO: " + rentalUpdateDTO);
        // Add authorization: ensure current user owns this rental or is admin
        Rental existingRental = rentalService.read(rentalUuid); // Fetch entity

        // If car or driver can be updated, fetch them based on UUIDs from rentalUpdateDTO
        Car carForUpdate = existingRental.getCar(); // Default to existing
        // if (rentalUpdateDTO.getCarUuid() != null && !rentalUpdateDTO.getCarUuid().equals(existingRental.getCar().getUuid())) {
        //     carForUpdate = carService.findByUuidAndNonDeleted(rentalUpdateDTO.getCarUuid());
        // }
        Driver driverForUpdate = existingRental.getDriver();
        // if (rentalUpdateDTO.getDriverUuid() != null && (existingRental.getDriver() == null || !rentalUpdateDTO.getDriverUuid().equals(existingRental.getDriver().getUuid()))) {
        //     driverForUpdate = driverService.readByUuid(rentalUpdateDTO.getDriverUuid());
        // }

        Rental rentalWithUpdates = RentalMapper.applyUpdateDtoToEntity(rentalUpdateDTO, existingRental, carForUpdate, driverForUpdate);
        Rental persistedRental = rentalService.update(rentalWithUpdates); // Service takes entity

        return ResponseEntity.ok(RentalMapper.toDto(persistedRental));
    }

    // Specific actions on a rental are often better as POST to sub-resources
    @PostMapping("/{rentalUuid}/confirm")
    public ResponseEntity<RentalResponseDTO> confirmRental(@PathVariable UUID rentalUuid) {
        Rental confirmedRental = rentalService.confirmRentalByUuid(rentalUuid);
        return ResponseEntity.ok(RentalMapper.toDto(confirmedRental));
    }

    @PostMapping("/{rentalUuid}/cancel")
    public ResponseEntity<RentalResponseDTO> cancelRental(@PathVariable UUID rentalUuid) {
        Rental cancelledRental = rentalService.cancelRentalByUuid(rentalUuid);
        return ResponseEntity.ok(RentalMapper.toDto(cancelledRental));
    }

    // Example: Endpoint for an admin/staff to mark a rental as completed/returned
    @PostMapping("/{rentalUuid}/complete")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<RentalResponseDTO> completeRental(
            @PathVariable UUID rentalUuid,
            @RequestParam(required = false, defaultValue = "0.0") double fineAmount
    ) {
        Rental completedRental = rentalService.completeRentalByUuid(rentalUuid, fineAmount);
        return ResponseEntity.ok(RentalMapper.toDto(completedRental));
    }


    // Your original read method using Integer ID - keep if needed for admin or internal,
    // but public API should favor UUID.
    /*
    @GetMapping("/read/{rentalId}")
    public ResponseEntity<RentalResponseDTO> readRentalById(@PathVariable Integer rentalId) {
        System.out.println("GET /api/user/rentals/read/" + rentalId + " triggered");
        Rental rental = rentalService.read(rentalId); // Assuming service has read(Integer)
        if (rental == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(RentalMapper.toDto(rental));
    }
    */
}