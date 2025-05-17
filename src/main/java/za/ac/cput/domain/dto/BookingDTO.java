package za.ac.cput.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.ac.cput.domain.Car;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private int id;
    private UserDTO user;
    private Car car;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private String status;
}

