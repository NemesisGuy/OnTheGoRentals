package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingUpdateDTO {
    // Admin can update these fields for an existing booking
    // Fields are optional; only provided fields will be updated.

    private UUID userUuid; // Can admin reassign booking to a different user?
    private UUID carUuid;  // Can admin change the car for a booking?
    private UUID driverUuid;

    @FutureOrPresent(message = "Booking start date must be in the present or future if provided")
    private LocalDateTime bookingStartDate;

    @FutureOrPresent(message = "Booking end date must be in the present or future if provided")
    private LocalDateTime bookingEndDate;

    private BookingStatus status;

//    private Integer issuerId;
//    private Integer receiverId; // e.g., when car is returned by admin/staff
//    private Double fine;*/
//
//    private LocalDateTime actualReturnedDate; // When the car is actually returned

}