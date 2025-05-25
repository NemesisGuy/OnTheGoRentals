package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.domain.dto.request.RentalRequestDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.security.User;
import za.ac.cput.domain.dto.request.BookingRequestDTO; // Using this for creation by admin
import za.ac.cput.domain.dto.request.BookingUpdateDTO;   // Using this for update by admin
import za.ac.cput.domain.dto.response.RentalResponseDTO; // Consistent response DTO
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IDriverService;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/rentals")
// @CrossOrigin(...)
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminRentalController {

    private final IRentalService rentalService;
    private final IUserService userService;
    private final ICarService carService;
    private final IDriverService driverService;

    @Autowired
    public AdminRentalController(IRentalService rentalService, IUserService userService,
                                 ICarService carService, IDriverService driverService) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.carService = carService;
        this.driverService = driverService;
    }

    @GetMapping
    public ResponseEntity<List<RentalResponseDTO>> getAllRentalsForAdmin() {
        List<Rental> rentals = rentalService.getAll();
        if (rentals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(rentals));
    }

    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRentalByAdmin(@Valid @RequestBody RentalRequestDTO createDto) {
        // Admin provides all necessary UUIDs in BookingRequestDTO
        User userEntity = userService.read(createDto.getUserUuid());
        Car carEntity = carService.read(createDto.getCarUuid());
        Driver driverEntity = null;
        if (createDto.getDriverUuid() != null) {
            driverEntity = driverService.read(createDto.getDriverUuid());
        }

        Rental rentalToCreate = RentalMapper.toEntity(createDto, userEntity, carEntity, driverEntity);
        // Admin-specific service method might do more, or just use a generic create
        Rental createdEntity = rentalService.create(rentalToCreate);
        return new ResponseEntity<>(RentalMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    @GetMapping("/{rentalUuid}")
    public ResponseEntity<RentalResponseDTO> getRentalByUuidAdmin(@PathVariable UUID rentalUuid) {
        Rental rentalEntity = rentalService.read(rentalUuid);
        return ResponseEntity.ok(RentalMapper.toDto(rentalEntity));
    }

//    @PutMapping("/{rentalUuid}")
//    public ResponseEntity<RentalResponseDTO> updateRentalByAdmin(
//            @PathVariable UUID rentalUuid,
//            @Valid @RequestBody BookingUpdateDTO updateDto // Using BookingUpdateDTO
//    ) {
//        Rental existingRental = rentalService.read(rentalUuid);
//
//        // Fetch related entities ONLY if their UUIDs are provided in updateDto AND are different
//        User userEntity = (updateDto.getUserUuid() != null && !updateDto.getUserUuid().equals(existingRental.getUser().getUuid())) ?
//                userService.read(updateDto.getUserUuid()) : existingRental.getUser();
//        Car carFromUpdate = carService.read(updateDto.getCarUuid());
//        carService.update()
//
//        Driver driverEntity = existingRental.getDriver(); // Default to existing
//        if (updateDto.getDriverUuid() != null) { // If DTO provides a driver UUID
//            if (existingRental.getDriver() == null || !updateDto.getDriverUuid().equals(existingRental.getDriver().getUuid())) {
//                driverEntity = driverService.read(updateDto.getDriverUuid());
//            }
//        } else if (existingRental.getDriver() != null && updateDto.getDriverUuid() == null) {
//            // If DTO explicitly means to remove driver by sending null for driverUuid
//            // This assumes your BookingUpdateDTO has driverUuid and sending null means remove.
//            // Alternatively, the mapper's applyUpdateDtoToEntity can handle null in DTO field as "no change".
//            // For now, this logic tries to update or remove driver if DTO implies it.
//            driverEntity = null; // Explicitly removing driver
//        }
//
//        Rental rentalWithUpdates = RentalMapper.applyUpdateDtoToEntity(updateDto, existingRental, userEntity, carEntity, driverEntity);
//        Rental persistedRental = rentalService.update(rentalWithUpdates);
//
//        return ResponseEntity.ok(RentalMapper.toDto(persistedRental));
//    }

    @DeleteMapping("/{rentalUuid}")
    public ResponseEntity<Void> deleteRentalByAdmin(@PathVariable UUID rentalUuid) {
        Rental existingRental = rentalService.read(rentalUuid);
        boolean deleted = rentalService.delete(existingRental.getId());
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // Specific admin actions on a rental
    @PostMapping("/{rentalUuid}/confirm")
    public ResponseEntity<RentalResponseDTO> confirmRentalByAdmin(@PathVariable UUID rentalUuid) {
        // The service method 'confirmRentalByUuid' might be generic enough,
        // or you might have an admin-specific version if logic differs.
        // For now, assuming it can be used by admin context too.
        Rental confirmedRental = rentalService.confirmRentalByUuid(rentalUuid); // This service method might need adjustment to not require 'currentUser' if called by admin
        return ResponseEntity.ok(RentalMapper.toDto(confirmedRental));
    }

    @PostMapping("/{rentalUuid}/cancel")
    public ResponseEntity<RentalResponseDTO> cancelRentalByAdmin(@PathVariable UUID rentalUuid) {
        Rental cancelledRental = rentalService.cancelRentalByUuid(rentalUuid); // Similar to confirm
        return ResponseEntity.ok(RentalMapper.toDto(cancelledRental));
    }

    @PostMapping("/{rentalUuid}/complete")
    public ResponseEntity<RentalResponseDTO> completeRentalByAdmin(
            @PathVariable UUID rentalUuid,
            @RequestParam(required = false, defaultValue = "0.0") double fineAmount
    ) {
        Rental completedRental = rentalService.completeRentalByUuid(rentalUuid, fineAmount);
        return ResponseEntity.ok(RentalMapper.toDto(completedRental));
    }
}