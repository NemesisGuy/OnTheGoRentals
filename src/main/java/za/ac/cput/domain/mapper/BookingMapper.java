package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.dto.BookingDTO;
import za.ac.cput.domain.dto.UserDTO;
import za.ac.cput.domain.security.User;

public class BookingMapper {

    public static BookingDTO toDto(Booking booking) {
        if (booking == null) return null;

        return BookingDTO.builder()
                .id(booking.getId())
                .user(UserMapper.toDto(booking.getUser()))
                .car(booking.getCar()) // Assuming car is already a DTO-compatible object
                .bookingStartDate(booking.getBookingStartDate())
                .bookingEndDate(booking.getBookingEndDate())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toEntity(BookingDTO dto) {
        if (dto == null) return null;

        return Booking.builder()
                .id(dto.getId())
                .user(UserMapper.toEntity(dto.getUser()))
                .car(dto.getCar())
                .bookingDate(dto.getBookingStartDate())
                .returnedDate(dto.getBookingEndDate())
                .status(dto.getStatus())
                .build();
    }
}
