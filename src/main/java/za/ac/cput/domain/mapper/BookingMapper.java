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

/**
 * BookingMapper.java
 * A stateless utility class for mapping between Booking entities and DTOs.
 * The `toDto` methods require the public API URL to correctly resolve nested image URLs.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
public class BookingMapper {

    /**
     * Converts a Booking entity to its DTO representation.
     * This now requires the public API URL to correctly map the nested User and Car DTOs.
     *
     * @param booking            The Booking entity.
     * @param fileStorageService The service for file operations.
     * @param publicApiUrl       The base public URL of the API.
     * @return A BookingResponseDTO.
     */
    public static BookingResponseDTO toDto(Booking booking, IFileStorageService fileStorageService, String publicApiUrl) {
        if (booking == null) return null;

        // --- THE FIX IS HERE: Pass the publicApiUrl to the nested mappers ---
        UserResponseDTO userDto = (booking.getUser() != null) ? UserMapper.toDto(booking.getUser(), fileStorageService, publicApiUrl) : null;
        CarResponseDTO carDto = (booking.getCar() != null) ? CarMapper.toDto(booking.getCar(), fileStorageService, publicApiUrl) : null;

        return BookingResponseDTO.builder()
                .uuid(booking.getUuid())
                .user(userDto)
                .car(carDto)
                .bookingStartDate(booking.getStartDate())
                .bookingEndDate(booking.getEndDate())
                .status(booking.getStatus())
                .build();
    }

    /**
     * Converts a list of Booking entities to a list of DTOs.
     *
     * @param bookings           The list of Booking entities.
     * @param fileStorageService The service for file operations.
     * @param publicApiUrl       The base public URL of the API.
     * @return A list of BookingResponseDTOs.
     */
    public static List<BookingResponseDTO> toDtoList(List<Booking> bookings, IFileStorageService fileStorageService, String publicApiUrl) {
        if (bookings == null) return Collections.emptyList();

        return bookings.stream()
                .map(booking -> toDto(booking, fileStorageService, publicApiUrl))
                .collect(Collectors.toList());
    }

    /**
     * Converts a BookingRequestDTO to a new Booking entity.
     */
    public static Booking toEntity(BookingRequestDTO requestDto, User userEntity, Car carEntity, Driver driverEntity) {
        if (requestDto == null) return null;

        return new Booking.Builder()
                .setUser(userEntity)
                .setCar(carEntity)
                .setDriver(driverEntity) // This can be null
                .setStartDate(requestDto.getBookingStartDate())
                .setEndDate(requestDto.getBookingEndDate())
                .setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : BookingStatus.CONFIRMED)
                .build();
    }

    /**
     * Applies updates from a BookingUpdateDTO to an existing Booking entity.
     */
    public static Booking applyUpdateDtoToEntity(BookingUpdateDTO updateDto, Booking existingBooking, User newUserEntity, Car newCarEntity, Driver newDriverEntity) {
        if (updateDto == null || existingBooking == null) {
            throw new IllegalArgumentException("Update DTO and existing Booking entity must not be null.");
        }

        Booking.Builder builder = new Booking.Builder().copy(existingBooking);

        if (newUserEntity != null) builder.setUser(newUserEntity);
        if (newCarEntity != null) builder.setCar(newCarEntity);

        // Handle setting driver to null explicitly
        if (updateDto.getDriverUuid() == null && existingBooking.getDriver() != null) {
            builder.setDriver(null);
        } else if (newDriverEntity != null) {
            builder.setDriver(newDriverEntity);
        }

        if (updateDto.getBookingStartDate() != null) builder.setStartDate(updateDto.getBookingStartDate());
        if (updateDto.getBookingEndDate() != null) builder.setEndDate(updateDto.getBookingEndDate());
        if (updateDto.getStatus() != null) builder.setStatus(updateDto.getStatus());

        return builder.build();
    }
}