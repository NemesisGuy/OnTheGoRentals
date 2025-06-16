package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.RentalRequestDTO;
import za.ac.cput.domain.dto.request.RentalUpdateDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.DriverResponseDTO;
import za.ac.cput.domain.dto.response.RentalResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.service.IFileStorageService;

import java.util.List;
import java.util.stream.Collectors;

public class RentalMapper {

    public static RentalResponseDTO toDto(Rental rental, IFileStorageService fileStorageService) {
        if (rental == null) return null;

        UserResponseDTO userDto = (rental.getUser() != null) ? UserMapper.toDto(rental.getUser(), fileStorageService) : null;
        CarResponseDTO carDto = (rental.getCar() != null) ? CarMapper.toDto(rental.getCar(), fileStorageService) : null;
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
                .expectedReturnDate(rental.getExpectedReturnDate())
                .returnedDate(rental.getReturnedDate()) // Added
                .status(String.valueOf(rental.getStatus()))
                .build();
    }

    public static List<RentalResponseDTO> toDtoList(List<Rental> rentals, IFileStorageService fileStorageService) {
        return rentals.stream()
                // Pass the service down for each conversion
                .map(rental -> toDto(rental, fileStorageService))
                .collect(Collectors.toList());
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
                .setExpectedReturnDate(createDto.getExpectedReturnDate())
                .setReturnedDate(createDto.getReturnedDate())
                .setIssuer(createDto.getIssuer()) // Assuming issuer is a User entity or ID
                .setReceiver(createDto.getReceiver()) // Assuming receiver is a User entity or ID

                // status, fine, issuerId, receiverId typically set by business logic or defaults in @PrePersist
                // uuid, id, createdAt, updatedAt, deleted handled by entity/JPA
                .build();
    }
    /*// For Creating a Rental (used by Admin or User flow if DTO is same)
    // Service layer will fetch User, Car, Driver entities based on UUIDs in createDto
    public static Rental toEntity(RentalRequestDTO createDto, User userEntity, Car carEntity, Driver driverEntity) {
        if (createDto == null) throw new IllegalArgumentException("RentalRequestDTO cannot be null.");
        if (userEntity == null) throw new IllegalArgumentException("User entity is required.");
        if (carEntity == null) throw new IllegalArgumentException("Car entity is required.");

        Rental.Builder builder = new Rental.Builder()
                .setUser(userEntity)
                .setCar(carEntity)
                .setDriver(driverEntity) // driverEntity can be null
                .setIssuedDate(createDto.getIssuedDate())
                .setReturnedDate(createDto.getReturnedDate());

        if (createDto.getStatus() != null) {
            builder.setStatus(createDto.getStatus());
        } else {
            // Default status for a new rental, could be different if admin creates vs user
            builder.setStatus(RentalStatus.PENDING_CONFIRMATION);
        }
        if (createDto.getIssuer() != null) {
            builder.setIssuer(createDto.getIssuer());
        }
        // uuid, id, createdAt, updatedAt, deleted handled by entity/JPA @PrePersist or defaults
        // fine, receiverId usually not set on initial creation by user/basic admin.

        return builder.build();
    }*/


    /**
     * Applies updates from a RentalUpdateDTO to an existing Rental entity.
     * This method now correctly handles all fields, including status, dates, and receiver.
     *
     * @param updateDto      The DTO containing the update data.
     * @param existingRental The existing Rental entity to be updated.
     * @param userEntity     The full User entity (can be the existing one or a new one).
     * @param carEntity      The full Car entity.
     * @param driverEntity   The full Driver entity.
     * @return A new, updated Rental instance ready for persistence.
     */
    public static Rental applyUpdateDtoToEntity(RentalUpdateDTO updateDto, Rental existingRental, User userEntity, Car carEntity, Driver driverEntity) {
        if (updateDto == null || existingRental == null) {
            throw new IllegalArgumentException("Update DTO and existing Rental entity must not be null.");
        }

        Rental.Builder builder = new Rental.Builder().copy(existingRental)
                .setUser(userEntity)
                .setCar(carEntity)
                .setDriver(driverEntity);

        // --- THE FIX IS HERE: Add logic for all missing fields ---

        if (updateDto.getIssuer() != null) {
            builder.setIssuer(updateDto.getIssuer());
        }
        if (updateDto.getReceiver() != null) {
            builder.setReceiver(updateDto.getReceiver()); // <-- FIX for receiver
        }
        if (updateDto.getFine() != null) {
            builder.setFine(updateDto.getFine().intValue());
        }
        if (updateDto.getIssuedDate() != null) {
            builder.setIssuedDate(updateDto.getIssuedDate());
        }
        if (updateDto.getExpectedReturnDate() != null) {
            builder.setExpectedReturnDate(updateDto.getExpectedReturnDate()); // <-- FIX for expectedReturnDate
        }
        if (updateDto.getReturnedDate() != null) {
            builder.setReturnedDate(updateDto.getReturnedDate());
        }
        if (updateDto.getStatus() != null) {
            try {
                // Convert the status string from the DTO to the RentalStatus enum
                RentalStatus newStatus = RentalStatus.valueOf(updateDto.getStatus().trim().toUpperCase());
                builder.setStatus(newStatus); // <-- FIX for status
            } catch (IllegalArgumentException e) {
                // Log a warning if an invalid status string is provided, but don't stop the update.
                // Or you could throw a BadRequestException here.
                System.err.println("Invalid status value in DTO: " + updateDto.getStatus());
            }
        }

        return builder.build();
    }
}