package za.ac.cput.domain.dto.dual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private UUID uuid;
    private UserDTO user;
    private CarDTO car;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private String status;
}

