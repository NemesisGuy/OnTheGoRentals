// BookingResponseDTO.java
package za.ac.cput.domain.dto.response;

import lombok.*;
import za.ac.cput.domain.dto.response.CarResponseDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.UUID;


import lombok.*;
import za.ac.cput.domain.enums.RentalStatus; // Assuming RentalStatus enum exists

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
    private RentalStatus status; // Use the enum type for clarity if possible, or String
    // Potentially other fields like fine, issuer/receiver DTOs if applicable
}