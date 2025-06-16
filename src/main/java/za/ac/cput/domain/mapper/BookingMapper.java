package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.BookingRequestDTO;
import za.ac.cput.domain.dto.request.BookingUpdateDTO;
import za.ac.cput.domain.dto.response.BookingResponseDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.service.IFileStorageService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    /**
     * Converts a Booking entity to a BookingResponseDTO.
     *
     * @param booking The Booking entity.
     * @return The corresponding BookingResponseDTO, or null if the booking entity is null.
     */
    public static BookingResponseDTO toDto(Booking booking, IFileStorageService fileStorageService) {
        if (booking == null) {
            return null;
        }


        UserResponseDTO userDto = null;
        if (booking.getUser() != null) {
            userDto = UserMapper.toDto(booking.getUser(), fileStorageService); // Uses UserMapper
        }

        CarResponseDTO carDto = null;
        if (booking.getCar() != null) {
            carDto = CarMapper.toDto(booking.getCar(), fileStorageService); // Uses CarMapper
        }

        return BookingResponseDTO.builder()
                .uuid(booking.getUuid()) // UUID of the Booking itself
                .user(userDto)
                .car(carDto)
                .bookingStartDate(booking.getStartDate())
                .bookingEndDate(booking.getEndDate())
                .status(booking.getStatus()) // Assuming Booking entity
                .build();
    }

    /**
     * Converts a BookingRequestDTO (and related fetched entities) to a new Booking entity.
     * This is typically used when creating a new booking.
     *
     * @param requestDto The BookingRequestDTO containing data from the client.
     * @param userEntity The fetched User entity (based on userUuid from DTO).
     * @param carEntity  The fetched Car entity (based on carUuid from DTO).
     * @return A new Booking entity populated from the DTO and related entities.
     */
    public static Booking toEntity(BookingRequestDTO requestDto, User userEntity, Car carEntity) {
        if (requestDto == null) {
            return null;
        }

        return new Booking.Builder()
                // UUID for the new Booking will be set by @PrePersist in the Booking entity itself.

                .setUser(userEntity)
                .setCar(carEntity)

                .setStartDate(requestDto.getBookingStartDate())
                .setEndDate(requestDto.getBookingEndDate())

                // Status for a new booking is typically set by backend business logic.
                // If the DTO *can* specify an initial status, you'd map it here, converting String to Enum.
                // For example, if BookingRequestDTO had a 'statusString' field:
                // if (requestDto.getStatusString() != null) {
                //     try {
                //         booking.setStatus(RentalStatus.valueOf(requestDto.getStatusString().toUpperCase()));
                //     } catch (IllegalArgumentException e) {
                //         // Handle invalid status, e.g., throw exception or set a default
                //         booking.setStatus(RentalStatus.PENDING); // Default status
                //     }
                // } else {
                //if null then we set to ACTIVE
                .setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : BookingStatus.CONFIRMED) // Default to ACTIVE if not specified
                // }

                .setDeleted(false) // Default for new bookings

                // Other fields like fine, issuerId, receiverId would be set by business logic
                // in the service layer, not directly from a simple create DTO.
                .build();
    }

    public static Booking toEntity(BookingRequestDTO requestDto, User userEntity, Car carEntity, Driver driverEntity) {
        if (requestDto == null) {
            return null;
        }

        return new Booking.Builder()
                // UUID for the new Booking will be set by @PrePersist in the Booking entity itself.

                .setUser(userEntity)
                .setCar(carEntity)
                .setDriver(driverEntity) // Optional, can be null if not provided

                .setStartDate(requestDto.getBookingStartDate())
                .setEndDate(requestDto.getBookingEndDate())

                // Status for a new booking is typically set by backend business logic.
                // If the DTO *can* specify an initial status, you'd map it here, converting String to Enum.
                // For example, if BookingRequestDTO had a 'statusString' field:
                // if (requestDto.getStatusString() != null) {
                //     try {
                //         booking.setStatus(RentalStatus.valueOf(requestDto.getStatusString().toUpperCase()));
                //     } catch (IllegalArgumentException e) {
                //         // Handle invalid status, e.g., throw exception or set a default
                //         booking.setStatus(RentalStatus.PENDING); // Default status
                //     }
                // } else {
                // if null then we set to CONFIRMED
                .setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : BookingStatus.CONFIRMED) // Default to CONFIRMED if not specified
                // }

                .setDeleted(false) // Default for new bookings

                // Other fields like fine, issuerId, receiverId would be set by business logic
                // in the service layer, not directly from a simple create DTO.
                .build();
    }

    /**
     * Updates an existing Booking entity from a BookingRequestDTO (or a specific BookingUpdateDTO).
     * The Booking entity to update should be fetched from the database first.
     *
     * @param updateDto        The DTO containing update information.
     * @param existingBooking  The existing Booking entity to be updated.
     * @param updatedCarEntity If the car for the booking can be changed (optional, pass null if not changing).
     */
    public static void updateEntityFromDto(BookingRequestDTO updateDto, Booking existingBooking, Car updatedCarEntity) {
        if (updateDto == null || existingBooking == null) {
            return;
        }

        // Typically, user is not changed for an existing booking.
        // If car can be changed:
        Booking.Builder builder = new Booking.Builder().copy(existingBooking);
        if (updatedCarEntity != null) {
            builder.setCar(updatedCarEntity);
        }

        if (updateDto.getBookingStartDate() != null) {
            builder.setStartDate(updateDto.getBookingStartDate());
        }
        if (updateDto.getBookingEndDate() != null) {
            builder.setEndDate(updateDto.getBookingEndDate());
        }

        // If status can be updated via this DTO
        // if (updateDto.getStatus() != null) { // Assuming BookingRequestDTO has 'status' as String
        //     try {
        //         existingBooking.setStatus(RentalStatus.valueOf(updateDto.getStatus().toUpperCase()));
        //     } catch (IllegalArgumentException e) {
        //         // Handle invalid status
        //         System.err.println("Invalid status string in DTO for update: " + updateDto.getStatus());
        //     }
        // }
        // Other updatable fields...
    }


    /**
     * Converts a list of Booking entities to a list of BookingResponseDTOs.
     *
     * @param bookings List of Booking entities.
     * @return List of BookingResponseDTOs.
     */
    public static List<BookingResponseDTO> toDtoList(List<Booking> bookings, IFileStorageService fileStorageService) {
        if (bookings == null) {
            return Collections.emptyList();
        }
        return bookings.stream()
                // Pass the service down for each conversion
                .map(booking -> toDto(booking, fileStorageService))
                .collect(Collectors.toList());
    }

    // For Admin Updating a Booking
    public static Booking applyUpdateDtoToEntity(
            BookingUpdateDTO updateDto,
            Booking existingBooking,
            User newUserEntity, // Pass if user can be changed by admin
            Car newCarEntity,     // Pass if car can be changed by admin
            Driver newDriverEntity // Pass if driver can be changed by admin
    ) {
        if (updateDto == null || existingBooking == null) {
            throw new IllegalArgumentException("Update DTO and existing Booking entity must not be null.");
        }

        Booking.Builder builder = new Booking.Builder().copy(existingBooking);

        // Only update if DTO field is provided (for partial updates)
        if (newUserEntity != null) builder.setUser(newUserEntity); // Admin changed user
        if (newCarEntity != null) builder.setCar(newCarEntity);     // Admin changed car
        if (newDriverEntity != null || (updateDto.getDriverUuid() == null && existingBooking.getDriver() != null)) {
            // Handle explicit setting to null if DTO driverUuid is null but existing had one
            builder.setDriver(newDriverEntity);
        }


        if (updateDto.getBookingStartDate() != null) builder.setStartDate(updateDto.getBookingStartDate());
        if (updateDto.getBookingEndDate() != null) builder.setEndDate(updateDto.getBookingEndDate());
        if (updateDto.getStatus() != null) builder.setStatus(updateDto.getStatus());
/*
        if (updateDto.getIssuerId() != null) builder.(updateDto.getIssuerId());
*/
       /* if (updateDto.getReceiverId() != null) builder.receiverId(updateDto.getReceiverId());
        if (updateDto.getFine() != null) builder.fine(updateDto.getFine());
        if (updateDto.getActualReturnedDate() != null) builder.setEndDate(updateDto.getActualReturnedDate());*/

        // id, uuid, createdAt are preserved. updatedAt handled by @PreUpdate.
        return builder.build();
    }

}