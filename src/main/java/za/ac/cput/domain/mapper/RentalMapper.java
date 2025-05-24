package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Car;
import za.ac.cput.domain.Driver; // Assuming Driver entity
import za.ac.cput.domain.Rental;
import za.ac.cput.domain.security.User;
import za.ac.cput.domain.dto.request.RentalRequestDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.DriverResponseDTO; // Assuming this DTO
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.enums.RentalStatus; // Assuming enum

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RentalMapper {

    /**
     * Converts a Rental entity to a RentalResponseDTO.
     */
    public static RentalResponseDTO toDto(Rental rental) {
        if (rental == null) {
            return null;
        }

        UserResponseDTO userDto = (rental.getUser() != null) ? UserMapper.toDto(rental.getUser()) : null;
        CarResponseDTO carDto = (rental.getCar() != null) ? CarMapper.toDto(rental.getCar()) : null;
       // DriverResponseDTO driverDto = (rental.getDriver() != null) ? DriverMapper.toDto(rental.getDriver()) : null; // Assuming DriverMapper

        return RentalResponseDTO.builder()
                .uuid(rental.getUuid())
                .user(userDto)
                .car(carDto)
                //.driver(driverDto)
                .issuer(rental.getIssuer())
                .receiver(rental.getReceiver())
                .fine(rental.getFine())
                .issuedDate(rental.getIssuedDate())
                .returnedDate(rental.getReturnedDate())
                .status(String.valueOf(rental.getStatus()))
                .build();
    }

    /**
     * Converts a RentalRequestDTO (and related fetched entities) to a new Rental entity.
     * Typically used for creating a new rental.
     *
     * @param requestDto  The DTO containing data for the new rental.
     * @param userEntity  The fetched User entity.
     * @param carEntity   The fetched Car entity.
     * @param driverEntity The fetched Driver entity (can be null if driver is optional).
     * @return A new Rental entity.
     */
    public static Rental toEntity(RentalRequestDTO requestDto, User userEntity, Car carEntity, Driver driverEntity) {
        if (requestDto == null) {
            return null;
        }

        Rental rental = new Rental();
        // UUID for the new Rental will be set by @PrePersist in the Rental entity.

        if (userEntity == null) throw new IllegalArgumentException("User entity cannot be null for creating a rental.");
        if (carEntity == null) throw new IllegalArgumentException("Car entity cannot be null for creating a rental.");

        rental.setUser(userEntity);
        rental.setCar(carEntity);
        rental.setDriver(driverEntity); // Can be null if driver is optional for a rental

        rental.setIssuedDate(requestDto.getIssuedDate());
        rental.setReturnedDate(requestDto.getReturnedDate()); // Using expectedReturnedDate from DTO

        // Issuer, Receiver, Fine, Status are typically set by business logic in the service
        // rental.setIssuerId(...);
        // rental.setReceiverId(...);
        // rental.setFine(0.0); // Initial fine
        rental.setStatus(RentalStatus.ACTIVE); // Example initial status for a new rental from request
        rental.setDeleted(false);

        return rental;
    }

    /**
     * Updates an existing Rental entity from a RentalRequestDTO (or a specific RentalUpdateDTO).
     *
     * @param updateDto      The DTO with update information.
     * @param existingRental The existing Rental entity to update.
     * @param updatedCarEntity Optional: new Car entity if car is being changed.
     * @param updatedDriverEntity Optional: new Driver entity if driver is being changed.
     */
    public static void updateEntityFromDto(RentalRequestDTO updateDto, Rental existingRental, Car updatedCarEntity, Driver updatedDriverEntity) {
        if (updateDto == null || existingRental == null) {
            return;
        }

        // Typically, user is not changed on an existing rental.
        if (updatedCarEntity != null) {
            existingRental.setCar(updatedCarEntity);
        }
        if (updatedDriverEntity != null) {
            existingRental.setDriver(updatedDriverEntity);
        }

        if (updateDto.getIssuedDate() != null) {
            existingRental.setIssuedDate(updateDto.getIssuedDate());
        }
        if (updateDto.getReturnedDate() != null) {
            existingRental.setReturnedDate(updateDto.getReturnedDate());
        }

        // Status updates might have specific business logic in the service
        // Example:
        // if (updateDto.getStatus() != null) {
        //     try {
        //         existingRental.setStatus(RentalStatus.valueOf(updateDto.getStatus().toUpperCase()));
        //     } catch (IllegalArgumentException e) { /* handle error */ }
        // }
    }


    public static List<RentalResponseDTO> toDtoList(List<Rental> rentals) {
        if (rentals == null) {
            return null;
        }
        return rentals.stream()
                .map(RentalMapper::toDto)
                .collect(Collectors.toList());
    }
}