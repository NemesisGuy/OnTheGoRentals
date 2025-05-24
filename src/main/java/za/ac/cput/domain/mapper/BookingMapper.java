package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Booking;

import za.ac.cput.domain.dto.response.BookingResponseDTO;



import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;     // Entity
import za.ac.cput.domain.security.User; // Entity
import za.ac.cput.domain.dto.request.BookingRequestDTO;
import za.ac.cput.domain.dto.response.BookingResponseDTO;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.enums.RentalStatus; // Make sure this enum is correctly imported

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    /**
     * Converts a Booking entity to a BookingResponseDTO.
     *
     * @param booking The Booking entity.
     * @return The corresponding BookingResponseDTO, or null if the booking entity is null.
     */
    public static BookingResponseDTO toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        UserResponseDTO userDto = null;
        if (booking.getUser() != null) {
            userDto = UserMapper.toDto(booking.getUser()); // Uses UserMapper
        }

        CarResponseDTO carDto = null;
        if (booking.getCar() != null) {
            carDto = CarMapper.toDto(booking.getCar()); // Uses CarMapper
        }

        return BookingResponseDTO.builder()
                .uuid(booking.getUuid()) // UUID of the Booking itself
                .user(userDto)
                .car(carDto)
                .bookingStartDate(booking.getBookingStartDate())
                .bookingEndDate(booking.getBookingEndDate())
                .status(RentalStatus.valueOf(booking.getStatus())) // Assuming Booking entity has RentalStatus enum
                .build();
    }

    /**
     * Converts a BookingRequestDTO (and related fetched entities) to a new Booking entity.
     * This is typically used when creating a new booking.
     *
     * @param requestDto  The BookingRequestDTO containing data from the client.
     * @param userEntity  The fetched User entity (based on userUuid from DTO).
     * @param carEntity   The fetched Car entity (based on carUuid from DTO).
     * @return A new Booking entity populated from the DTO and related entities.
     */
    public static Booking toEntity(BookingRequestDTO requestDto, User userEntity, Car carEntity) {
        if (requestDto == null) {
            return null;
        }

        Booking booking = new Booking();
        // UUID for the new Booking will be set by @PrePersist in the Booking entity itself.

        booking.setUser(userEntity); // Assign the fully fetched User entity
        booking.setCar(carEntity);   // Assign the fully fetched Car entity

        booking.setBookingStartDate(requestDto.getBookingStartDate());
        booking.setBookingEndDate(requestDto.getBookingEndDate());

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
        booking.setStatus(String.valueOf(RentalStatus.ACTIVE)); // Default status for a new booking
        // }

        booking.setDeleted(false); // Default for new bookings

        // Other fields like fine, issuerId, receiverId would be set by business logic
        // in the service layer, not directly from a simple create DTO.
        return booking;
    }

    /**
     * Updates an existing Booking entity from a BookingRequestDTO (or a specific BookingUpdateDTO).
     * The Booking entity to update should be fetched from the database first.
     *
     * @param updateDto      The DTO containing update information.
     * @param existingBooking The existing Booking entity to be updated.
     * @param updatedCarEntity If the car for the booking can be changed (optional, pass null if not changing).
     */
    public static void updateEntityFromDto(BookingRequestDTO updateDto, Booking existingBooking, Car updatedCarEntity) {
        if (updateDto == null || existingBooking == null) {
            return;
        }

        // Typically, user is not changed for an existing booking.
        // If car can be changed:
        if (updatedCarEntity != null) {
            existingBooking.setCar(updatedCarEntity);
        }

        if (updateDto.getBookingStartDate() != null) {
            existingBooking.setBookingStartDate(updateDto.getBookingStartDate());
        }
        if (updateDto.getBookingEndDate() != null) {
            existingBooking.setBookingEndDate(updateDto.getBookingEndDate());
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
    public static List<BookingResponseDTO> toDtoList(List<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}