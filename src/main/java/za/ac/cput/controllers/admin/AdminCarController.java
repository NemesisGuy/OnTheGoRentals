package za.ac.cput.controllers.admin;

/**
 * AdminCarController.java
 * Controller for the Car entity (admin only)
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023 // Updated: [Your Current Date]
 */

import jakarta.validation.Valid; // For validating DTOs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import za.ac.cput.domain.Car; // Service layer still uses Car entity
import za.ac.cput.domain.Car;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.mapper.CarMapper; // Your mapper
import za.ac.cput.service.impl.CarServiceImpl; // Or ICarService

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/cars")
// @CrossOrigin(...) // Consider global CORS configuration
public class AdminCarController {

    private final CarServiceImpl carService; // Or ICarService

    @Autowired
    public AdminCarController(CarServiceImpl carService) {
        this.carService = carService;
    }

    // Get all cars (including potentially soft-deleted ones if service method allows, or only non-deleted)
    // Returns List<CarDTO>
    @GetMapping("/all")
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        // Assuming carService.getAllAdminView() or similar fetches all cars relevant for admin
        // If carService.getAll() only returns non-deleted, that's fine too.
        List<za.ac.cput.domain.Car> cars = carService.getAll(); // Service returns entities
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<CarResponseDTO> carDTOs = CarMapper.toDtoList(cars); // Map to DTOs
        return ResponseEntity.ok(carDTOs);
    }

    // Create new car using CarDTO
    @PostMapping("/create")
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody Car car) {
        // The carService.createCar method should now accept a CarDTO
        // and handle the mapping to a Car entity internally before saving.
        // Its return type should also be CarDTO.
        Car createdCar = carService.create(car); // Assuming service handles DTO
        if (createdCar == null) { // If service returns null on failure
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Or handle as needed
        }
        // If the service returns a Car entity, map it to DTO here
        CarResponseDTO createdCarDto = CarMapper.toDto(createdCar); // Assuming service returns entity
        return new ResponseEntity<>(createdCarDto, HttpStatus.CREATED);
    }

    // Get car by UUID, returns CarDTO or 404
    @GetMapping("/read/{carUuid}") // Changed path variable to carUuid for clarity
    public ResponseEntity<CarResponseDTO> readCarByUuid(@PathVariable UUID carUuid) {
        // carService.getCarByUuid should return CarDTO directly, or service returns entity and controller maps
        // Following the pattern where service returns DTO:

         Car carEntity = carService.read(carUuid);
         if (carEntity == null) return ResponseEntity.notFound().build();
        CarResponseDTO carDto = CarMapper.toDto(carEntity);


        return ResponseEntity.ok(carDto);
    }

    // Update existing car by UUID using CarDTO
    @PutMapping("/update/{carUuid}") // Changed path variable
    public ResponseEntity<CarResponseDTO> updateCar(@PathVariable UUID carUuid, @Valid @RequestBody Car car) {
        // carService.updateCar should accept UUID and CarDTO
        // and return the updated CarDTO.
       /// Car car = carService.findByUuid(carUuid);
        Car updatedCar = carService.update(car);

        if (updatedCar == null) { // If service returns null on not found
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(CarMapper.toDto(updatedCar));
    }

    // Soft delete car by UUID
    @DeleteMapping("/delete/{carUuid}") // Changed path variable
    public ResponseEntity<Void> deleteCar(@PathVariable UUID carUuid) {
        // carService.deleteCarByUuid should handle the soft delete logic
        boolean deleted = carService.delete(carUuid); // Assuming a dedicated soft delete method
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    // The mapPriceGroup helper is likely no longer needed in the controller
    // if CarDTO uses the PriceGroup enum directly and validation/mapping occurs
    // earlier (e.g., during JSON deserialization into CarDTO, or in the service layer
    // when mapping CarDTO to Car entity if the DTO took a string).
    // If CarDTO takes a String for priceGroup, then the service layer (or mapper) would handle this.
    /*
    private PriceGroup mapPriceGroup(String priceGroupString) {
        if (priceGroupString == null) return PriceGroup.NONE; // Or throw error
        try {
            return PriceGroup.valueOf(priceGroupString.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Log error or handle as invalid input, perhaps returning a default or throwing an exception
            System.err.println("Invalid priceGroupString provided: " + priceGroupString);
            return PriceGroup.NONE; // Or throw new BadRequestException("Invalid price group: " + priceGroupString);
        }
    }
    */
}