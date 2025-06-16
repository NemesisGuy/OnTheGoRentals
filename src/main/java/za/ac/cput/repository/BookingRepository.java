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
    /**
     * Finds bookings by the user ID.
     *
     * @param userId The internal integer ID of the user.
     * @return A list of {@link Booking} entities associated with the specified user ID.
     */
    List<Booking> findByUserId(int userId);

    /**
     * Finds bookings by the car ID and status.
     *
     * @param carId  The internal integer ID of the car.
     * @param status The status of the booking (e.g., CONFIRMED, CANCELLED).
     * @return A list of {@link Booking} entities associated with the specified car ID and status.
     */
    List<Booking> findByCarIdAndStatus(int carId, String status);


    //Booking findByCarId(int carId, String status);

    /**
     * Finds bookings by the user ID.
     *
     * @param userId The internal integer ID of the user.
     * @return A list of {@link Booking} entities associated with the specified user ID that are not soft-deleted.
     */
    List<Booking> findBookingByUserId(int userId);

    /**
     * Finds a booking by its internal integer ID and ensures it is not soft-deleted.
     *
     * @param bookingId The internal integer ID of the booking.
     * @return An {@link Optional} containing the {@link Booking} entity if found and not soft-deleted, otherwise empty.
     */
    Optional<Booking> findByIdAndDeletedFalse(int bookingId);

    /**
     * Finds bookings by the user ID that are not soft-deleted.
     *
     * @param userId The internal integer ID of the user.
     * @return A list of {@link Booking} entities associated with the specified user ID that are not soft-deleted.
     */
    List<Booking> findByUserIdAndDeletedFalse(int userId);

    /**
     * Finds all bookings that are not soft-deleted.
     *
     * @return A list of all non-deleted {@link Booking} entities. Can be empty.
     */
    List<Booking> findByDeletedFalse();

    /**
     * Finds bookings by the car ID and status, ensuring they are not soft-deleted.
     *
     * @param carId     The internal integer ID of the car.
     * @param confirmed The status of the booking (e.g., CONFIRMED).
     * @return A list of {@link Booking} entities associated with the specified car ID and status that are not soft-deleted.
     */
    List<Booking> findByCarIdAndStatusAndDeletedFalse(int carId, String confirmed);

    /**
     * Finds bookings for a specific car that are in a given status and overlap with the specified date range.
     * The bookings must not be soft-deleted.
     *
     * @param car       The {@link Car} entity for which to find bookings.
     * @param confirmed The status of the booking (e.g., CONFIRMED).
     * @param startDate The start date of the period to check for overlaps.
     * @param endDate   The end date of the period to check for overlaps.
     * @return A list of {@link Booking} entities that match the criteria.
     */
    List<Booking> findByCarAndStatusAndEndDateAfterAndStartDateBeforeAndDeletedFalse(Car car, String confirmed, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds a booking by its UUID and ensures it is not soft-deleted.
     *
     * @param id The UUID of the booking.
     * @return An {@link Optional} containing the {@link Booking} entity if found and not soft-deleted, otherwise empty.
     */
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

    /**
     * Finds bookings by status and a date range, ensuring they are not soft-deleted.
     * This method retrieves bookings that match the specified status
     * and have a start date within the given range.
     * * @param bookingStatus The status of the booking (e.g., CONFIRMED).
     * * @param startOfDay    The start of the day for the date range.
     * * @param endOfDay      The end of the day for the date range.
     * * @return A list of {@link Booking} entities that match the criteria and are not soft-deleted.
     */
    List<Booking> findByStatusAndStartDateBetweenAndDeletedFalse(BookingStatus bookingStatus, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
