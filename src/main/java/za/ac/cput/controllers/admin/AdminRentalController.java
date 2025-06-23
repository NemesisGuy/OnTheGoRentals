package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.RentalRequestDTO;
import za.ac.cput.domain.dto.request.RentalUpdateDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.mapper.RentalMapper;
import za.ac.cput.service.*;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * AdminRentalController.java
 * Controller for administrators to manage all aspects of Rental entities.
 *
 * @author Peter Buckingham
 * @version 2.1
 */
@RestController
@RequestMapping("/api/v1/admin/rentals")
@Tag(name = "Admin: Rental Management", description = "Endpoints for administrators to manage all aspects of rentals.")
public class AdminRentalController {

    private static final Logger log = LoggerFactory.getLogger(AdminRentalController.class);

    private final IRentalService rentalService;
    private final IUserService userService;
    private final ICarService carService;
    private final IDriverService driverService;
    private final IFileStorageService fileStorageService;
    private final String publicApiUrl;

    /**
     * Constructs an AdminRentalController with necessary service dependencies.
     *
     * @param rentalService      The service for rental logic.
     * @param userService        The service for user lookups.
     * @param carService         The service for car lookups.
     * @param driverService      The service for driver lookups.
     * @param fileStorageService The service for generating image URLs.
     */
    @Autowired
    public AdminRentalController(IRentalService rentalService, IUserService userService,
                                 ICarService carService, IDriverService driverService, IFileStorageService fileStorageService,
                                 @Value("${app.public-api-url}") String publicApiUrl) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.carService = carService;
        this.driverService = driverService;
        this.fileStorageService = fileStorageService;
        this.publicApiUrl = publicApiUrl;
        log.info("AdminRentalController initialized.");
    }

    /**
     * Creates a new rental. This is typically done by an administrator on behalf of a user.
     *
     * @param createDto The DTO containing data for the new rental.
     * @return A ResponseEntity containing the created RentalResponseDTO.
     */
    @Operation(summary = "Create a rental", description = "Allows an administrator to create a new rental, specifying user, car, and driver.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rental created successfully", content = @Content(schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User, Car, or Driver not found")
    })
    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRentalByAdmin(@Valid @RequestBody RentalRequestDTO createDto) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to create a new rental with DTO: {}", adminId, createDto);

        User userEntity = userService.read(createDto.getUserUuid());
        Car carEntity = carService.read(createDto.getCarUuid());
        Driver driverEntity = (createDto.getDriverUuid() != null) ? driverService.read(createDto.getDriverUuid()) : null;

        Rental rentalToCreate = RentalMapper.toEntity(createDto, userEntity, carEntity, driverEntity);
        Rental createdEntity = rentalService.create(rentalToCreate);
        return new ResponseEntity<>(RentalMapper.toDto(createdEntity, fileStorageService , publicApiUrl), HttpStatus.CREATED);
    }

    /**
     * Retrieves all rentals in the system for administrative view.
     *
     * @return ResponseEntity with a list of all RentalResponseDTOs.
     */
    @Operation(summary = "Get all rentals", description = "Retrieves a list of all rentals in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of rentals"),
            @ApiResponse(responseCode = "204", description = "No rentals found in the system")
    })
    @GetMapping()
    public ResponseEntity<List<RentalResponseDTO>> getAllRentalsForAdmin() {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to get all rentals.", adminId);
        List<Rental> rentals = rentalService.getAll();
        if (rentals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(rentals, fileStorageService, publicApiUrl));
    }

    /**
     * Retrieves all currently active rentals.
     *
     * @return ResponseEntity with a list of active RentalResponseDTOs.
     */
    @Operation(summary = "Get all active rentals", description = "Retrieves all rentals that are currently in an ACTIVE status.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successfully retrieved active rentals"))
    @GetMapping("/active")
    public ResponseEntity<List<RentalResponseDTO>> getActiveRentals() {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to get all active rentals.", adminId);
        List<Rental> activeRentals = rentalService.findActiveRentals();
        if (activeRentals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(activeRentals, fileStorageService, publicApiUrl));
    }

    /**
     * Retrieves rentals that are due for return today.
     *
     * @return ResponseEntity with a list of RentalResponseDTOs due for return.
     */
    @Operation(summary = "Get rentals due for return today", description = "Retrieves active rentals scheduled to be returned today.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successfully retrieved rentals due today"))
    @GetMapping("/returns-due-today")
    public ResponseEntity<List<RentalResponseDTO>> getRentalsDueForReturnToday() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Staff/Admin [{}]: Request for rentals due for return today.", requesterId);
        List<Rental> rentals = rentalService.findRentalsDueToday();
        if (rentals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(rentals, fileStorageService, publicApiUrl));
    }

    /**
     * Retrieves rentals that are currently overdue.
     *
     * @return ResponseEntity with a list of overdue RentalResponseDTOs.
     */
    @Operation(summary = "Get overdue rentals", description = "Retrieves rentals that have passed their expected return date and are not yet returned.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successfully retrieved overdue rentals"))
    @GetMapping("/overdue-rentals")
    public ResponseEntity<List<RentalResponseDTO>> getOverdueRentals() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Staff/Admin [{}]: Request for overdue rentals.", requesterId);
        List<Rental> rentals = rentalService.findOverdueRentals();
        if (rentals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RentalMapper.toDtoList(rentals, fileStorageService, publicApiUrl));
    }

    /**
     * Retrieves a specific rental by its UUID.
     *
     * @param rentalUuid The UUID of the rental to retrieve.
     * @return A ResponseEntity containing the RentalResponseDTO.
     */
    @Operation(summary = "Get rental by UUID", description = "Retrieves a specific rental by its unique identifier.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Rental found", content = @Content(schema = @Schema(implementation = RentalResponseDTO.class))))
    @GetMapping("/{rentalUuid}")
    public ResponseEntity<RentalResponseDTO> getRentalByUuidAdmin(@PathVariable UUID rentalUuid) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to get rental by UUID: {}", adminId, rentalUuid);
        Rental rentalEntity = rentalService.read(rentalUuid);
        return ResponseEntity.ok(RentalMapper.toDto(rentalEntity, fileStorageService, publicApiUrl));
    }

    /**
     * Allows an admin to update specific fields of an existing rental. The controller resolves all UUIDs
     * from the DTO into full domain entities before passing an updated Rental entity to the service layer.
     *
     * @param rentalUuid The UUID of the rental to update.
     * @param updateDto  The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated RentalResponseDTO.
     */
    @Operation(summary = "Update a rental", description = "Updates specific fields of an existing rental.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rental updated successfully", content = @Content(schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rental or a related entity for update not found")
    })
    @PutMapping("/{rentalUuid}")
    public ResponseEntity<RentalResponseDTO> updateRentalByAdmin(@PathVariable UUID rentalUuid, @Valid @RequestBody RentalUpdateDTO updateDto) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.info("Admin [{}]: Request to update rental UUID: {} with DTO: {}", adminId, rentalUuid, updateDto);

        Rental existingRental = rentalService.read(rentalUuid);
        User userEntity = (updateDto.getUserUuid() != null) ? userService.read(updateDto.getUserUuid()) : existingRental.getUser();
        Car carEntity = (updateDto.getCarUuid() != null) ? carService.read(updateDto.getCarUuid()) : existingRental.getCar();
        Driver driverEntity = (updateDto.getDriverUuid() != null) ? driverService.read(updateDto.getDriverUuid()) : existingRental.getDriver();

        Rental rentalWithUpdates = RentalMapper.applyUpdateDtoToEntity(updateDto, existingRental, userEntity, carEntity, driverEntity);
        Rental persistedRental = rentalService.update(rentalWithUpdates);

        return ResponseEntity.ok(RentalMapper.toDto(persistedRental, fileStorageService, publicApiUrl   ));
    }

    /**
     * Deletes a rental by its UUID.
     *
     * @param rentalUuid The UUID of the rental to delete.
     * @return A ResponseEntity with status 204 No Content.
     */
    @Operation(summary = "Delete a rental", description = "Soft-deletes a rental by its UUID.")
    @ApiResponses(@ApiResponse(responseCode = "204", description = "Rental deleted successfully"))
    @DeleteMapping("/{rentalUuid}")
    public ResponseEntity<Void> deleteRentalByAdmin(@PathVariable UUID rentalUuid) {
        String adminId = SecurityUtils.getRequesterIdentifier();
        log.warn("ADMIN ACTION: [{}] requesting to delete rental with UUID: {}", adminId, rentalUuid);
        Rental existingRental = rentalService.read(rentalUuid);
        rentalService.delete(existingRental.getId());
        return ResponseEntity.noContent().build();
    }
}