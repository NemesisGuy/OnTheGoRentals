package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(int userId);
    List<Booking> findByCarIdAndStatus(int carId, String status);
    List<Booking>   findByCarAndStatusAndBookingEndDateAfterAndBookingStartDateBefore(Car car, String status, LocalDateTime bookingEndDate, LocalDateTime bookingStartDate);


}

