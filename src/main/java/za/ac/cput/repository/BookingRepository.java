package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(int userId);

    List<Booking> findByCarIdAndStatus(int carId, String status);

    List<Booking> findByCarAndStatusAndBookingEndDateAfterAndBookingStartDateBefore(Car car, String status, LocalDateTime bookingEndDate, LocalDateTime bookingStartDate);


    //Booking findByCarId(int carId, String status);
    List<Booking> findBookingByUserId(int userId);

    Optional<Booking> findByIdAndDeletedFalse(int bookingId);

    List<Booking> findByUserIdAndDeletedFalse(int userId);

    List<Booking> findByDeletedFalse();

    List<Booking> findByCarIdAndStatusAndDeletedFalse(int carId, String confirmed);

    List<Booking> findByCarAndStatusAndBookingEndDateAfterAndBookingStartDateBeforeAndDeletedFalse(Car car, String confirmed, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Booking> findByUuidAndDeletedFalse(UUID id);
}

