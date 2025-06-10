// BookingResponseDTO.java
package za.ac.cput.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {
    private UUID uuid; // UUID of the Booking itself
    private UserResponseDTO user; // Nested DTO for user details
    private CarResponseDTO car;   // Nested DTO for car details
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private BookingStatus status; // Use the enum type for clarity if possible
}