package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Car;
import za.ac.cput.domain.Driver;
import za.ac.cput.domain.Rental;
import za.ac.cput.domain.security.User;
import za.ac.cput.domain.dto.request.RentalRequestDTO;
import za.ac.cput.domain.dto.request.RentalUpdateDTO; // Assuming you create this
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.DriverResponseDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.enums.RentalStatus; // Ensure correct import

import java.util.List;
import java.util.stream.Collectors;

public class RentalMapper {

    public static RentalResponseDTO toDto(Rental rental) {
        if (rental == null) return null;

        UserResponseDTO userDto = (rental.getUser() != null) ? UserMapper.toDto(rental.getUser()) : null;
        CarResponseDTO carDto = (rental.getCar() != null) ? CarMapper.toDto(rental.getCar()) : null;
        DriverResponseDTO driverDto = (rental.getDriver() != null) ? DriverMapper.toDto(rental.getDriver()) : null;

        return RentalResponseDTO.builder()
                .uuid(rental.getUuid())
                .user(userDto)
                .car(carDto)
                .driver(driverDto)
                .issuer(rental.getIssuer()) // Assuming entity has getIssuerId()
                .receiver(rental.getReceiver()) // Assuming entity has getReceiverId()
                .fine(rental.getFine())
                .issuedDate(rental.getIssuedDate())
                .returnedDate(rental.getReturnedDate())
                .returnedDate(rental.getReturnedDate()) // Added
                .status(String.valueOf(rental.getStatus()))
                .build();
    }

    public static List<RentalResponseDTO> toDtoList(List<Rental> rentals) {
        if (rentals == null) return null;
        return rentals.stream().map(RentalMapper::toDto).collect(Collectors.toList());
    }

    public static Rental toEntity(RentalRequestDTO createDto, User userEntity, Car carEntity, Driver driverEntity) {
        if (createDto == null) return null;
        if (userEntity == null) throw new IllegalArgumentException("User entity is required for rental creation.");
        if (carEntity == null) throw new IllegalArgumentException("Car entity is required for rental creation.");

        // Using the Rental entity's builder
        return new Rental.Builder()
                .setUser(userEntity)
                .setCar(carEntity)
                .setDriver(driverEntity) // driverEntity can be null if optional
                .setIssuedDate(createDto.getIssuedDate())
                .setReturnedDate(createDto.getReturnedDate())
                // status, fine, issuerId, receiverId typically set by business logic or defaults in @PrePersist
                // uuid, id, createdAt, updatedAt, deleted handled by entity/JPA
                .build();
    }

    public static Rental applyUpdateDtoToEntity(RentalUpdateDTO updateDto, Rental existingRental, Car newCarEntity, Driver newDriverEntity) {
        if (updateDto == null || existingRental == null) {
            throw new IllegalArgumentException("Update DTO and existing Rental entity must not be null.");
        }

        Rental.Builder builder = new Rental.Builder().copy(existingRental);

        if (updateDto.getExpectedReturnedDate() != null) {
            builder.setReturnedDate(updateDto.getExpectedReturnedDate());
        }
        // Example if car or driver can be changed during an update (complex logic)
        // if (newCarEntity != null) {
        //     builder.car(newCarEntity);
        // }
        // if (newDriverEntity != null) {
        //     builder.driver(newDriverEntity);
        // }
        // Status updates are usually distinct actions (confirm, cancel, complete)
        // if (updateDto.getStatus() != null) {
        //     builder.status(updateDto.getStatus());
        // }

        return builder.build();
    }
}