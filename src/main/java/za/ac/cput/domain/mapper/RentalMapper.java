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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RentalMapper {

    /**
     * Converts a Rental entity to its DTO representation.
     * This now requires the public API URL to correctly map nested DTOs with images.
     *
     * @param rental             The Rental entity.
     * @param fileStorageService The service for file operations.
     * @param publicApiUrl       The base public URL of the API.
     * @return A RentalResponseDTO.
     */
    public static RentalResponseDTO toDto(Rental rental, IFileStorageService fileStorageService, String publicApiUrl) {
        if (rental == null) return null;

        // --- THE FIX IS HERE: Pass the publicApiUrl to the nested mappers ---
        UserResponseDTO userDto = (rental.getUser() != null) ? UserMapper.toDto(rental.getUser(), fileStorageService, publicApiUrl) : null;
        CarResponseDTO carDto = (rental.getCar() != null) ? CarMapper.toDto(rental.getCar(), fileStorageService, publicApiUrl) : null;

        // DriverMapper does not need the file service or public URL (assuming drivers have no images)
        DriverResponseDTO driverDto = (rental.getDriver() != null) ? DriverMapper.toDto(rental.getDriver()) : null;

        return RentalResponseDTO.builder()
                .uuid(rental.getUuid())
                .user(userDto)
                .car(carDto)
                .driver(driverDto)
                .issuer(rental.getIssuer())
                .receiver(rental.getReceiver())
                .fine(rental.getFine())
                .issuedDate(rental.getIssuedDate())
                .expectedReturnDate(rental.getExpectedReturnDate())
                .returnedDate(rental.getReturnedDate())
                .status(String.valueOf(rental.getStatus()))
                .build();
    }

    /**
     * Converts a list of Rental entities to a list of DTOs.
     *
     * @param rentals            The list of Rental entities.
     * @param fileStorageService The service for file operations.
     * @param publicApiUrl       The base public URL of the API.
     * @return A list of RentalResponseDTOs.
     */
    public static List<RentalResponseDTO> toDtoList(List<Rental> rentals, IFileStorageService fileStorageService, String publicApiUrl) {
        if (rentals == null) return Collections.emptyList();

        return rentals.stream()
                // Pass the necessary parameters down for each conversion
                .map(rental -> toDto(rental, fileStorageService, publicApiUrl))
                .collect(Collectors.toList());
    }

    /**
     * Converts a RentalRequestDTO to a new Rental entity.
     */
    public static Rental toEntity(RentalRequestDTO createDto, User userEntity, Car carEntity, Driver driverEntity) {
        if (createDto == null) return null;
        if (userEntity == null) throw new IllegalArgumentException("User entity is required for rental creation.");
        if (carEntity == null) throw new IllegalArgumentException("Car entity is required for rental creation.");

        return new Rental.Builder()
                .setUser(userEntity) // This seems to be missing from Rental, assuming it should be part of Booking
                .setCar(carEntity)   // Same here
                .setDriver(driverEntity)
                .setIssuedDate(createDto.getIssuedDate())
                .setExpectedReturnDate(createDto.getExpectedReturnDate())
                .setIssuer(createDto.getIssuer())
                .build();
    }

    /**
     * Applies updates from a RentalUpdateDTO to an existing Rental entity.
     */
    public static Rental applyUpdateDtoToEntity(RentalUpdateDTO updateDto, Rental existingRental, User userEntity, Car carEntity, Driver driverEntity) {
        if (updateDto == null || existingRental == null) {
            throw new IllegalArgumentException("Update DTO and existing Rental entity must not be null.");
        }

        Rental.Builder builder = new Rental.Builder().copy(existingRental)
                .setUser(userEntity) // Assuming user can be changed
                .setCar(carEntity)     // Assuming car can be changed
                .setDriver(driverEntity); // Assuming driver can be changed

        if (updateDto.getIssuer() != null) builder.setIssuer(updateDto.getIssuer());
        if (updateDto.getReceiver() != null) builder.setReceiver(updateDto.getReceiver());
        if (updateDto.getFine() != null) builder.setFine(updateDto.getFine().intValue());
        if (updateDto.getIssuedDate() != null) builder.setIssuedDate(updateDto.getIssuedDate());
        if (updateDto.getExpectedReturnDate() != null) builder.setExpectedReturnDate(updateDto.getExpectedReturnDate());
        if (updateDto.getReturnedDate() != null) builder.setReturnedDate(updateDto.getReturnedDate());
        if (updateDto.getStatus() != null) {
            try {
                RentalStatus newStatus = RentalStatus.valueOf(updateDto.getStatus().trim().toUpperCase());
                builder.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid status value in DTO: " + updateDto.getStatus());
            }
        }
        return builder.build();
    }
}