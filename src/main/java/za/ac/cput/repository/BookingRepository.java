package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(int userId);

    List<Booking> findByCarIdAndStatus(int carId, String status);


    //Booking findByCarId(int carId, String status);
    List<Booking> findBookingByUserId(int userId);

    Optional<Booking> findByIdAndDeletedFalse(int bookingId);

    List<Booking> findByUserIdAndDeletedFalse(int userId);

    List<Booking> findByDeletedFalse();

    List<Booking> findByCarIdAndStatusAndDeletedFalse(int carId, String confirmed);

    List<Booking> findByCarAndStatusAndEndDateAfterAndStartDateBeforeAndDeletedFalse(Car car, String confirmed, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Booking> findByUuidAndDeletedFalse(UUID id);


    /**
     * Finds bookings for a specific car that are in a given status (e.g., CONFIRMED)
     * and overlap with the proposed date range.
     * An overlap occurs if:
     * (booking.startDate < proposedEndDate) AND (booking.endDate > proposedStartDate)
     * This query also ensures that the booking is not soft-deleted.
     *
     * @param carId             The internal integer ID of the {@link za.ac.cput.domain.entity.Car}.
     * @param status            The {@link BookingStatus} to filter by (e.g., CONFIRMED).
     * @param proposedStartDate The start datetime of the period to check for overlaps.
     * @param proposedEndDate   The end datetime of the period to check for overlaps.
     * @return A list of {@link Booking} entities that overlap with the given criteria.
     */
    @Query("SELECT b FROM Booking b " +
            "WHERE b.car.id = :carId " +          // Filter by car ID
            "AND b.status = :status " +           // Filter by booking status
            "AND b.deleted = false " +            // Ensure booking is not soft-deleted
            "AND b.startDate < :proposedEndDate " + // Booking starts before the proposed period ends
            "AND b.endDate > :proposedStartDate")
    // Booking ends after the proposed period starts
    List<Booking> findOverlappingBookings(@Param("carId") Integer carId,
                                          @Param("status") BookingStatus status,
                                          @Param("proposedStartDate") LocalDateTime proposedStartDate,
                                          @Param("proposedEndDate") LocalDateTime proposedEndDate);

    List<Booking> findByStatusAndStartDateBetweenAndDeletedFalse(BookingStatus bookingStatus, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
